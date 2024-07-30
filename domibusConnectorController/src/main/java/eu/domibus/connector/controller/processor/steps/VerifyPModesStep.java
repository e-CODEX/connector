/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.controller.spring.ConnectorMessageProcessingProperties;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.persistence.service.DomibusConnectorPModeService;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This class is responsible for verifying PModes for outgoing and incoming messages
 * in the Domibus Connector.
 */
@Component
public class VerifyPModesStep {
    private static final Logger LOGGER = LogManager.getLogger(VerifyPModesStep.class);
    private final DomibusConnectorPModeService connectorPModeService;
    private final ConnectorMessageProcessingProperties connectorMessageProcessingProperties;

    public VerifyPModesStep(
        DomibusConnectorPModeService connectorPModeService,
        ConnectorMessageProcessingProperties connectorMessageProcessingProperties) {
        this.connectorPModeService = connectorPModeService;
        this.connectorMessageProcessingProperties = connectorMessageProcessingProperties;
    }

    private <T> void checkConfiguredSingle(
        Supplier<Optional<T>> singleSupplier,
        Consumer<T> setter,
        String elementType,
        String elementDetails) {
        Optional<T> single = singleSupplier.get();
        if (single.isPresent()) {
            setter.accept(single.get());
        } else {
            LOGGER.warn(
                LoggingMarker.Log4jMarker.BUSINESS_LOG,
                "The {} [{}] is not configured on connector. Check your uploaded p-Modes!",
                elementType, elementDetails
            );
            throw new RuntimeException(
                "error, " + elementType + " not configured:" + elementDetails);
        }
    }

    private void executeStep(
        DomibusConnectorMessage domibusConnectorMessage,
        ConnectorMessageProcessingProperties.PModeVerificationMode verificationMode) {
        LOGGER.debug("Verifying PModes with verification mode [{}]", verificationMode);
        var businessDomainId = domibusConnectorMessage.getMessageLaneId();
        var messageDetails = domibusConnectorMessage.getMessageDetails();

        if (verificationMode
            == ConnectorMessageProcessingProperties.PModeVerificationMode.RELAXED) {
            checkConfiguredSingle(
                () -> connectorPModeService
                    .getConfiguredSingle(businessDomainId, messageDetails.getAction()),
                messageDetails::setAction,
                "action",
                messageDetails.getAction().toString()
            );

            checkConfiguredSingle(
                () -> connectorPModeService
                    .getConfiguredSingle(businessDomainId, messageDetails.getService()),
                messageDetails::setService,
                "service",
                messageDetails.getService().toString()
            );

            if (!StringUtils.hasText(messageDetails.getToParty().getPartyIdType())) {
                LOGGER.debug(
                    "PMode verification mode is relaxed. Assuming ToParty PartyIdType [{}] "
                        + "as empty!",
                    messageDetails.getToParty().getPartyIdType()
                );
                messageDetails.getToParty().setPartyIdType(null);
            }

            checkConfiguredSingle(
                () -> connectorPModeService
                    .getConfiguredSingle(businessDomainId, messageDetails.getToParty()),
                messageDetails::setToParty,
                "toParty",
                messageDetails.getToParty().toString()
            );

            if (!StringUtils.hasText(messageDetails.getFromParty().getPartyIdType())) {
                LOGGER.debug(
                    "PMode verification mode is relaxed. Assuming FromParty PartyIdType "
                        + "[{}] as empty!",
                    messageDetails.getFromParty().getPartyIdType()
                );
                messageDetails.getFromParty().setPartyIdType(null);
            }

            checkConfiguredSingle(
                () -> connectorPModeService
                    .getConfiguredSingle(businessDomainId, messageDetails.getToParty()),
                messageDetails::setFromParty,
                "fromParty",
                messageDetails.getFromParty().toString()
            );
        }
        if (verificationMode == ConnectorMessageProcessingProperties.PModeVerificationMode.CREATE) {
            LOGGER.warn(
                "PMode verification mode {} is not supported!",
                ConnectorMessageProcessingProperties.PModeVerificationMode.CREATE
            );
        }
        if (verificationMode == ConnectorMessageProcessingProperties.PModeVerificationMode.STRICT) {
            LOGGER.warn(
                "PMode verification mode {} is an experimental feature!",
                ConnectorMessageProcessingProperties.PModeVerificationMode.STRICT
            );
            // just don't complete p-Mode information => messagePersist has to do the work
        }
    }

    /**
     * Verifies the outgoing message.
     *
     * @param message the outgoing message to be verified
     */
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "VerifyPModes")
    public void verifyOutgoing(DomibusConnectorMessage message) {
        if (message.getMessageDetails().getFromParty().getRoleType() == null) {
            message.getMessageDetails().getFromParty()
                .setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        }
        if (message.getMessageDetails().getToParty().getRoleType() == null) {
            message.getMessageDetails().getToParty()
                .setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        }
        executeStep(
            message, connectorMessageProcessingProperties.getOutgoingPModeVerificationMode());
    }

    /**
     * Verifies the incoming message.
     *
     * @param message the incoming message to be verified
     */
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "VerifyPModes")
    public void verifyIncoming(DomibusConnectorMessage message) {
        if (message.getMessageDetails().getFromParty().getRoleType() == null) {
            message.getMessageDetails().getFromParty()
                .setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        }
        if (message.getMessageDetails().getToParty().getRoleType() == null) {
            message.getMessageDetails().getToParty()
                .setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        }
        executeStep(
            message, connectorMessageProcessingProperties.getIncomingPModeVerificationMode());
    }
}
