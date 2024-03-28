package eu.domibus.connectorplugins.link.gwwspushplugin;

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.ws.gateway.submission.webservice.DomibusConnectorGatewaySubmissionWebService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


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

    @Override
    //    @Transactional //(Transactional.TxType.REQUIRES_NEW)
    public void submitToLink(
            DomibusConnectorMessage message,
            DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) throws DomibusConnectorSubmitToLinkException {
        TransportStateService.DomibusConnectorTransportState transportState =
                new TransportStateService.DomibusConnectorTransportState();
        transportState.setStatus(TransportState.PENDING);
        TransportStateService.TransportId transportId =
                transportStateService.createTransportFor(message, linkPartnerName);
        transportStateService.updateTransportToGatewayStatus(transportId, transportState);

        DomibusConnectorMessageType domibusConnectorMessageType =
                transformerService.transformDomainToTransition(message);
        // TODO: catch P-Mode exception, or read issue from plugin
        DomibsConnectorAcknowledgementType domibsConnectorAcknowledgementType =
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
