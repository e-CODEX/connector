/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import eu.ecodex.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDetails;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.ecodex.connector.domain.model.helper.DomainModelHelper;
import eu.ecodex.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.ecodex.connector.persistence.model.PDomibusConnectorMessage;
import eu.ecodex.connector.persistence.model.PDomibusConnectorMessageInfo;
import eu.ecodex.connector.persistence.service.DCMessagePersistenceService;
import eu.ecodex.connector.persistence.service.exceptions.PersistenceException;
import eu.ecodex.connector.persistence.service.impl.helper.MessageDirectionMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

/**
 * DCMessagePersistenceServiceImpl is an implementation of the DCMessagePersistenceService
 * interface. It provides methods for persisting and retrieving DomibusConnectorMessage objects from
 * the database.
 */
@SuppressWarnings({"WeakerAccess", "squid:S1135"})
@org.springframework.stereotype.Service("persistenceService")
public class DCMessagePersistenceServiceImpl implements DCMessagePersistenceService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DCMessagePersistenceServiceImpl.class);
    private final DomibusConnectorMessageDao messageDao;
    private final MsgContentPersistenceService msgContentService;
    private final InternalMessageInfoPersistenceServiceImpl internalMessageInfoPersistenceService;

    /**
     * This class provides the implementation for persisting and retrieving messages in the
     * database.
     */
    public DCMessagePersistenceServiceImpl(
        DomibusConnectorMessageDao messageDao,
        MsgContentPersistenceService msgContentService,
        InternalMessageInfoPersistenceServiceImpl internalMessageInfoPersistenceService) {
        this.messageDao = messageDao;
        this.msgContentService = msgContentService;
        this.internalMessageInfoPersistenceService = internalMessageInfoPersistenceService;
    }

    @Override
    public DomibusConnectorMessage findMessageByConnectorMessageId(String connectorMessageId) {
        PDomibusConnectorMessage dbMessage =
            messageDao.findOneByConnectorMessageId(connectorMessageId).orElse(null);
        return mapMessageToDomain(dbMessage);
    }

    @Override
    public Optional<DomibusConnectorMessage> findMessageByEbmsIdAndDirection(
        String ebmsMessageId, DomibusConnectorMessageDirection messageDirection) {
        return messageDao.findOneByEbmsMessageIdAndDirectionTarget(
                             ebmsMessageId, messageDirection.getTarget())
                         .map(this::mapMessageToDomain);
    }

    @Override
    public Optional<DomibusConnectorMessage> findMessageByNationalIdAndDirection(
        String nationalMessageId, DomibusConnectorMessageDirection messageDirection) {
        return messageDao.findOneByBackendMessageIdAndDirectionTarget(
                             nationalMessageId, messageDirection.getTarget())
                         .map(this::mapMessageToDomain);
    }

    @Override
    public Optional<DomibusConnectorMessage> findMessageByEbmsIdOrBackendIdAndDirection(
        String messageId, DomibusConnectorMessageDirection messageDirection) {
        return messageDao.findOneByEbmsMessageIdOrBackendMessageIdAndDirectionTarget(
                             messageId, messageDirection.getTarget())
                         .map(this::mapMessageToDomain);
    }

    @Override
    public boolean checkMessageConfirmedOrRejected(DomibusConnectorMessageId messageId) {
        return messageDao.checkMessageConfirmedOrRejected(messageId.getConnectorMessageId());
    }

    @Override
    public boolean checkMessageRejected(DomibusConnectorMessage message) {
        PDomibusConnectorMessage dbMessage = this.findMessageByMessage(message);
        return this.messageDao.checkMessageRejected(dbMessage.getId());
    }

    @Override
    public boolean checkMessageConfirmed(DomibusConnectorMessage message) {
        PDomibusConnectorMessage dbMessage = this.findMessageByMessage(message);
        return this.messageDao.checkMessageConfirmed(dbMessage.getId());
    }

    @Override
    public void persistBusinessMessageIntoDatabase(DomibusConnectorMessage message) {
        if (message.getMessageDetails() == null) {
            throw new IllegalArgumentException(
                "MessageDetails (getMessageDetails()) are not allowed to be null in message!");
        }
        if (message.getConnectorMessageId() == null) {
            throw new IllegalArgumentException(
                "connectorMessageId (getConnectorMessageId()) must be set!");
        }

        DomibusConnectorMessageDirection direction = message.getMessageDetails().getDirection();
        LOGGER.trace(
            "#persistMessageIntoDatabase: Persist message [{}] with direction [{}] into storage",
            message, direction
        );
        var dbMessage = new PDomibusConnectorMessage();

        dbMessage.setDirectionSource(direction.getSource());
        dbMessage.setDirectionTarget(direction.getTarget());

        dbMessage.setConversationId(message.getMessageDetails().getConversationId());
        dbMessage.setEbmsMessageId(message.getMessageDetails().getEbmsMessageId());
        dbMessage.setBackendMessageId(message.getMessageDetails().getBackendMessageId());
        dbMessage.setConnectorMessageId(message.getConnectorMessageId().getConnectorMessageId());
        dbMessage.setBackendName(message.getMessageDetails().getConnectorBackendClientName());
        dbMessage.setGatewayName(message.getMessageDetails().getGatewayName());

        try {
            LOGGER.trace(
                "#persistMessageIntoDatabase: Saving message [{}] into storage", dbMessage);
            dbMessage = messageDao.save(dbMessage);
        } catch (DuplicateKeyException cve) {
            var error = String.format(
                "Message already persisted! The domibusConnectorMessageId [%s] already exist.",
                dbMessage.getConnectorMessageId()
            );
            LOGGER.error(error);
            throw new PersistenceException(error, cve);
        }

        this.internalMessageInfoPersistenceService.persistMessageInfo(message, dbMessage);
        this.msgContentService.saveMessagePayloads(message, dbMessage);
    }

    /**
     * Persists a DomibusConnectorMessage into the database.
     *
     * @param message   The DomibusConnectorMessage to be persisted.
     * @param direction The direction of the message.
     * @return The persisted DomibusConnectorMessage.
     * @throws PersistenceException     If an error occurs during persistence.
     * @throws IllegalArgumentException if message.getMessageDetails() is null or
     *                                  message.getConnectorMessageId() is null.
     * @deprecated This method is deprecated.
     */
    @Override
    @Deprecated
    @Transactional
    public DomibusConnectorMessage persistMessageIntoDatabase(
        @Nonnull DomibusConnectorMessage message,
        @Nonnull DomibusConnectorMessageDirection direction) throws PersistenceException {
        if (message.getMessageDetails() == null) {
            throw new IllegalArgumentException(
                "MessageDetails (getMessageDetails()) are not allowed to be null in message!");
        }
        if (message.getConnectorMessageId() == null) {
            throw new IllegalArgumentException(
                "connectorMessageId (getConnectorMessageId()) must be set!");
        }

        message.getMessageDetails().setDirection(direction);

        LOGGER.trace(
            "#persistMessageIntoDatabase: Persist message [{}] with direction [{}] into storage",
            message, direction
        );
        var dbMessage = new PDomibusConnectorMessage();

        dbMessage.setDirectionSource(direction.getSource());
        dbMessage.setDirectionTarget(direction.getTarget());

        dbMessage.setConversationId(message.getMessageDetails().getConversationId());
        dbMessage.setEbmsMessageId(message.getMessageDetails().getEbmsMessageId());
        dbMessage.setBackendMessageId(message.getMessageDetails().getBackendMessageId());
        dbMessage.setConnectorMessageId(message.getConnectorMessageId().getConnectorMessageId());
        dbMessage.setBackendName(message.getMessageDetails().getConnectorBackendClientName());
        dbMessage.setGatewayName(message.getMessageDetails().getGatewayName());

        try {
            LOGGER.trace(
                "#persistMessageIntoDatabase: Saving message [{}] into storage", dbMessage);
            dbMessage = messageDao.save(dbMessage);
        } catch (DuplicateKeyException cve) {
            var error = String.format(
                "Message already persisted! The domibusConnectorMessageId [%s] already exist.",
                dbMessage.getConnectorMessageId()
            );
            LOGGER.error(error);
            throw new PersistenceException(error, cve);
        }

        this.msgContentService.saveMessagePayloads(message, dbMessage);

        this.internalMessageInfoPersistenceService.persistMessageInfo(message, dbMessage);

        return message;
    }

    /**
     * Tries to find a message by domibusConnectorMessageId.
     *
     * @param message - the message to which the storage data should be found, message need a
     *                domibusConnectorMessageId set
     * @return the found message or null
     */
    PDomibusConnectorMessage findMessageByMessage(@Nonnull DomibusConnectorMessage message) {
        var connectorMessageId = message.getConnectorMessageIdAsString();
        Optional<PDomibusConnectorMessage> dbMessage =
            messageDao.findOneByConnectorMessageId(connectorMessageId);
        if (dbMessage.isEmpty()) {
            LOGGER.warn("No message found with connector message id [{}] ", connectorMessageId);
        }
        return dbMessage.orElse(null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @Transactional
    public DomibusConnectorMessage mergeMessageWithDatabase(
        @Nonnull DomibusConnectorMessage message) throws PersistenceException {
        if (DomainModelHelper.isEvidenceMessage(message)) {
            LOGGER.debug(
                "#mergeMessageWithDatabase: message is an evidence message, doing nothing!");
            return message;
        }
        PDomibusConnectorMessage dbMessage = findMessageByMessage(message);
        if (dbMessage == null) {
            var error = String.format(
                "No db message found for domain message %s in storage!%n Can only merge a message "
                    + "which has already been persisted \nThrowing exception!", message);
            LOGGER.error(error);
            throw new PersistenceException(error);
        }

        dbMessage.setConversationId(message.getMessageDetails().getConversationId());
        dbMessage.setEbmsMessageId(message.getMessageDetails().getEbmsMessageId());
        dbMessage.setBackendMessageId(message.getMessageDetails().getBackendMessageId());
        dbMessage.setConnectorMessageId(message.getConnectorMessageIdAsString());
        dbMessage.setBackendName(message.getMessageDetails().getConnectorBackendClientName());
        dbMessage.setGatewayName(message.getMessageDetails().getGatewayName());

        PDomibusConnectorMessageInfo messageInfo = dbMessage.getMessageInfo();
        if (messageInfo == null) {
            messageInfo = new PDomibusConnectorMessageInfo();
            dbMessage.setMessageInfo(messageInfo);
        }

        DomibusConnectorMessageDetails messageDetails = message.getMessageDetails();

        if (messageDetails != null) {
            this.internalMessageInfoPersistenceService.mergeMessageInfo(message, dbMessage);
            // mapMessageDetailsToDbMessageInfoPersistence(
            // message.getMessageDetails(), messageInfo);
            // messageInfoDao.save(messageInfo);
        }

        this.msgContentService.saveMessagePayloads(message, dbMessage);
        mapRelatedConfirmations(dbMessage, message);

        this.messageDao.save(dbMessage);

        return message;
    }

    private void mapRelatedConfirmations(
        PDomibusConnectorMessage dbMessage, DomibusConnectorMessage message) {
        var collect = message
            .getRelatedMessageConfirmations()
            .stream()
            .map(
                MessageConfirmationMapper::mapFromDomainToDb)
            .toList();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @Transactional
    public void setDeliveredToGateway(DomibusConnectorMessage message) {
        LOGGER.trace("#setDeliveredToGateway: with message [{}]", message);
        Optional<PDomibusConnectorMessage> dbMessage;

        String connectorMessageId = message.getConnectorMessageId().getConnectorMessageId();
        dbMessage = messageDao.findOneByConnectorMessageId(connectorMessageId);
        if (dbMessage.isPresent()) {
            LOGGER.trace(
                "#setDeliveredToGateway: set connectorId [{}] as delivered in db",
                connectorMessageId
            );
            messageDao.setMessageDeliveredToGateway(dbMessage.get());
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @Transactional
    public void setMessageDeliveredToNationalSystem(DomibusConnectorMessage message) {
        Optional<PDomibusConnectorMessage> dbMessage;

        String connectorMessageId = message.getConnectorMessageId().getConnectorMessageId();
        dbMessage = messageDao.findOneByConnectorMessageId(connectorMessageId);
        if (dbMessage.isPresent()) {
            LOGGER.trace(
                "#setMessageDeliveredToNationalSystem: set connectorId [{}] as delivered in db",
                connectorMessageId
            );
            messageDao.setMessageDeliveredToBackend(dbMessage.get());
        }
    }

    @Override
    @Transactional
    public void updateMessageDetails(DomibusConnectorMessage message) {
        PDomibusConnectorMessage dbMessage = this.findMessageByMessage(message);

        dbMessage.setConversationId(message.getMessageDetails().getConversationId());
        dbMessage.setEbmsMessageId(message.getMessageDetails().getEbmsMessageId());
        dbMessage.setBackendMessageId(message.getMessageDetails().getBackendMessageId());

        dbMessage.setBackendName(message.getMessageDetails().getConnectorBackendClientName());
        dbMessage.setGatewayName(message.getMessageDetails().getGatewayName());

        this.internalMessageInfoPersistenceService.mergeMessageInfo(message, dbMessage);
        messageDao.save(dbMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomibusConnectorMessage> findMessagesByConversationId(String conversationId) {
        List<PDomibusConnectorMessage> dbMessages = messageDao.findByConversationId(conversationId);
        return mapDBMessagesToDTO(dbMessages);
    }

    private List<DomibusConnectorMessage> mapDBMessagesToDTO(
        List<PDomibusConnectorMessage> dbMessages) {
        if (dbMessages != null && !dbMessages.isEmpty()) {
            List<DomibusConnectorMessage> messages = new ArrayList<>(dbMessages.size());
            for (PDomibusConnectorMessage dbMessage : dbMessages) {
                messages.add(mapMessageToDomain(dbMessage));
            }
            return messages;
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomibusConnectorMessage> findOutgoingUnconfirmedMessages() {
        List<PDomibusConnectorMessage> dbMessages = messageDao.findOutgoingUnconfirmedMessages();
        return mapDBMessagesToDTO(dbMessages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomibusConnectorMessage>
    findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery() {
        List<PDomibusConnectorMessage> dbMessages =
            messageDao.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery();
        return mapDBMessagesToDTO(dbMessages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomibusConnectorMessage>
    findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD() {
        List<PDomibusConnectorMessage> dbMessages =
            messageDao.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();
        return mapDBMessagesToDTO(dbMessages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomibusConnectorMessage> findIncomingUnconfirmedMessages() {
        List<PDomibusConnectorMessage> dbMessages = messageDao.findIncomingUnconfirmedMessages();
        return mapDBMessagesToDTO(dbMessages);
    }

    @Override
    @Transactional
    public void confirmMessage(DomibusConnectorMessage message) {
        if (message == null) {
            throw new IllegalArgumentException(
                "Argument message must be not null! Cannot confirm null!");
        }
        var confirmedDate = ZonedDateTime.now();
        message.getMessageDetails().setConfirmed(confirmedDate);
        PDomibusConnectorMessage dbMessage = this.findMessageByMessage(message);
        if (dbMessage == null) {
            throw new IllegalArgumentException(
                "Message must be already persisted to database! Call persistMessageIntoDatabase "
                    + "first"
            );
        }
        int confirmed = messageDao.confirmMessage(dbMessage.getId(), confirmedDate);
        if (confirmed == 1) {
            LOGGER.debug("Message {} successfully confirmed in db", message);
        } else if (confirmed < 1) {
            throw new PersistenceException("message not confirmed!");
        } else {
            throw new IllegalStateException(
                "Multiple messages confirmed! This should not happen! Maybe DB corrupted? "
                    + "Duplicate IDs?"
            );
        }
    }

    @Override
    @Transactional
    public void rejectMessage(DomibusConnectorMessage message) {
        if (message == null) {
            throw new IllegalArgumentException(
                "Argument message must be not null! Cannot reject null!");
        }
        var confirmedDate = ZonedDateTime.now();
        message.getMessageDetails().setRejected(confirmedDate);
        PDomibusConnectorMessage dbMessage = this.findMessageByMessage(message);
        if (dbMessage == null) {
            throw new IllegalArgumentException(
                "Message must be already persisted to database! Call persistMessageIntoDatabase "
                    + "message first!"
            );
        }
        int rejected = messageDao.rejectMessage(dbMessage.getId());
        if (rejected == 1) {
            LOGGER.debug("Message {} successfully marked as rejected in persistence", message);
        } else if (rejected < 1) {
            throw new PersistenceException("message not confirmed!");
        } else {
            throw new IllegalStateException(
                "Multiple messages marked as rejected! This should not happen! Maybe DB corrupted?"
                    + " Duplicate IDs?");
        }
    }

    /**
     * Maps database messages (PDomibusConnectorMessage) to the according representation in Domain
     * layer (Message).
     *
     * @param dbMessage - the database message
     * @return - the mapped message
     */
    @Nullable
    DomibusConnectorMessage mapMessageToDomain(PDomibusConnectorMessage dbMessage) {
        if (dbMessage == null) {
            return null;
        }

        var details = new DomibusConnectorMessageDetails();
        details.setEbmsMessageId(dbMessage.getEbmsMessageId());
        details.setBackendMessageId(dbMessage.getBackendMessageId());
        details.setConversationId(dbMessage.getConversationId());

        details.setConnectorBackendClientName(dbMessage.getBackendName());
        details.setGatewayName(dbMessage.getGatewayName());

        details.setDeliveredToBackend(dbMessage.getDeliveredToNationalSystem());
        details.setDeliveredToGateway(dbMessage.getDeliveredToGateway());

        details.setDirection(
            MessageDirectionMapper.mapFromPersistenceToDomain(
                dbMessage.getDirectionSource(),
                dbMessage.getDirectionTarget()
            ));

        details.setRejected(dbMessage.getRejected());
        details.setConfirmed(dbMessage.getConfirmed());

        this.internalMessageInfoPersistenceService.mapMessageInfoIntoMessageDetails(
            dbMessage, details);

        var messageBuilder = DomibusConnectorMessageBuilder.createBuilder();
        messageBuilder.setMessageDetails(details);
        messageBuilder.setConnectorMessageId(dbMessage.getConnectorMessageId());
        messageBuilder.setMessageLaneId(
            // TODO: replace with value from DB!
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId()
        );

        this.msgContentService.loadMessagePayloads(messageBuilder, dbMessage);

        loadRelatedEvidences(messageBuilder, dbMessage);

        return messageBuilder.build();
    }

    private void loadRelatedEvidences(
        DomibusConnectorMessageBuilder messageBuilder, PDomibusConnectorMessage dbMessage) {
        List<DomibusConnectorMessageConfirmation> collect = dbMessage
            .getRelatedEvidences().stream()
            .map(
                MessageConfirmationMapper::mapFromDbToDomain)
            .toList();
        messageBuilder.addRelatedConfirmations(collect);
    }
}
