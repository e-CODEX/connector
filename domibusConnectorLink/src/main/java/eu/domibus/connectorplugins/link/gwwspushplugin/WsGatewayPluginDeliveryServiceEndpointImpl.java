/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connectorplugins.link.gwwspushplugin;

import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWebService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of the DomibusConnectorGatewayDeliveryWebService interface. This class provides
 * the functionality to deliver messages from the gateway to the connector.
 */
@SuppressWarnings("squid:S6813")
public class WsGatewayPluginDeliveryServiceEndpointImpl
    implements DomibusConnectorGatewayDeliveryWebService {
    private static final Logger LOGGER =
        LogManager.getLogger(WsGatewayPluginDeliveryServiceEndpointImpl.class);
    @Autowired
    SubmitToConnector submitToConnector;
    @Autowired
    DomibusConnectorMessageIdGenerator messageIdGenerator;
    @Autowired
    DomibusConnectorDomainMessageTransformerService transformerService;
    @Autowired
    DomibusConnectorLinkPartner linkPartner;

    @Override
    public DomibsConnectorAcknowledgementType deliverMessage(
        DomibusConnectorMessageType deliverMessageRequest) {
        String linkName = linkPartner.getLinkPartnerName().getLinkName();
        var acknowledgementType = new DomibsConnectorAcknowledgementType();
        var connectorMessageId =
            messageIdGenerator.generateDomibusConnectorMessageId();
        try (var mdc = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_LINK_PARTNER_NAME,
            linkName
        );
             var conId = MDC.putCloseable(
                 LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
                 connectorMessageId.getConnectorMessageId()
             )
        ) {
            LOGGER.debug(
                "Message delivered from gateway [{}] assigning connectorMessageId [{}]", linkName,
                connectorMessageId
            );
            var domibusConnectorMessage = transformerService.transformTransitionToDomain(
                deliverMessageRequest,
                connectorMessageId
            );
            domibusConnectorMessage.setConnectorMessageId(connectorMessageId);

            submitToConnector.submitToConnector(domibusConnectorMessage, linkPartner);

            acknowledgementType.setMessageId(connectorMessageId.getConnectorMessageId());
            acknowledgementType.setResult(true);
        } catch (Exception e) {
            acknowledgementType.setResultMessage(e.getMessage());
            acknowledgementType.setResult(false);
            LOGGER.error("Exception occured while receiving message from gateway", e);
        }
        return acknowledgementType;
    }
}
