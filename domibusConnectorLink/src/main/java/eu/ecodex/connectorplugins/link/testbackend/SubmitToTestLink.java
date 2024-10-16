/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connectorplugins.link.testbackend;

import eu.ecodex.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.ecodex.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.ecodex.connector.controller.service.SubmitToConnector;
import eu.ecodex.connector.controller.service.TransportStateService;
import eu.ecodex.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.enums.TransportState;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorMessageConfirmationBuilder;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorMessageDetailsBuilder;
import eu.ecodex.connector.domain.model.helper.DomainModelHelper;
import eu.ecodex.connector.link.service.SubmitToLinkPartner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for submitting a message to the test link partner.
 */
@Component
@Getter
@Setter
public class SubmitToTestLink implements SubmitToLinkPartner {
    private static final Logger LOGGER = LogManager.getLogger(SubmitToTestLink.class);
    private final SubmitToConnector submitToConnector;
    private final TransportStateService transportStateService;
    private final DomibusConnectorMessageIdGenerator messageIdGenerator;
    private boolean enabled;
    private DomibusConnectorLinkPartner linkPartner;

    /**
     * Initializes a new instance of the SubmitToTestLink class.
     *
     * @param submitToConnector     the SubmitToConnector implementation to submit the message
     * @param transportStateService the TransportStateService implementation to handle the transport
     *                              state of the message
     * @param messageIdGenerator    the DomibusConnectorMessageIdGenerator implementation to
     *                              generate a unique message id for the connector
     */
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
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName)
        throws DomibusConnectorSubmitToLinkException {
        if (DomainModelHelper.isBusinessMessage(message)) {
            if (this.enabled) {
                String ebmsMessageId = message.getMessageDetails().getEbmsMessageId();

                DomibusConnectorMessage deliveryConfirmation = DomibusConnectorMessageBuilder
                    .createBuilder()
                    .setMessageDetails(
                        DomibusConnectorMessageDetailsBuilder
                            .create()
                            .withRefToMessageId(
                                ebmsMessageId
                            ) // set ref to message id to ebms id
                            .build()
                    )
                    .setConnectorMessageId(
                        messageIdGenerator.generateDomibusConnectorMessageId())
                    .setMessageLaneId(message.getMessageLaneId())
                    .addTransportedConfirmations(
                        DomibusConnectorMessageConfirmationBuilder
                            .createBuilder() // append evidence trigger of type DELIVERY
                            .setEvidenceType(
                                DomibusConnectorEvidenceType.DELIVERY)
                            .setEvidence(
                                new byte[0])
                            .build())
                    .build();

                submitToConnector.submitToConnector(
                    deliveryConfirmation, linkPartnerName,
                    LinkType.BACKEND
                ); // submit trigger message to connector

                LOGGER.info(
                    "Generated Delivery evidence trigger message for connector test "
                        + "message with EBMS ID [{}]",
                    message.getConnectorMessageId()
                );
            } else {
                LOGGER.warn(
                    "Test message received, but test backend is not enabled! No response will be "
                        + "sent!"
                );
            }
        }
        TransportStateService.TransportId transportFor =
            transportStateService.createTransportFor(message, linkPartnerName);
        var state = new TransportStateService.DomibusConnectorTransportState();
        state.setConnectorTransportId(transportFor);
        state.setLinkPartner(linkPartner);
        state.setRemoteMessageId(
            "Testbackend_" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        state.setStatus(TransportState.ACCEPTED);
        transportStateService.updateTransportStatus(state);
    }

    public void setDomibusConnectorLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        this.linkPartner = linkPartner;
    }
}
