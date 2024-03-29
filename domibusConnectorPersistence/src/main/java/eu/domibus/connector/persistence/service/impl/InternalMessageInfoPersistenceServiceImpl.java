package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageInfoDao;
import eu.domibus.connector.persistence.model.*;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;


@Component
public class InternalMessageInfoPersistenceServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalMessageInfoPersistenceServiceImpl.class);

    private final DomibusConnectorMessageInfoDao messageInfoDao;
    private final DomibusConnectorPModePersistenceService pModeService;

    public InternalMessageInfoPersistenceServiceImpl(
            DomibusConnectorMessageInfoDao messageInfoDao, DomibusConnectorPModePersistenceService pModeService) {
        this.messageInfoDao = messageInfoDao;
        this.pModeService = pModeService;
    }

    public void persistMessageInfo(
            DomibusConnectorMessage message, PDomibusConnectorMessage dbMessage) throws PersistenceException {
        try {
            PDomibusConnectorMessageInfo dbMessageInfo = new PDomibusConnectorMessageInfo();
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
    private PDomibusConnectorMessageInfo validatePartyServiceActionOfMessageInfo(PDomibusConnectorMessageInfo messageInfo) throws
            PersistenceException {
        DomibusConnectorBusinessDomain.BusinessDomainId defaultBusinessDomainId =
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
        PDomibusConnectorAction dbAction = messageInfo.getAction();
        Optional<PDomibusConnectorAction> dbActionFound =
                pModeService.getConfiguredSingleDB(defaultBusinessDomainId, dbAction);
        checkNull(
                dbAction,
                dbActionFound,
                String.format("No action [%s] is configured at the connector!", dbAction.getId())
        );
        messageInfo.setAction(dbActionFound.get());

        PDomibusConnectorService dbService = messageInfo.getService();
        Optional<PDomibusConnectorService> dbServiceFound =
                pModeService.getConfiguredSingleDB(defaultBusinessDomainId, dbService);
        checkNull(
                dbService,
                dbServiceFound,
                String.format("No service [%s] is configured at the connector!", dbService.getId())
        );
        messageInfo.setService(dbServiceFound.get());

        PDomibusConnectorParty dbFromParty = messageInfo.getFrom();

        Optional<PDomibusConnectorParty> dbFromPartyFound =
                pModeService.getConfiguredSingleDB(defaultBusinessDomainId, dbFromParty);
        checkNull(
                dbFromParty,
                dbFromPartyFound,
                String.format("No party [%s] is configured at the connector!", dbFromParty)
        );
        messageInfo.setFrom(dbFromPartyFound.get());

        PDomibusConnectorParty dbToParty = messageInfo.getTo();

        Optional<PDomibusConnectorParty> dbToPartyFound =
                pModeService.getConfiguredSingleDB(defaultBusinessDomainId, dbToParty);
        checkNull(dbToParty, dbToPartyFound, String.format(
                "No party [%s] is configured at the connector!",
                dbToParty
        ));
        messageInfo.setTo(dbToPartyFound.get());

        return messageInfo;
    }

    private void checkNull(Object provided, Optional foundInDb, String errorMessage) throws PersistenceException {
        if (!foundInDb.isPresent()) {
            String error = String.format(
                    "%s [%s] is not configured in database!",
                    provided.getClass().getSimpleName(),
                    provided
            );

            LOGGER.error(
                    LoggingMarker.BUSINESS_LOG,
                    "{} Check your p-modes or reimport them into the connector.",
                    errorMessage
            );
            throw new PersistenceException(error);
        }
    }

    /**
     * maps all messageInfos into the message details
     * *) action
     * *) service
     * *) originalSender
     * *) finalRecipient
     * *) fromParty
     * *) toParty
     *
     * @param dbMessage the db message
     * @param details   the details, wich are changed
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

    public void mapMessageDetailsToDbMessageInfoPersistence(
            DomibusConnectorMessageDetails messageDetails, PDomibusConnectorMessageInfo dbMessageInfo) {
        PDomibusConnectorAction persistenceAction = ActionMapper.mapActionToPersistence(messageDetails.getAction());
        dbMessageInfo.setAction(persistenceAction);

        PDomibusConnectorService persistenceService =
                ServiceMapper.mapServiceToPersistence(messageDetails.getService());
        dbMessageInfo.setService(persistenceService);

        dbMessageInfo.setFinalRecipient(messageDetails.getFinalRecipient());
        dbMessageInfo.setOriginalSender(messageDetails.getOriginalSender());

        PDomibusConnectorParty from = PartyMapper.mapPartyToPersistence(messageDetails.getFromParty());
        dbMessageInfo.setFrom(from);
        PDomibusConnectorParty to = PartyMapper.mapPartyToPersistence(messageDetails.getToParty());
        dbMessageInfo.setTo(to);
    }

    public void mergeMessageInfo(DomibusConnectorMessage message, PDomibusConnectorMessage dbMessage) {
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
