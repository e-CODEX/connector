/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.persistence.service;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.ui.dto.WebMessage;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The DomibusConnectorWebMessagePersistenceService interface defines the methods for retrieving and
 * manipulating web messages from the Domibus Connector persistence.
 */
public interface DomibusConnectorWebMessagePersistenceService {
    LinkedList<WebMessage> getAllMessages();

    Optional<WebMessage> getMessageByConnectorId(String connectorMessageId);

    LinkedList<WebMessage> getMessagesWithinPeriod(Date from, Date to);

    Optional<WebMessage> findMessageByNationalId(
        String nationalMessageId, DomibusConnectorMessageDirection direction);

    Optional<WebMessage> findMessageByEbmsId(
        String ebmsMessageId, DomibusConnectorMessageDirection direction);

    LinkedList<WebMessage> findMessagesByConversationId(String conversationId);

    Page<WebMessage> findAll(Example<WebMessage> example, Pageable pageable);

    long count(Example<WebMessage> example);

    Optional<WebMessage> getOutgoingMessageByBackendMessageId(String backendMessageId);

    Optional<WebMessage> getIncomingMessageByEbmsMessageId(String ebmsMessageId);

    LinkedList<WebMessage> findConnectorTestMessages(String connectorTestBackendName);
}
