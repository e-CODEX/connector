
package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
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
     * marks the message as rejected
     * @throws IllegalArgumentException is thrown, if the message is null,
     *  or the message does not contain a connector id
     * @throws RuntimeException - if the message is not successfully marked as
     * rejected
     * @param message - the message
     */
    void rejectMessage(DomibusConnectorMessage message);

    /**
     * marks the message as confirmed
     * @throws IllegalArgumentException  is thrown, if the message is null,
     *  or the message does not contain a db id
     * @throws RuntimeException - if the message is not successfully marked as
     * confirmed
     * @param message - the message to confirm
     */
    void confirmMessage(DomibusConnectorMessage message);

    /**
     * all messages which are going to the national system
     * @return the list of unconfirmed messages
     */
    List<DomibusConnectorMessage> findIncomingUnconfirmedMessages();

    DomibusConnectorMessage findMessageByConnectorMessageId(String connectorMessageId);

    /**
     *
     *
     * @param ebmsMessageId - the ebmsId of the message
     * @param messageDirection - the direction of the message
     * @return the found message or an empty Optional if no message found with this ebmsId and direction
     */
    Optional<DomibusConnectorMessage> findMessageByEbmsIdAndDirection(String ebmsMessageId, DomibusConnectorMessageDirection messageDirection);

    /**
     * finds the message by the national id and direction
     * the nationalId is not set if the message was received from the gw
     * @param nationalMessageId - the nationalMessageId
     * @param messageDirection - the direction of the message
     * @return the found message or an empty Optional if no message found with this nationalMessageId and direction
     */
    Optional<DomibusConnectorMessage> findMessageByNationalIdAndDirection(String nationalMessageId, DomibusConnectorMessageDirection messageDirection);


    /**
     *
     *
     * @param ebmsMessageId - the ebmsId of the message
     * @param messageDirection - the direction of the message
     * @return the found message or an empty Optional if no message found with this ebmsId and direction
     */
    Optional<DomibusConnectorMessage> findMessageByEbmsIdOrBackendIdAndDirection(String ebmsMessageId, DomibusConnectorMessageDirection messageDirection);

    /**
     * returns all messages related to the
     * conversation id
     * @param conversationId - the conversation id
     * @return - a list of messages, if there are no messages found
     *  the list will be empty
     */
    List<DomibusConnectorMessage> findMessagesByConversationId(String conversationId);

    /**
     *
     * @return a list of Messages or an emtpy List if nothing found
     */
    List<DomibusConnectorMessage> findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery();

    /**
     *
     * @return a list of Messages or an emtpy List if nothing found
     */
    List<DomibusConnectorMessage> findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();

    /**
     * all messages which are going to the GW
     * @return the list of unconfirmed messages
     */
    List<DomibusConnectorMessage> findOutgoingUnconfirmedMessages();

    /**
     * Only updates
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
     * also stores/updates message content
     * <ul>
     *  <li>all attachments</li>
     *  <li>message content xml content</li>
     *  <li>message message content document with signature</li>
     * </ul>
     *
     *
     * @param message - the message
     * @return the message with eventually updated fields
     * @throws PersistenceException in case of an error
     */
    @Deprecated
    DomibusConnectorMessage mergeMessageWithDatabase(@Nonnull DomibusConnectorMessage message) throws PersistenceException;

    /**
     * stores a new message into storage
     *
     * @deprecated  the method persistBusinessMessageIntoDatabase should be used instead
     * @param message - the message
     * @param direction - direction of the message
     * @return the message with eventually updated fields
     * @throws PersistenceException - in case of failures with persistence
     *
     */
    @Deprecated
    DomibusConnectorMessage persistMessageIntoDatabase(@Nonnull DomibusConnectorMessage message, DomibusConnectorMessageDirection direction) throws PersistenceException;

    /**
     * Marks the message as delivered to the gateway
     * @param message - the message, which should be marked
     */
    void setDeliveredToGateway(DomibusConnectorMessage message);

    /**
     * Marks the message as delivered to national backend
     * @param message - the message, which should be marked
     */
    void setMessageDeliveredToNationalSystem(DomibusConnectorMessage message);

    void updateMessageDetails(DomibusConnectorMessage message);

    /**
     * stores a business messsage into database
     *  -) the message details
     *  -) the message content
     *  -) the message attachments
     *
     * @param message - the connector message
     */
    void persistBusinessMessageIntoDatabase(DomibusConnectorMessage message);
}
