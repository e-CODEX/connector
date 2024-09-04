/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.wsbackendplugin;

import static eu.ecodex.connector.link.wsbackendplugin.BackendClientPushWebServiceConfiguration.PUSH_DELIVERED_MESSAGES_LIST_BEAN_NAME;

import eu.ecodex.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

class DummyDomibusConnectorBackendDeliveryWebServiceImpl
    implements DomibusConnectorBackendDeliveryWebService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DummyDomibusConnectorBackendDeliveryWebServiceImpl.class);
    @Resource
    WebServiceContext webServiceContext;
    @Autowired
    @Qualifier(PUSH_DELIVERED_MESSAGES_LIST_BEAN_NAME)
    LinkedBlockingQueue<DomibusConnectorMessageType> domibusConnectorMessageTypeList;
    @Value("${ws.backendclient.name}")
    String backendClientName;

    @Override
    public DomibsConnectorAcknowledgementType deliverMessage(
        DomibusConnectorMessageType deliverMessageRequest) {
        LOGGER.debug("deliverMessage [{}]", deliverMessageRequest);
        String name = webServiceContext.getUserPrincipal().getName();
        LOGGER.debug("message client with name: [{}]", name);

        DomibsConnectorAcknowledgementType ack = new DomibsConnectorAcknowledgementType();
        domibusConnectorMessageTypeList.add(deliverMessageRequest);
        ack.setResult(true);
        ack.setMessageId(backendClientName + "msg" + UUID.randomUUID());
        return ack;
    }
}
