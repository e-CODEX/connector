/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.impl.gwwspullplugin;

import static eu.ecodex.connector.link.impl.gwwspullplugin.TestGwWebService.TO_PULL_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME;

import eu.ecodex.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.ws.gateway.webservice.DomibusConnectorGatewayWebService;
import eu.ecodex.connector.ws.gateway.webservice.GetMessageByIdRequest;
import eu.ecodex.connector.ws.gateway.webservice.ListPendingMessageIdsRequest;
import eu.ecodex.connector.ws.gateway.webservice.ListPendingMessageIdsResponse;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The TestGwPullServiceImpl class implements the DomibusConnectorGatewayWebService interface and
 * provides the implementation for submitting messages, listing pending message IDs, and getting
 * messages by ID.
 */
public class TestGwPullServiceImpl implements DomibusConnectorGatewayWebService {
    @Autowired
    @Qualifier(TO_PULL_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME)
    LinkedBlockingQueue<DomibusConnectorMessageType> toGwSubmittedMsg;
    @Autowired
    TestGwWebService.GetMessageByIdMock getMessageByIdMock;
    @Autowired
    TestGwWebService.ListPendingMessagesMock listPendingMessagesMock;

    @Override
    public DomibsConnectorAcknowledgementType submitMessage(
        DomibusConnectorMessageType submitMessageRequest) {
        boolean add = toGwSubmittedMsg.add(submitMessageRequest);
        DomibsConnectorAcknowledgementType ack = new DomibsConnectorAcknowledgementType();
        ack.setResult(add);
        ack.setMessageId(UUID.randomUUID().toString());
        return ack;
    }

    @Override
    public ListPendingMessageIdsResponse listPendingMessageIds(
        ListPendingMessageIdsRequest listPendingMessageIdsRequest) {
        return listPendingMessagesMock.listPendingMessageIds();
    }

    @Override
    public DomibusConnectorMessageType getMessageById(GetMessageByIdRequest getMessageByIdRequest) {
        return getMessageByIdMock.getMessageById(getMessageByIdRequest);
    }
}
