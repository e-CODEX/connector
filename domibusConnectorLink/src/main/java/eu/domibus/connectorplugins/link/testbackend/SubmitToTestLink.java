package eu.domibus.connectorplugins.link.testbackend;

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageConfirmationBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDetailsBuilder;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class SubmitToTestLink implements SubmitToLinkPartner {
    private static final Logger LOGGER = LogManager.getLogger(SubmitToTestLink.class);

    private final SubmitToConnector submitToConnector;
    private final TransportStateService transportStateService;
    private final DomibusConnectorMessageIdGenerator messageIdGenerator;
    private boolean enabled;
    private DomibusConnectorLinkPartner linkPartner;

    public SubmitToTestLink(
            SubmitToConnector submitToConnector,
            TransportStateService transportStateService,
            DomibusConnectorMessageIdGenerator messageIdGenerator) {
        this.submitToConnector = submitToConnector;
        this.transportStateService = transportStateService;
        this.messageIdGenerator = messageIdGenerator;
    }

    @Override
    public void submitToLink(
            DomibusConnectorMessage message,
            DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) throws DomibusConnectorSubmitToLinkException {
        if (DomainModelHelper.isBusinessMessage(message)) {
            if (this.enabled) {
                String ebmsMessageId = message.getMessageDetails().getEbmsMessageId();
                DomibusConnectorMessage deliveryConfirmation = DomibusConnectorMessageBuilder
                        .createBuilder()
                        .setMessageDetails(
                                DomibusConnectorMessageDetailsBuilder
                                        .create()
                                        .withRefToMessageId(ebmsMessageId)                                              // set ref to message id to ebms id
                                        .build()
                        )
                        .setConnectorMessageId(
                                messageIdGenerator.generateDomibusConnectorMessageId()
                        )
                        .setMessageLaneId(message.getMessageLaneId())
                        .addTransportedConfirmations(
                                DomibusConnectorMessageConfirmationBuilder
                                        .createBuilder()     // append evidence trigger
                                        // of type DELIVERY
                                        .setEvidenceType(
                                                DomibusConnectorEvidenceType.DELIVERY
                                        )
                                        .setEvidence(
                                                new byte[0])
                                        .build()
                        )
                        .build();

                submitToConnector.submitToConnector(
                        deliveryConfirmation,
                        linkPartnerName,
                        LinkType.BACKEND
                );       // submit trigger message to connector

                LOGGER.info(
                        "Generated Delivery evidence trigger message for connector test message with EBMS ID [{}]",
                        message.getConnectorMessageId()
                );
            } else {
                LOGGER.warn("Test message received, but test backend is not enabled! No response will be sent!");
            }
        } else {
            // ignore evidence messages...
        }
        TransportStateService.TransportId transportFor =
                transportStateService.createTransportFor(message, linkPartnerName);
        TransportStateService.DomibusConnectorTransportState state =
                new TransportStateService.DomibusConnectorTransportState();
        state.setConnectorTransportId(transportFor);
        state.setLinkPartner(linkPartner);
        state.setRemoteMessageId("Testbackend_" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        state.setStatus(TransportState.ACCEPTED);
        transportStateService.updateTransportStatus(state);
    }

    public void setDomibusConnectorLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        this.linkPartner = linkPartner;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
