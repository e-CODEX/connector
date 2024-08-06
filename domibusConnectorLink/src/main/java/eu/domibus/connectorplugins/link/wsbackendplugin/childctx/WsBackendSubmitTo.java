/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connectorplugins.link.wsbackendplugin.childctx;

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.LinkMode;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import eu.domibus.connectorplugins.link.wsbackendplugin.WsBackendPluginActiveLinkPartner;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * This class represents a backend submission to a link partner. It implements the
 * SubmitToLinkPartner interface.
 */
@Component
public class WsBackendSubmitTo implements SubmitToLinkPartner {
    private final DCActiveLinkManagerService linkManagerService;
    private final WsBackendPluginWebServiceClientFactory webServiceClientFactory;
    private final DomibusConnectorDomainMessageTransformerService transformerService;
    private final TransportStateService transportStateService;

    /**
     * Initializes a new instance of the WsBackendSubmitTo class with the specified parameters.
     *
     * @param linkManagerService      The DCActiveLinkManagerService used for managing links.
     * @param webServiceClientFactory The WsBackendPluginWebServiceClientFactory used for creating
     *                                web service clients.
     * @param transformerService      The DomibusConnectorDomainMessageTransformerService used for
     *                                transforming messages.
     * @param transportStateService   The TransportStateService used for managing transport states.
     */
    public WsBackendSubmitTo(
        DCActiveLinkManagerService linkManagerService,
        WsBackendPluginWebServiceClientFactory webServiceClientFactory,
        DomibusConnectorDomainMessageTransformerService transformerService,
        TransportStateService transportStateService) {
        this.linkManagerService = linkManagerService;
        this.webServiceClientFactory = webServiceClientFactory;
        this.transformerService = transformerService;
        this.transportStateService = transportStateService;
    }

    @Override
    public void submitToLink(
        DomibusConnectorMessage message,
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName)
        throws DomibusConnectorSubmitToLinkException {
        Optional<ActiveLinkPartner> activeLinkPartnerByName =
            linkManagerService.getActiveLinkPartnerByName(linkPartnerName);
        if (activeLinkPartnerByName.isPresent()) {
            WsBackendPluginActiveLinkPartner activeLinkPartner =
                (WsBackendPluginActiveLinkPartner) activeLinkPartnerByName.get();
            if (activeLinkPartner.getLinkPartner().getSendLinkMode() == LinkMode.PUSH) {
                pushMessage(message, activeLinkPartner);
            } else {
                makeMessageReadyForPull(message, activeLinkPartner);
            }
        } else {
            throw new DomibusConnectorSubmitToLinkException(
                message, java.lang.String.format(
                "No LinkPartner found with name [%s]", linkPartnerName));
        }
    }

    void makeMessageReadyForPull(
        DomibusConnectorMessage message, WsBackendPluginActiveLinkPartner activeLinkPartner) {
        // DomibusConnectorBackendDeliveryWebService backendWsClient =
        // webServiceClientFactory.createBackendWsClient(activeLinkPartner);

        var transportId = transportStateService.createTransportFor(
            message, activeLinkPartner.getLinkPartner().getLinkPartnerName()
        );

        var state = new TransportStateService.DomibusConnectorTransportState();
        state.setStatus(TransportState.PENDING);
        state.setText("Message ready for pull by client");
        transportStateService.updateTransportToBackendClientStatus(transportId, state);
    }

    void pushMessage(
        DomibusConnectorMessage message, WsBackendPluginActiveLinkPartner activeLinkPartner) {
        DomibusConnectorBackendDeliveryWebService backendWsClient =
            webServiceClientFactory.createBackendWsClient(activeLinkPartner);

        var transportId = transportStateService.createTransportFor(
            message, activeLinkPartner.getLinkPartner().getLinkPartnerName()
        );

        var state = new TransportStateService.DomibusConnectorTransportState();
        var domibusConnectorMessageType = transformerService.transformDomainToTransition(message);
        try {
            var domibsConnectorAcknowledgementType =
                backendWsClient.deliverMessage(domibusConnectorMessageType);

            state.setStatus(
                domibsConnectorAcknowledgementType.isResult() ? TransportState.ACCEPTED :
                    TransportState.FAILED);
            state.setRemoteMessageId(domibsConnectorAcknowledgementType.getMessageId());
            state.setText(domibsConnectorAcknowledgementType.getResultMessage());
        } catch (Exception e) {
            state.setStatus(TransportState.FAILED);
            state.setText(e.getMessage());
        }
        transportStateService.updateTransportToBackendClientStatus(transportId, state);
    }
}
