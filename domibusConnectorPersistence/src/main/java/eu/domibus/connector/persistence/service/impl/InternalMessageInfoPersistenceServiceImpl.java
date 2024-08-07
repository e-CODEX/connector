/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageInfoDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageInfo;
import eu.domibus.connector.persistence.model.PDomibusConnectorParty;
import eu.domibus.connector.persistence.model.PDomibusConnectorService;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import eu.domibus.connector.tools.logging.LoggingMarker;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for persisting and retrieving message info in the database.
 */
@Component
public class InternalMessageInfoPersistenceServiceImpl {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(InternalMessageInfoPersistenceServiceImpl.class);
    private final DomibusConnectorMessageInfoDao messageInfoDao;
    private final DomibusConnectorPModePersistenceService connectorPModePersistenceService;

    public InternalMessageInfoPersistenceServiceImpl(
        DomibusConnectorMessageInfoDao messageInfoDao,
        DomibusConnectorPModePersistenceService connectorPModePersistenceService) {
        this.messageInfoDao = messageInfoDao;
        this.connectorPModePersistenceService = connectorPModePersistenceService;
    }

    /**
     * Persists the message information into the database.
     *
     * @param message   The message to persist the information from.
     * @param dbMessage The database message object to persist the information to.
     * @throws PersistenceException If an error occurs while persisting the message information.
     */
    public void persistMessageInfo(
        DomibusConnectorMessage message, PDomibusConnectorMessage dbMessage)
        throws PersistenceException {
        try {
            var dbMessageInfo = new PDomibusConnectorMessageInfo();
            dbMessageInfo.setMessage(dbMessage);
            dbMessageInfo.setCreated(new Date());
            dbMessageInfo.setUpdated(new Date());
            mapMessageDetailsToDbMessageInfoPersistence(message.getMessageDetails(), dbMessageInfo);

            this.validatePartyServiceActionOfMessageInfo(dbMessageInfo);

            dbMessageInfo = this.messageInfoDao.save(dbMessageInfo);
            dbMessageInfo = messageInfoDao.findById(dbMessageInfo.getId()).get();
            this.validatePartyServiceActionOfMessageInfo(dbMessageInfo);
            dbMessage.setMessageInfo(dbMessageInfo);

            mapMessageInfoIntoMessageDetails(dbMessage, message.getMessageDetails());
        } catch (Exception e) {
            throw new PersistenceException("Could not persist message info into database. ", e);
        }
    }

    /*
     * Looks up the Action, Service, Party within the database
     *  and replaces it with the corresponding persistence object
     *
     */
    private PDomibusConnectorMessageInfo validatePartyServiceActionOfMessageInfo(
        PDomibusConnectorMessageInfo messageInfo) throws PersistenceException {
        var defaultBusinessDomainId =
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
        PDomibusConnectorAction dbAction = messageInfo.getAction();
        Optional<PDomibusConnectorAction> dbActionFound =
            connectorPModePersistenceService.getConfiguredSingleDB(
                defaultBusinessDomainId, dbAction);
        checkNull(dbAction, dbActionFound,
                  String.format("No action [%s] is configured at the connector!", dbAction.getId())
        );
        messageInfo.setAction(dbActionFound.get());

        PDomibusConnectorService dbService = messageInfo.getService();
        Optional<PDomibusConnectorService> dbServiceFound =
            connectorPModePersistenceService.getConfiguredSingleDB(
                defaultBusinessDomainId, dbService);
        checkNull(dbService, dbServiceFound,
                  String.format(
                      "No service [%s] is configured at the connector!", dbService.getId())
        );
        messageInfo.setService(dbServiceFound.get());

        PDomibusConnectorParty dbFromParty = messageInfo.getFrom();

        Optional<PDomibusConnectorParty> dbFromPartyFound =
            connectorPModePersistenceService.getConfiguredSingleDB(
                defaultBusinessDomainId, dbFromParty);
        checkNull(dbFromParty, dbFromPartyFound,
                  String.format("No party [%s] is configured at the connector!", dbFromParty)
        );
        messageInfo.setFrom(dbFromPartyFound.get());

        PDomibusConnectorParty dbToParty = messageInfo.getTo();

        Optional<PDomibusConnectorParty> dbToPartyFound =
            connectorPModePersistenceService.getConfiguredSingleDB(
                defaultBusinessDomainId, dbToParty);
        checkNull(dbToParty, dbToPartyFound,
                  String.format("No party [%s] is configured at the connector!", dbToParty)
        );
        messageInfo.setTo(dbToPartyFound.get());

        return messageInfo;
    }

    private void checkNull(Object provided, Optional foundInDb, String errorMessage)
        throws PersistenceException {
        if (foundInDb.isEmpty()) {
            var error = String.format(
                "%s [%s] is not configured in database!",
                provided.getClass().getSimpleName(),
                provided
            );

            LOGGER.error(
                LoggingMarker.BUSINESS_LOG,
                "{} Check your p-modes or reimport them into the connector.", errorMessage
            );
            throw new PersistenceException(error);
        }
    }

