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

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageErrorDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageError;
import eu.domibus.connector.persistence.service.DomibusConnectorMessageErrorPersistenceService;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * The DomibusConnectorMessageErrorPersistenceServiceImpl class is an implementation of the
 * DomibusConnectorMessageErrorPersistenceService interface. It provides methods to interact with
 * the persistence layer for managing errors related to a DomibusConnectorMessage.
 */
@Service
public class DomibusConnectorMessageErrorPersistenceServiceImpl
    implements DomibusConnectorMessageErrorPersistenceService {
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
    @Transactional(propagation = REQUIRES_NEW) // run in new transaction...so error gets recorded
    public void persistMessageError(
        String connectorMessageId, DomibusConnectorMessageError messageError) {
        var dbError = new PDomibusConnectorMessageError();

        Optional<PDomibusConnectorMessage> msg =
            messageDao.findOneByConnectorMessageId(connectorMessageId);
        if (msg.isPresent()) {
            dbError.setMessage(msg.get());
            dbError.setErrorMessage(messageError.getText());
            dbError.setDetailedText(messageError.getDetails());
            dbError.setErrorSource(messageError.getSource());

            this.messageErrorDao.save(dbError);
        }
    }

    @Override
    public List<DomibusConnectorMessageError> getMessageErrors(DomibusConnectorMessage message)
        throws PersistenceException {
        Optional<PDomibusConnectorMessage> dbMessage =
            messageDao.findOneByConnectorMessageId(message.getConnectorMessageIdAsString());
        if (dbMessage.isEmpty()) {
            // no message reference
            return new ArrayList<>();
        }
        List<PDomibusConnectorMessageError> dbErrorsForMessage =
            this.messageErrorDao.findByMessageId(dbMessage.get().getId());
        if (!CollectionUtils.isEmpty(dbErrorsForMessage)) {
            List<DomibusConnectorMessageError> messageErrors =
                new ArrayList<>(dbErrorsForMessage.size());
            for (PDomibusConnectorMessageError dbMsgError : dbErrorsForMessage) {
                DomibusConnectorMessageError msgError =
                    DomibusConnectorMessageErrorBuilder.createBuilder()
                                                       .setSource(dbMsgError.getErrorSource())
                                                       .setText(dbMsgError.getErrorMessage())
                                                       .setDetails(dbMsgError.getDetailedText())
                                                       .build();
                messageErrors.add(msgError);
            }

            return messageErrors;
        }
        return new ArrayList<>();
    }
}
