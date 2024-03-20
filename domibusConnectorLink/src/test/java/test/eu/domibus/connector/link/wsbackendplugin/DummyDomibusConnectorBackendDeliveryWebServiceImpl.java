package test.eu.domibus.connector.link.wsbackendplugin;

import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import static test.eu.domibus.connector.link.wsbackendplugin.BackendClientPushWebServiceConfiguration.PUSH_DELIVERED_MESSAGES_LIST_BEAN_NAME;

class DummyDomibusConnectorBackendDeliveryWebServiceImpl implements DomibusConnectorBackendDeliveryWebService {

    private final static Logger LOGGER = LoggerFactory.getLogger(DummyDomibusConnectorBackendDeliveryWebServiceImpl.class);

    @Resource
    WebServiceContext webServiceContext;

    @Autowired
    @Qualifier(PUSH_DELIVERED_MESSAGES_LIST_BEAN_NAME)
    LinkedBlockingQueue<DomibusConnectorMessageType> domibusConnectorMessageTypeList;

    @Value("${ws.backendclient.name}")
    String backendClientName;

    @Override
    public DomibsConnectorAcknowledgementType deliverMessage(DomibusConnectorMessageType deliverMessageRequest) {
        LOGGER.debug("deliverMessage [{}]", deliverMessageRequest);
        String name = webServiceContext.getUserPrincipal().getName();
        LOGGER.debug("message client with name: [{}]", name);

        DomibsConnectorAcknowledgementType ack = new DomibsConnectorAcknowledgementType();
        domibusConnectorMessageTypeList.add(deliverMessageRequest);
        ack.setResult(true);
        ack.setMessageId(backendClientName + "msg" + UUID.randomUUID().toString());
        return ack;
    }

}
