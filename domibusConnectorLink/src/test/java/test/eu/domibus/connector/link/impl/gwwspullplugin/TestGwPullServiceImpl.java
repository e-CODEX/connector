package test.eu.domibus.connector.link.impl.gwwspullplugin;

import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.gateway.webservice.DomibusConnectorGatewayWebService;
import eu.domibus.connector.ws.gateway.webservice.GetMessageByIdRequest;
import eu.domibus.connector.ws.gateway.webservice.ListPendingMessageIdsRequest;
import eu.domibus.connector.ws.gateway.webservice.ListPendingMessageIdsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import static test.eu.domibus.connector.link.impl.gwwspullplugin.TestGwWebService.TO_PULL_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME;


public class TestGwPullServiceImpl implements DomibusConnectorGatewayWebService {
    @Autowired
    @Qualifier(TO_PULL_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME)
    LinkedBlockingQueue<DomibusConnectorMessageType> toGwSubmittedMsg;

    @Autowired
    TestGwWebService.GetMessageByIdMock getMessageByIdMock;

    @Autowired
    TestGwWebService.ListPendingMessagesMock listPendingMessagesMock;

    @Override
    public DomibsConnectorAcknowledgementType submitMessage(DomibusConnectorMessageType submitMessageRequest) {
        boolean add = toGwSubmittedMsg.add(submitMessageRequest);
        DomibsConnectorAcknowledgementType ack = new DomibsConnectorAcknowledgementType();
        ack.setResult(add);
        ack.setMessageId(UUID.randomUUID().toString());
        return ack;
    }

    @Override
    public ListPendingMessageIdsResponse listPendingMessageIds(ListPendingMessageIdsRequest listPendingMessageIdsRequest) {
        return listPendingMessagesMock.listPendingMessageIds();
    }

    @Override
    public DomibusConnectorMessageType getMessageById(GetMessageByIdRequest getMessageByIdRequest) {
        return getMessageByIdMock.getMessageById(getMessageByIdRequest);
    }
}
