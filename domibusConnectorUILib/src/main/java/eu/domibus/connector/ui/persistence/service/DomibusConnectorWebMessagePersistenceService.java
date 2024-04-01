package eu.domibus.connector.ui.persistence.service;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.ui.dto.WebMessage;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;


public interface DomibusConnectorWebMessagePersistenceService {
    LinkedList<WebMessage> getAllMessages();

    Optional<WebMessage> getMessageByConnectorId(String connectorMessageId);

    LinkedList<WebMessage> getMessagesWithinPeriod(Date from, Date to);

    Optional<WebMessage> findMessageByNationalId(String nationalMessageId, DomibusConnectorMessageDirection direction);

    Optional<WebMessage> findMessageByEbmsId(String ebmsMessageId, DomibusConnectorMessageDirection direction);

    LinkedList<WebMessage> findMessagesByConversationId(String conversationId);

    Page<WebMessage> findAll(Example<WebMessage> example, Pageable pageable);

    //	long count(Example<WebMessage> example, Pageable pageable);
    long count(Example<WebMessage> example);

    Optional<WebMessage> getOutgoingMessageByBackendMessageId(String backendMessageId);

    Optional<WebMessage> getIncomingMessageByEbmsMessageId(String ebmsMessageId);

    LinkedList<WebMessage> findConnectorTestMessages(String connectorTestBackendName);
}
