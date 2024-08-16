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

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.ws.gateway.submission.webservice.DomibusConnectorGatewaySubmissionWebService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for submitting a message to a link partner through the Domibus
 * Connector Gateway Submission Web Service.
 */
@SuppressWarnings("squid:S6813")
public class WsGatewayPluginWebServiceClient implements SubmitToLinkPartner {
    @Autowired
    DomibusConnectorGatewaySubmissionWebService gatewayWebService;
    @Autowired
    DomibusConnectorDomainMessageTransformerService transformerService;
    @Autowired
    TransportStateService transportStateService;
    @Autowired
    SubmitToConnector submitToConnector;
    @Autowired
    DCActiveLinkManagerService dcActiveLinkManagerService;

    @SuppressWarnings("squid:S1135")
    @Override
    //    @Transactional //(Transactional.TxType.REQUIRES_NEW)
    public void submitToLink(
        DomibusConnectorMessage message,
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName)
        throws DomibusConnectorSubmitToLinkException {
        var transportState = new TransportStateService.DomibusConnectorTransportState();
        transportState.setStatus(TransportState.PENDING);
        var transportId = transportStateService.createTransportFor(message, linkPartnerName);
        transportStateService.updateTransportToGatewayStatus(transportId, transportState);

        var domibusConnectorMessageType = transformerService.transformDomainToTransition(message);
        // TODO: catch P-Mode exception, or read issue from plugin
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
}