    /**
     * Maps all messageInfos into the message details *) action *) service *) originalSender *)
     * finalRecipient *) fromParty *) toParty.
     *
     * @param dbMessage the db message
     * @param details   the details, which are changed
     * @return - the reference of the changed details (same reference as passed via param details)
     */
    public DomibusConnectorMessageDetails mapMessageInfoIntoMessageDetails(
        PDomibusConnectorMessage dbMessage, DomibusConnectorMessageDetails details) {
        PDomibusConnectorMessageInfo messageInfo = dbMessage.getMessageInfo();
        if (messageInfo != null) {

            PDomibusConnectorAction dbAction = messageInfo.getAction();
            DomibusConnectorAction action = ActionMapper.mapActionToDomain(dbAction);
            details.setAction(action);

            PDomibusConnectorService dbService = messageInfo.getService();
            DomibusConnectorService service = ServiceMapper.mapServiceToDomain(dbService);
            details.setService(service);

            details.setFinalRecipient(messageInfo.getFinalRecipient());
            details.setOriginalSender(messageInfo.getOriginalSender());

            PDomibusConnectorParty fromPartyDb = messageInfo.getFrom();
            DomibusConnectorParty fromParty = PartyMapper.mapPartyToDomain(fromPartyDb);
            LOGGER.trace("#mapMessageInfoIntoMessageDetails: set fromParty to [{}]", fromParty);
            details.setFromParty(fromParty);

            PDomibusConnectorParty dbToParty = messageInfo.getTo();
            DomibusConnectorParty toParty = PartyMapper.mapPartyToDomain(dbToParty);
            LOGGER.trace("#mapMessageInfoIntoMessageDetails: set toParty to [{}]", toParty);
            details.setToParty(toParty);
        }
        return details;
    }

    /**
     * Maps the message details from {@link DomibusConnectorMessageDetails} to
     * {@link PDomibusConnectorMessageInfo}.
     *
     * @param messageDetails the message details to be mapped
     * @param dbMessageInfo  the {@link PDomibusConnectorMessageInfo} object where the mapped values
     *                       will be set
     */
    public void mapMessageDetailsToDbMessageInfoPersistence(
        DomibusConnectorMessageDetails messageDetails, PDomibusConnectorMessageInfo dbMessageInfo) {
        PDomibusConnectorAction persistenceAction =
            ActionMapper.mapActionToPersistence(messageDetails.getAction());
        dbMessageInfo.setAction(persistenceAction);

        PDomibusConnectorService persistenceService =
            ServiceMapper.mapServiceToPersistence(messageDetails.getService());
        dbMessageInfo.setService(persistenceService);

        dbMessageInfo.setFinalRecipient(messageDetails.getFinalRecipient());
        dbMessageInfo.setOriginalSender(messageDetails.getOriginalSender());

        PDomibusConnectorParty from =
            PartyMapper.mapPartyToPersistence(messageDetails.getFromParty());
        dbMessageInfo.setFrom(from);
        PDomibusConnectorParty to = PartyMapper.mapPartyToPersistence(messageDetails.getToParty());
        dbMessageInfo.setTo(to);
    }

    /**
     * Merges the message information of the given DomibusConnectorMessage object into the
     * PDomibusConnectorMessage object.
     *
     * <p>If the messageInfo field in the PDomibusConnectorMessage is null, it creates a new
     * PDomibusConnectorMessageInfo object and sets it in the PDomibusConnectorMessage.
     *
     * <p>If the messageDetails field in the DomibusConnectorMessage is not null, it maps the
     * values from the messageDetails to the corresponding fields in the
     * PDomibusConnectorMessageInfo, and saves the PDomibusConnectorMessageInfo object using the
     * messageInfoDao.
     *
     * @param message   the DomibusConnectorMessage object to merge from
     * @param dbMessage the PDomibusConnectorMessage object to merge to
     */
    public void mergeMessageInfo(
        DomibusConnectorMessage message, PDomibusConnectorMessage dbMessage) {
        PDomibusConnectorMessageInfo messageInfo = dbMessage.getMessageInfo();
        if (messageInfo == null) {
            messageInfo = new PDomibusConnectorMessageInfo();
            dbMessage.setMessageInfo(messageInfo);
        }

        DomibusConnectorMessageDetails messageDetails = message.getMessageDetails();

        if (messageDetails != null) {
            // this.internalMessageInfoPersistenceService.mergeMessageInfo(message, dbMessage);
            mapMessageDetailsToDbMessageInfoPersistence(message.getMessageDetails(), messageInfo);
            messageInfoDao.save(messageInfo);
        }
    }
}
