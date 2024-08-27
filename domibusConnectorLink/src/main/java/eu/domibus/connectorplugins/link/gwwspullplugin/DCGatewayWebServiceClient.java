/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connectorplugins.link.gwwspullplugin;

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.link.service.PullFromLinkPartner;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.ws.gateway.webservice.DomibusConnectorGatewayWebService;
import eu.domibus.connector.ws.gateway.webservice.GetMessageByIdRequest;
import eu.domibus.connector.ws.gateway.webservice.ListPendingMessageIdsRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The DCGatewayWebServiceClient class is a client for the Domibus Connector Gateway Web Service. It
 * implements the SubmitToLinkPartner and PullFromLinkPartner interfaces.
 */
@SuppressWarnings("squid:S6813")
public class DCGatewayWebServiceClient implements SubmitToLinkPartner, PullFromLinkPartner {
    private static final Logger LOGGER = LogManager.getLogger(DCGatewayWebServiceClient.class);
    @Autowired
    DomibusConnectorGatewayWebService gatewayWebService;
    @Autowired
    DomibusConnectorDomainMessageTransformerService transformerService;
    @Autowired
    TransportStateService transportStateService;
    @Autowired
    SubmitToConnector submitToConnector;
    @Autowired
    DCActiveLinkManagerService dcActiveLinkManagerService;
    @Autowired
    DomibusConnectorMessageIdGenerator messageIdGenerator;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void submitToLink(
        DomibusConnectorMessage message,
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName)
        throws DomibusConnectorSubmitToLinkException {
        var transportState = new TransportStateService.DomibusConnectorTransportState();
        transportState.setStatus(TransportState.PENDING);
        var transportId = transportStateService.createTransportFor(message, linkPartnerName);
        transportStateService.updateTransportToGatewayStatus(transportId, transportState);

        var domibusConnectorMessageType = transformerService.transformDomainToTransition(message);
        var domibsConnectorAcknowledgementType =
            gatewayWebService.submitMessage(domibusConnectorMessageType);

        transportState = new TransportStateService.DomibusConnectorTransportState();
        transportState.setRemoteMessageId(domibsConnectorAcknowledgementType.getMessageId());
        transportState.setText(domibsConnectorAcknowledgementType.getResultMessage());
        if (domibsConnectorAcknowledgementType.isResult()) {
            transportState.setStatus(TransportState.ACCEPTED);
        } else {
            transportState.setStatus(TransportState.FAILED);
        }
        transportStateService.updateTransportToGatewayStatus(transportId, transportState);
    }

    /**
     * Pulls messages from a specific link partner in the Domibus Connector.
     *
     * @param linkPartner The name of the link partner.
     */
    public void pullMessagesFrom(DomibusConnectorLinkPartner.LinkPartnerName linkPartner) {
        try (var mdcCloseable = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_LINK_PARTNER_NAME, linkPartner.getLinkName())) {
            var req = new ListPendingMessageIdsRequest();
            var listPendingMessageIdsResponse =
                gatewayWebService.listPendingMessageIds(req);

            List<java.lang.String> messageIds = listPendingMessageIdsResponse.getMessageIds();
            messageIds.forEach(id -> this.pullMessage(linkPartner, id));
        }
    }

    private void pullMessage(
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName,
        java.lang.String remoteMessageId) {

        var connectorMessageId = messageIdGenerator.generateDomibusConnectorMessageId();
        try (var mdcCloseable = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_REMOTE_MSG_ID, remoteMessageId);
             MDC.MDCCloseable conMsgId = MDC.putCloseable(
                 LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
                 connectorMessageId.getConnectorMessageId()
             )
        ) {
            LOGGER.trace(
                "Pulling message with id [{}] from [{}]", remoteMessageId, linkPartnerName);
            var getMessageByIdRequest = new GetMessageByIdRequest();
            getMessageByIdRequest.setMessageId(remoteMessageId);
            DomibusConnectorMessageType messageById =
                gatewayWebService.getMessageById(getMessageByIdRequest);

            DomibusConnectorMessage message =
                transformerService.transformTransitionToDomain(messageById, connectorMessageId);

            Optional<ActiveLinkPartner> activeLinkPartnerByName =
                dcActiveLinkManagerService.getActiveLinkPartnerByName(linkPartnerName);
            submitToConnector.submitToConnector(
                message, activeLinkPartnerByName.get().getLinkPartner());
        }
    }
}
