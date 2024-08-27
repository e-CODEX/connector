/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * DCMessagePersistenceService interface provides methods for persisting and retrieving connector
 * messages in the database.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface DCMessagePersistenceService {
    boolean checkMessageConfirmed(DomibusConnectorMessage message);

    default boolean checkMessageConfirmedOrRejected(DomibusConnectorMessage message) {
        return checkMessageConfirmedOrRejected(message.getConnectorMessageId());
    }

    boolean checkMessageConfirmedOrRejected(DomibusConnectorMessageId message);

    boolean checkMessageRejected(DomibusConnectorMessage message);

    /**
     * Marks the message as rejected.
     *
     * @param message - the message
     * @throws IllegalArgumentException is thrown, if the message is null, or the message does not
     *                                  contain a connector id
     * @throws RuntimeException         - if the message is not successfully marked as rejected
     */
    void rejectMessage(DomibusConnectorMessage message);

    /**
     * Marks the message as confirmed.
     *
     * @param message - the message to confirm
     * @throws IllegalArgumentException is thrown, if the message is null, or the message does not
     *                                  contain a db id
     * @throws RuntimeException         - if the message is not successfully marked as confirmed
     */
    void confirmMessage(DomibusConnectorMessage message);

    /**
     * All messages which are going to the national system.
     *
     * @return the list of unconfirmed messages
     */
    List<DomibusConnectorMessage> findIncomingUnconfirmedMessages();

    DomibusConnectorMessage findMessageByConnectorMessageId(String connectorMessageId);

    /**
     * Finds a message in the persistence storage by ebmsMessageId and messageDirection.
     *
     * @param ebmsMessageId    - the ebmsId of the message
     * @param messageDirection - the direction of the message
     * @return the optional of DomibusConnectorMessage found with the specified ebmsMessageId and
     *      messageDirection, or an empty optional if no message is found
     */
    Optional<DomibusConnectorMessage> findMessageByEbmsIdAndDirection(
        String ebmsMessageId,
        DomibusConnectorMessageDirection messageDirection);

    /**
     * finds the message by the national id and direction the nationalId is not set if the message
     * was received from the gw.
     *
     * @param nationalMessageId - the nationalMessageId
     * @param messageDirection  - the direction of the message
     * @return the found message or an empty Optional if no message found with this
     *      nationalMessageId and direction
     */
    Optional<DomibusConnectorMessage> findMessageByNationalIdAndDirection(
        String nationalMessageId,
        DomibusConnectorMessageDirection messageDirection);

    /**
     * Finds a message in the persistence storage by the given ebmsMessageId and messageDirection.
     *
     * @param ebmsMessageId    the ebmsId of the message
     * @param messageDirection the direction of the message
     * @return the optional of DomibusConnectorMessage found with the specified ebmsMessageId and
     *      messageDirection, or an empty optional if no message is found
     */
    Optional<DomibusConnectorMessage> findMessageByEbmsIdOrBackendIdAndDirection(
        String ebmsMessageId, DomibusConnectorMessageDirection messageDirection);

    /**
     * Returns all messages related to the conversation id.
     *
     * @param conversationId - the conversation id
     * @return - a list of messages, if there are no messages found the list will be empty
     */
    List<DomibusConnectorMessage> findMessagesByConversationId(String conversationId);

    /**
     * Finds all outgoing messages that are not rejected, not confirmed, and without delivery.
     *
     * @return a list of outgoing messages that match the criteria
     */
    List<DomibusConnectorMessage> findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery();

    /**
     * Finds all outgoing messages that are not rejected, not confirmed, and without relay REMMD.
     *
     * @return a list of outgoing messages that match the criteria
     */
    List<DomibusConnectorMessage> findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();

    /**
     * All messages which are going to the GW.
     *
     * @return the list of unconfirmed messages
     */
    List<DomibusConnectorMessage> findOutgoingUnconfirmedMessages();

    /**
     * Merges the given {@link DomibusConnectorMessage} with the database. This method updates the
     * fields of the message in the database.
     *
     * <p>Only updates.
     * <ul>
     *   <li>action</li>
     *   <li>service</li>
     *   <li>fromParty</li>
     *   <li>toParty</li>
     *   <li>finalRecipient</li>
     *   <li>originalRecipient</li>
     * </ul>
     *  of the provided message details
     *
     * <p>Also stores/updates message content
     * <ul>
     *  <li>all attachments</li>
     *  <li>message content xml content</li>
     *  <li>message message content document with signature</li>
     * </ul>
     *
     * @param message The {@link DomibusConnectorMessage} to merge with the database.
     * @return The updated {@link DomibusConnectorMessage}.
     * @throws PersistenceException If there is a failure with persistence.
     * @deprecated This method is deprecated and should not be used anymore. It is recommended to
     *      commended to use the persistBusinessMessageIntoDatabase method instead.
     */
    @Deprecated
    DomibusConnectorMessage mergeMessageWithDatabase(@Nonnull DomibusConnectorMessage message)
        throws PersistenceException;

    /**
     * Stores a new message into storage.
     *
     * @param message   - the message
     * @param direction - direction of the message
     * @return the message with eventually updated fields
     * @throws PersistenceException - in case of failures with persistence
     * @deprecated the method persistBusinessMessageIntoDatabase should be used instead
     */
    @Deprecated
    DomibusConnectorMessage persistMessageIntoDatabase(
        @Nonnull DomibusConnectorMessage message,
        DomibusConnectorMessageDirection direction)
        throws PersistenceException;

    /**
     * Marks the message as delivered to the gateway.
     *
     * @param message - the message, which should be marked
     */
    void setDeliveredToGateway(DomibusConnectorMessage message);

    /**
     * Marks the message as delivered to national backend.
     *
     * @param message - the message, which should be marked
     */
    void setMessageDeliveredToNationalSystem(DomibusConnectorMessage message);

    void updateMessageDetails(DomibusConnectorMessage message);

    /**
     * stores a business message into database -) the message details -) the message content -) the
     * message attachments.
     *
     * @param message - the connector message
     */
    void persistBusinessMessageIntoDatabase(DomibusConnectorMessage message);
}
