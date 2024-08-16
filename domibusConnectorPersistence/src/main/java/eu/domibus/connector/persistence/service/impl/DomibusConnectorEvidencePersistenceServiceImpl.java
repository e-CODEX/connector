/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.persistence.dao.DomibusConnectorEvidenceDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.service.DomibusConnectorEvidencePersistenceService;
import eu.domibus.connector.persistence.service.exceptions.DuplicateEvidencePersistenceException;
import eu.domibus.connector.persistence.service.exceptions.EvidencePersistenceException;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import eu.domibus.connector.persistence.service.impl.helper.EvidenceTypeMapper;
import eu.domibus.connector.persistence.service.impl.helper.MapperHelper;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * The DomibusConnectorEvidencePersistenceServiceImpl class is responsible for handling evidence
 * persistence for DomibusConnectorMessage objects.
 */
@Service
public class DomibusConnectorEvidencePersistenceServiceImpl
    implements DomibusConnectorEvidencePersistenceService {
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
    public void setConfirmationAsTransportedToGateway(
        DomibusConnectorMessageConfirmation confirmation) {
        if (confirmation == null) {
            throw new IllegalArgumentException("The confirmation is not allowed to be null!");
        }
        if (confirmation.getEvidenceDbId() == null) {
            throw new IllegalArgumentException(
                "The confirmation must already be persisted into DB and also the evidenceDbId "
                    + "must not be null!");
        }
        evidenceDao.setEvidenceDeliveredToGateway(confirmation.getEvidenceDbId());
    }

    @Override
    public void setConfirmationAsTransportedToBackend(
        DomibusConnectorMessageConfirmation confirmation) {
        if (confirmation == null) {
            throw new IllegalArgumentException("The confirmation is not allowed to be null!");
        }
        if (confirmation.getEvidenceDbId() == null) {
            throw new IllegalArgumentException(
                "The confirmation must already be persisted into DB and also the evidenceDbId must "
                    + "not be null!");
        }
        evidenceDao.setEvidenceDeliveredToBackend(confirmation.getEvidenceDbId());
    }

    @Override
    public void persistEvidenceMessageToBusinessMessage(
        DomibusConnectorMessage businessMessage, DomibusConnectorMessageId transportId,
        DomibusConnectorMessageConfirmation confirmation) {
        String connectorMessageId = businessMessage.getConnectorMessageId().getConnectorMessageId();
        Optional<PDomibusConnectorMessage> optionalMessage =
            messageDao.findOneByConnectorMessageId(connectorMessageId);
        if (optionalMessage.isEmpty()) {
            var error = String.format(
                "Could not find business message with id [%s] within DB!",
                connectorMessageId
            );
            throw new PersistenceException(error);
        }
        PDomibusConnectorMessage oneByConnectorMessageId = optionalMessage.get();
        var dbEvidenceType =
            EvidenceTypeMapper.mapEvidenceTypeFromDomainToDb(confirmation.getEvidenceType());

        // check if the max occurrence count of the evidences are OK
        // see ETSI-REM standard for max-occurrence variable
        if (confirmation.getEvidenceType().getMaxOccurence() > 0) {
            List<PDomibusConnectorEvidence> byMessageAndEvidenceType =
                evidenceDao.findByMessageAndEvidenceType(oneByConnectorMessageId, dbEvidenceType);
            if (byMessageAndEvidenceType.size() >= confirmation.getEvidenceType().getMaxOccurence()
                &&
                byMessageAndEvidenceType != null) {
                var error = String.format(
                    "There is already a evidence persisted of type [%s] for message [%s]",
                    dbEvidenceType, oneByConnectorMessageId
                );
                throw new DuplicateEvidencePersistenceException(error);
            }
        }

        var dbEvidence = new PDomibusConnectorEvidence();

        oneByConnectorMessageId.getRelatedEvidences().add(dbEvidence);
        dbEvidence.setBusinessMessage(oneByConnectorMessageId);

        var evidenceXml = MapperHelper.convertByteArrayToString(confirmation.getEvidence());
        if (!StringUtils.hasLength(evidenceXml)) {
            throw new EvidencePersistenceException("Evidence string is not allowed to be null!");
        }
        dbEvidence.setEvidence(evidenceXml);
        dbEvidence.setType(dbEvidenceType);

        evidenceDao.save(dbEvidence);

        // set DB id
        confirmation.setEvidenceDbId(dbEvidence.getId());
        // set confirmation as related
        businessMessage.addRelatedMessageConfirmation(confirmation);
    }
}
