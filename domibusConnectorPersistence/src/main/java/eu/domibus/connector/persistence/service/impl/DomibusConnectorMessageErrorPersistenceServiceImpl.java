package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageErrorDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageError;
import eu.domibus.connector.persistence.service.DomibusConnectorMessageErrorPersistenceService;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;


@Service
public class DomibusConnectorMessageErrorPersistenceServiceImpl implements DomibusConnectorMessageErrorPersistenceService {
    DomibusConnectorMessageErrorDao messageErrorDao;
    DomibusConnectorMessageDao messageDao;

    @Autowired
    public void setMessageErrorDao(DomibusConnectorMessageErrorDao messageErrorDao) {
        this.messageErrorDao = messageErrorDao;
    }

    @Autowired
    public void setMessageDao(DomibusConnectorMessageDao messageDao) {
        this.messageDao = messageDao;
    }
    @Override
    public List<DomibusConnectorMessageError> getMessageErrors(DomibusConnectorMessage message)
            throws PersistenceException {
        Optional<PDomibusConnectorMessage> dbMessage =
                messageDao.findOneByConnectorMessageId(message.getConnectorMessageIdAsString());
        if (!dbMessage.isPresent()) {
            // no message reference
            return new ArrayList<>();
        }
        List<PDomibusConnectorMessageError> dbErrorsForMessage =
                this.messageErrorDao.findByMessage(dbMessage.get().getId());
        if (!CollectionUtils.isEmpty(dbErrorsForMessage)) {
            List<DomibusConnectorMessageError> messageErrors = new ArrayList<>(dbErrorsForMessage.size());
            for (PDomibusConnectorMessageError dbMsgError : dbErrorsForMessage) {
                DomibusConnectorMessageError msgError = DomibusConnectorMessageErrorBuilder
                        .createBuilder().setSource(dbMsgError.getErrorSource())
                        .setText(dbMsgError.getErrorMessage())
                        .setDetails(dbMsgError.getDetailedText()).build();
                messageErrors.add(msgError);
            }

            return messageErrors;
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW) // run in new transaction...so error gets recorded
    public void persistMessageError(String connectorMessageId, DomibusConnectorMessageError messageError) {
        PDomibusConnectorMessageError dbError = new PDomibusConnectorMessageError();

        Optional<PDomibusConnectorMessage> msg = messageDao.findOneByConnectorMessageId(connectorMessageId);
        if (msg.isPresent()) {
            dbError.setMessage(msg.get());
            dbError.setErrorMessage(messageError.getText());
            dbError.setDetailedText(messageError.getDetails());
            dbError.setErrorSource(messageError.getSource());

            this.messageErrorDao.save(dbError);
        }
    }
}
