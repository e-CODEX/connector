/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.processor.steps;

import eu.ecodex.connector.controller.spring.ConnectorMessageProcessingProperties;
import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDetails;
import eu.ecodex.connector.domain.model.DomibusConnectorParty;
import eu.ecodex.connector.domain.model.DomibusConnectorService;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.persistence.service.DomibusConnectorPModeService;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This class is responsible for verifying PModes for outgoing and incoming messages in the Domibus
 * Connector.
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

    private boolean executeStep(
        DomibusConnectorMessage domibusConnectorMessage,
        ConnectorMessageProcessingProperties.PModeVerificationMode verificationMode) {
        LOGGER.debug("Verifying PModes with verification mode [{}]", verificationMode);
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId =
            domibusConnectorMessage.getMessageLaneId();
        DomibusConnectorMessageDetails messageDetails = domibusConnectorMessage.getMessageDetails();

        if (verificationMode
            == ConnectorMessageProcessingProperties.PModeVerificationMode.RELAXED) {

            Optional<DomibusConnectorAction> action =
                connectorPModeService.getConfiguredSingle(
                    businessDomainId, messageDetails.getAction()
                );
            if (action.isPresent()) {
                messageDetails.setAction(action.get());
            } else {
                LOGGER.warn(
                    LoggingMarker.Log4jMarker.BUSINESS_LOG,
                    "The action [{}] is not configured on connector. Check your uploaded p-Modes!",
                    messageDetails.getAction()
                );
                // TODO: improve exception
                throw new RuntimeException(
                    "error, action not configured:" + messageDetails.getAction());
            }

            Optional<DomibusConnectorService> service =
                connectorPModeService.getConfiguredSingle(
                    businessDomainId, messageDetails.getService()
                );
            if (service.isPresent()) {
                messageDetails.setService(service.get());
            } else {
                LOGGER.warn(
                    LoggingMarker.Log4jMarker.BUSINESS_LOG,
                    "The service [{}] is not configured on connector. Check your uploaded p-Modes!",
                    messageDetails.getService()
                );
                // TODO: improve exception
                throw new RuntimeException(
                    "error, service not configured!" + messageDetails.getService());
            }

            if (!StringUtils.hasText(messageDetails.getToParty().getPartyIdType())) {
                LOGGER.debug(
                    "PMode verification mode is relaxed. Assuming ToParty PartyIdType [{}]"
                        + "as empty!",
                    messageDetails.getToParty().getPartyIdType()
                );
                messageDetails.getToParty().setPartyIdType(null);
            }

            Optional<DomibusConnectorParty> toParty =
                connectorPModeService.getConfiguredSingle(
                    businessDomainId, messageDetails.getToParty()
                );
            if (toParty.isPresent()) {
                messageDetails.setToParty(toParty.get());
            } else {
                LOGGER.warn(
                    LoggingMarker.Log4jMarker.BUSINESS_LOG,
                    "The toParty [{}] is not configured on connector. Check your uploaded "
                        + "p-Modes!",
                    messageDetails.getToParty()
                );
                // TODO: improve exception
                throw new RuntimeException(
                    "error, party not configured:" + messageDetails.getToParty());
            }

            if (!StringUtils.hasText(messageDetails.getFromParty().getPartyIdType())) {
                LOGGER.debug(
                    "PMode verification mode is relaxed. Assuming FromParty PartyIdType "
                        + "[{}] as empty!",
                    messageDetails.getFromParty().getPartyIdType()
                );
                messageDetails.getFromParty().setPartyIdType(null);
            }
            Optional<DomibusConnectorParty> fromParty =
                connectorPModeService.getConfiguredSingle(
                    businessDomainId, messageDetails.getFromParty()
                );
            if (fromParty.isPresent()) {
                messageDetails.setFromParty(fromParty.get());
            } else {
                LOGGER.warn(
                    LoggingMarker.Log4jMarker.BUSINESS_LOG,
                    "The toParty [{}] is not configured on connector. Check your uploaded "
                        + "p-Modes!",
                    messageDetails.getFromParty()
                );
                // TODO: improve exception
                throw new RuntimeException(
                    "error, party not configured:" + messageDetails.getFromParty());
            }
        }
        if (verificationMode == ConnectorMessageProcessingProperties.PModeVerificationMode.CREATE) {
            LOGGER.warn("PMode verification mode "
                            + ConnectorMessageProcessingProperties.PModeVerificationMode.CREATE
                            + " is not supported!");
        }
        if (verificationMode == ConnectorMessageProcessingProperties.PModeVerificationMode.STRICT) {
            LOGGER.warn("PMode verification mode "
                            + ConnectorMessageProcessingProperties.PModeVerificationMode.STRICT
                            + " is experimental feature!");
            // just don't complete p-Mode information => messagePersist has to do the work
        }

        return true;
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
