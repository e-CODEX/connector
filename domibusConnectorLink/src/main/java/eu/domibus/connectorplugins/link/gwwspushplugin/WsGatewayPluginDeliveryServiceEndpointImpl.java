package eu.domibus.connectorplugins.link.gwwspushplugin;

import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWebService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;


public class WsGatewayPluginDeliveryServiceEndpointImpl implements DomibusConnectorGatewayDeliveryWebService {
    private static final Logger LOGGER = LogManager.getLogger(WsGatewayPluginDeliveryServiceEndpointImpl.class);

    @Autowired
    SubmitToConnector submitToConnector;
    @Autowired
    DomibusConnectorMessageIdGenerator messageIdGenerator;
    @Autowired
    DomibusConnectorDomainMessageTransformerService transformerService;
    @Autowired
    DomibusConnectorLinkPartner linkPartner;

    @Override
    public DomibsConnectorAcknowledgementType deliverMessage(DomibusConnectorMessageType deliverMessageRequest) {
        String linkName = linkPartner.getLinkPartnerName().getLinkName();
        DomibsConnectorAcknowledgementType ret = new DomibsConnectorAcknowledgementType();
        DomibusConnectorMessageId connectorMessageId = messageIdGenerator.generateDomibusConnectorMessageId();
        try (
                MDC.MDCCloseable mdc = MDC.putCloseable(
                        LoggingMDCPropertyNames.MDC_LINK_PARTNER_NAME,
                        linkName
                ); MDC.MDCCloseable conId = MDC.putCloseable(
                LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
                connectorMessageId.getConnectorMessageId()
        )
        ) {
            LOGGER.debug(
                    "Message delivered from gateway [{}] assigning connectorMessageId [{}]",
                    linkName,
                    connectorMessageId
            );
            DomibusConnectorMessage domibusConnectorMessage =
                    transformerService.transformTransitionToDomain(deliverMessageRequest, connectorMessageId);
            domibusConnectorMessage.setConnectorMessageId(connectorMessageId);

            submitToConnector.submitToConnector(domibusConnectorMessage, linkPartner);

            ret.setMessageId(connectorMessageId.getConnectorMessageId());
            ret.setResult(true);
        } catch (Exception e) {
            ret.setResultMessage(e.getMessage());
            ret.setResult(false);
            LOGGER.error("Exception occured while receiving message from gateway", e);
        }
        return ret;
    }
}
