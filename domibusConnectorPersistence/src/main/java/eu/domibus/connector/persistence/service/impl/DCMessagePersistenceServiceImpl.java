package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.model.*;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import eu.domibus.connector.persistence.service.impl.helper.MessageDirectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@org.springframework.stereotype.Service("persistenceService")
public class DCMessagePersistenceServiceImpl implements DCMessagePersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DCMessagePersistenceServiceImpl.class);

    private final DomibusConnectorMessageDao messageDao;
    private final MsgContentPersistenceService msgContentService;
    private final InternalMessageInfoPersistenceServiceImpl internalMessageInfoPersistenceService;

    public DCMessagePersistenceServiceImpl(DomibusConnectorMessageDao messageDao,
                                           MsgContentPersistenceService msgContentService,
                                           InternalMessageInfoPersistenceServiceImpl internalMessageInfoPersistenceService) {
        this.messageDao = messageDao;
        this.msgContentService = msgContentService;
        this.internalMessageInfoPersistenceService = internalMessageInfoPersistenceService;
    }


    @Override
    public DomibusConnectorMessage findMessageByConnectorMessageId(String connectorMessageId) {
        PDomibusConnectorMessage dbMessage = messageDao.findOneByConnectorMessageId(connectorMessageId).orElse(null);
        return mapMessageToDomain(dbMessage);
    }

    @Override
    public Optional<DomibusConnectorMessage> findMessageByEbmsIdAndDirection(String ebmsMessageId, DomibusConnectorMessageDirection messageDirection) {
        return messageDao.findOneByEbmsMessageIdAndDirectionTarget(ebmsMessageId, messageDirection.getTarget())
               .map(this::mapMessageToDomain);
    }


    @Override
    public Optional<DomibusConnectorMessage> findMessageByNationalIdAndDirection(String nationalMessageId, DomibusConnectorMessageDirection messageDirection) {
        return messageDao.findOneByBackendMessageIdAndDirectionTarget(nationalMessageId, messageDirection.getTarget())
                .map(this::mapMessageToDomain);
    }

    @Override
    public Optional<DomibusConnectorMessage> findMessageByEbmsIdOrBackendIdAndDirection(String messageId, DomibusConnectorMessageDirection messageDirection) {
        return messageDao.findOneByEbmsMessageIdOrBackendMessageIdAndDirectionTarget(messageId, messageDirection.getTarget())
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
            throw new IllegalArgumentException("MessageDetails (getMessageDetails()) are not allowed to be null in message!");
        }
        if (message.getConnectorMessageId() == null) {
            throw new IllegalArgumentException("connectorMessageId (getConnectorMessageId()) must be set!");
        }

        DomibusConnectorMessageDirection direction = message.getMessageDetails().getDirection();
        LOGGER.trace("#persistMessageIntoDatabase: Persist message [{}] with direction [{}] into storage", message, direction);
        PDomibusConnectorMessage dbMessage = new PDomibusConnectorMessage();

        dbMessage.setDirectionSource(direction.getSource());
        dbMessage.setDirectionTarget(direction.getTarget());

        dbMessage.setConversationId(message.getMessageDetails().getConversationId());
        dbMessage.setEbmsMessageId(message.getMessageDetails().getEbmsMessageId());
        dbMessage.setBackendMessageId(message.getMessageDetails().getBackendMessageId());
        dbMessage.setConnectorMessageId(message.getConnectorMessageId().getConnectorMessageId());
        dbMessage.setBackendName(message.getMessageDetails().getConnectorBackendClientName());
        dbMessage.setGatewayName(message.getMessageDetails().getGatewayName());

        try {
            LOGGER.trace("#persistMessageIntoDatabase: Saving message [{}] into storage", dbMessage);
            dbMessage = messageDao.save(dbMessage);
        } catch (DuplicateKeyException cve) {
            String error = String.format("Message already persisted! The domibusConnectorMessageId [%s] already exist.",
                    dbMessage.getConnectorMessageId());
            LOGGER.error(error);
            throw new PersistenceException(error, cve);
        }

        this.internalMessageInfoPersistenceService.persistMessageInfo(message, dbMessage);
        this.msgContentService.saveMessagePayloads(message, dbMessage);
    }


    @Override
    @Deprecated
    @Transactional
    public DomibusConnectorMessage persistMessageIntoDatabase(@Nonnull DomibusConnectorMessage message, @Nonnull DomibusConnectorMessageDirection direction) throws PersistenceException {
        if (message.getMessageDetails() == null) {
            throw new IllegalArgumentException("MessageDetails (getMessageDetails()) are not allowed to be null in message!");
        }
        if (message.getConnectorMessageId() == null) {
            throw new IllegalArgumentException("connectorMessageId (getConnectorMessageId()) must be set!");
        }

        message.getMessageDetails().setDirection(direction);

        LOGGER.trace("#persistMessageIntoDatabase: Persist message [{}] with direction [{}] into storage", message, direction);
        PDomibusConnectorMessage dbMessage = new PDomibusConnectorMessage();

        dbMessage.setDirectionSource(direction.getSource());
        dbMessage.setDirectionTarget(direction.getTarget());

        dbMessage.setConversationId(message.getMessageDetails().getConversationId());
        dbMessage.setEbmsMessageId(message.getMessageDetails().getEbmsMessageId());
        dbMessage.setBackendMessageId(message.getMessageDetails().getBackendMessageId());
        dbMessage.setConnectorMessageId(message.getConnectorMessageId().getConnectorMessageId());
        dbMessage.setBackendName(message.getMessageDetails().getConnectorBackendClientName());
        dbMessage.setGatewayName(message.getMessageDetails().getGatewayName());

        try {
            LOGGER.trace("#persistMessageIntoDatabase: Saving message [{}] into storage", dbMessage);
            dbMessage = messageDao.save(dbMessage);
        } catch (DuplicateKeyException cve) {
            String error = String.format("Message already persisted! The domibusConnectorMessageId [%s] already exist.",
                    dbMessage.getConnectorMessageId());
            LOGGER.error(error);
            throw new PersistenceException(error, cve);
        }

        this.msgContentService.saveMessagePayloads(message, dbMessage);

        this.internalMessageInfoPersistenceService.persistMessageInfo(message, dbMessage);

        return message;
    }



    /**
     * Tries to find a message by domibusConnectorMessageId
     *
     * @param message - the message to which the storage data should be found,
     * message need a domibusConnectorMessageId set
     * @return the found message or null
     */
    PDomibusConnectorMessage findMessageByMessage(@Nonnull DomibusConnectorMessage message) {
        String connectorMessageId = message.getConnectorMessageIdAsString();
        Optional<PDomibusConnectorMessage> dbMessage = messageDao.findOneByConnectorMessageId(connectorMessageId);
        if (!dbMessage.isPresent()) {
            LOGGER.warn("No message found with connector message id [{}] ", connectorMessageId);
        }
        return dbMessage.orElse(null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @Transactional
    public DomibusConnectorMessage mergeMessageWithDatabase(@Nonnull DomibusConnectorMessage message) throws PersistenceException {
        if (DomainModelHelper.isEvidenceMessage(message)) {
            LOGGER.debug("#mergeMessageWithDatabase: message is an evidence message, doing nothing!");
            return message;
        }
        PDomibusConnectorMessage dbMessage = findMessageByMessage(message);
        if (dbMessage == null) {
            String error = String.format("No db message found for domain message %s in storage!%n"
                    + "Can only merge a message wich has already been persisted", message);
            LOGGER.error(error + "\nThrowing exception!");
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
//            mapMessageDetailsToDbMessageInfoPersistence(message.getMessageDetails(), messageInfo);
//            messageInfoDao.save(messageInfo);
        }

        this.msgContentService.saveMessagePayloads(message, dbMessage);
        mapRelatedConfirmations(dbMessage, message);
        
        this.messageDao.save(dbMessage);

        return message;
    }

    private void mapRelatedConfirmations(PDomibusConnectorMessage dbMessage, DomibusConnectorMessage message) {
        List<PDomibusConnectorEvidence> collect = message.getRelatedMessageConfirmations()
                .stream()
                .map(MessageConfirmationMapper::mapFromDomainToDb)
                .collect(Collectors.toList());

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
            LOGGER.trace("#setDeliveredToGateway: set connectorId [{}] as delivered in db", connectorMessageId);
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
            LOGGER.trace("#setMessageDeliveredToNationalSystem: set connectorId [{}] as delivered in db", connectorMessageId);
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

    private List<DomibusConnectorMessage> mapDBMessagesToDTO(List<PDomibusConnectorMessage> dbMessages) {
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
    public List<DomibusConnectorMessage> findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery() {
        List<PDomibusConnectorMessage> dbMessages = messageDao.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery();
        return mapDBMessagesToDTO(dbMessages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomibusConnectorMessage> findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD() {
        List<PDomibusConnectorMessage> dbMessages = messageDao.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();
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
            throw new IllegalArgumentException("Argument message must be not null! Cannot confirm null!");
        }
        ZonedDateTime confirmedDate = ZonedDateTime.now();
        message.getMessageDetails().setConfirmed(confirmedDate);
        PDomibusConnectorMessage dbMessage = this.findMessageByMessage(message);
        if (dbMessage == null) {
            throw new IllegalArgumentException("Message must be already persisted to database! Call persistMessageIntoDatabase first");
        }
        int confirmed = messageDao.confirmMessage(dbMessage.getId(), confirmedDate);
        if (confirmed == 1) {
            LOGGER.debug("Message {} successfully confirmed in db", message);
        } else if (confirmed < 1) {
            throw new PersistenceException("message not confirmed!");
        } else {
            throw new IllegalStateException("Multiple messages confirmed! This should not happen! Maybe DB corrupted? Duplicate IDs?");
        }
    }

    @Override
    @Transactional
    public void rejectMessage(DomibusConnectorMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Argument message must be not null! Cannot reject null!");
        }
        ZonedDateTime confirmedDate = ZonedDateTime.now();
        message.getMessageDetails().setRejected(confirmedDate);
        PDomibusConnectorMessage dbMessage = this.findMessageByMessage(message);
        if (dbMessage == null) {
            throw new IllegalArgumentException("Message must be already persisted to database! Call persistMessageIntoDatabase message first!");
        }
        int rejected = messageDao.rejectMessage(dbMessage.getId());
        if (rejected == 1) {
            LOGGER.debug("Message {} successfully marked as rejected in persistence", message);
        } else if (rejected < 1) {
            throw new PersistenceException("message not confirmed!");
        } else {
            throw new IllegalStateException("Multiple messages marked as rejected! This should not happen! Maybe DB corrupted? Duplicate IDs?");
        }

    }

    /**
     * Maps database messages (PDomibusConnectorMessage) to the according
     * representation in Domain layer (Message)
     *
     * @param dbMessage - the database message
     * @return - the mapped message
     */
    @Nullable
    DomibusConnectorMessage mapMessageToDomain(PDomibusConnectorMessage dbMessage) {
        if (dbMessage == null) {
            return null;
        }
        DomibusConnectorMessageBuilder messageBuilder = DomibusConnectorMessageBuilder.createBuilder();

        DomibusConnectorMessageDetails details = new DomibusConnectorMessageDetails();
        details.setEbmsMessageId(dbMessage.getEbmsMessageId());
        details.setBackendMessageId(dbMessage.getBackendMessageId());
        details.setConversationId(dbMessage.getConversationId());

        details.setConnectorBackendClientName(dbMessage.getBackendName());
        details.setGatewayName(dbMessage.getGatewayName());

        details.setDeliveredToBackend(dbMessage.getDeliveredToNationalSystem());
        details.setDeliveredToGateway(dbMessage.getDeliveredToGateway());

        details.setDirection(MessageDirectionMapper.mapFromPersistenceToDomain(dbMessage.getDirectionSource(), dbMessage.getDirectionTarget()));

        details.setRejected(dbMessage.getRejected());
        details.setConfirmed(dbMessage.getConfirmed());

        this.internalMessageInfoPersistenceService.mapMessageInfoIntoMessageDetails(dbMessage, details);

        messageBuilder.setMessageDetails(details);
        messageBuilder.setConnectorMessageId(dbMessage.getConnectorMessageId());
        messageBuilder.setMessageLaneId(DomibusConnectorBusinessDomain.getDefaultMessageLaneId()); //TODO: replace with value from DB!

        this.msgContentService.loadMessagePayloads(messageBuilder, dbMessage);

        loadRelatedEvidences(messageBuilder, dbMessage);

        DomibusConnectorMessage message =  messageBuilder.build();

        return message;
    }


    private void loadRelatedEvidences(DomibusConnectorMessageBuilder messageBuilder, PDomibusConnectorMessage dbMessage) {
        List<DomibusConnectorMessageConfirmation> collect = dbMessage.getRelatedEvidences().stream()
                .map(MessageConfirmationMapper::mapFromDbToDomain)
                .collect(Collectors.toList());
        messageBuilder.addRelatedConfirmations(collect);
    }


}
