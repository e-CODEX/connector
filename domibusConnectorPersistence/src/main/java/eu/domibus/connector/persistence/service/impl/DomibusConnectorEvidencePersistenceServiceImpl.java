package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.persistence.dao.DomibusConnectorEvidenceDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import eu.domibus.connector.persistence.service.DomibusConnectorEvidencePersistenceService;
import eu.domibus.connector.persistence.service.exceptions.DuplicateEvidencePersistenceException;
import eu.domibus.connector.persistence.service.exceptions.EvidencePersistenceException;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import eu.domibus.connector.persistence.service.impl.helper.EvidenceTypeMapper;
import eu.domibus.connector.persistence.service.impl.helper.MapperHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class DomibusConnectorEvidencePersistenceServiceImpl implements DomibusConnectorEvidencePersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorEvidencePersistenceServiceImpl.class);

    private DomibusConnectorEvidenceDao evidenceDao;
    private DomibusConnectorMessageDao messageDao;

    @Autowired
    public void setEvidenceDao(DomibusConnectorEvidenceDao evidenceDao) {
        this.evidenceDao = evidenceDao;
    }

    @Autowired
    public void setMessageDao(DomibusConnectorMessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public void setConfirmationAsTransportedToGateway(DomibusConnectorMessageConfirmation confirmation) {
        if (confirmation == null) {
            throw new IllegalArgumentException("The confirmation is not allowed to be null!");
        }
        if (confirmation.getEvidenceDbId() == null) {
            throw new IllegalArgumentException("The confirmation must already be persisted into DB and also the evidenceDbId must not be null!");
        }
        evidenceDao.setEvidenceDeliveredToGateway(confirmation.getEvidenceDbId());
    }

    @Override
    public void setConfirmationAsTransportedToBackend(DomibusConnectorMessageConfirmation confirmation) {
        if (confirmation == null) {
            throw new IllegalArgumentException("The confirmation is not allowed to be null!");
        }
        if (confirmation.getEvidenceDbId() == null) {
            throw new IllegalArgumentException("The confirmation must already be persisted into DB and also the evidenceDbId must not be null!");
        }
        evidenceDao.setEvidenceDeliveredToBackend(confirmation.getEvidenceDbId());
    }

    @Override
    public void persistEvidenceMessageToBusinessMessage(DomibusConnectorMessage businessMessage, DomibusConnectorMessageId transportId, DomibusConnectorMessageConfirmation confirmation) {
        String connectorMessageId = businessMessage.getConnectorMessageId().getConnectorMessageId();
        Optional<PDomibusConnectorMessage> optionalMessage = messageDao.findOneByConnectorMessageId(connectorMessageId);
        if (!optionalMessage.isPresent()) {
            String error = String.format("Could not find business message with id [%s] within DB!", connectorMessageId);
            throw new PersistenceException(error);
        }
        PDomibusConnectorMessage oneByConnectorMessageId = optionalMessage.get();
        EvidenceType dbEvidenceType = EvidenceTypeMapper.mapEvidenceTypeFromDomainToDb(confirmation.getEvidenceType());

        //check if the max occurence count of the evidences are OK
        //see ETSI-REM standard for max-occurence variable
        if (confirmation.getEvidenceType().getMaxOccurence() > 0) {
            List<PDomibusConnectorEvidence> byMessageAndEvidenceType = evidenceDao.findByMessageAndEvidenceType(oneByConnectorMessageId, dbEvidenceType);
            if (byMessageAndEvidenceType.size() >= confirmation.getEvidenceType().getMaxOccurence() &&
                    byMessageAndEvidenceType != null) {
                String error = String.format("There is already a evidence persisted of type [%s] for message [%s]", dbEvidenceType, oneByConnectorMessageId);
                throw new DuplicateEvidencePersistenceException(error);
            }
        }

        PDomibusConnectorEvidence dbEvidence = new PDomibusConnectorEvidence();

        oneByConnectorMessageId.getRelatedEvidences().add(dbEvidence);
        dbEvidence.setBusinessMessage(oneByConnectorMessageId);

        String evidenceXml = MapperHelper.convertByteArrayToString(confirmation.getEvidence());
        if (StringUtils.isEmpty(evidenceXml)) {
            throw new EvidencePersistenceException("Evidence string is not allowed to be null!");
        }
        dbEvidence.setEvidence(evidenceXml);
        dbEvidence.setType(dbEvidenceType);

        evidenceDao.save(dbEvidence);

        //set DB id
        confirmation.setEvidenceDbId(dbEvidence.getId());
        //set confirmation as related
        businessMessage.addRelatedMessageConfirmation(confirmation);
    }
}
