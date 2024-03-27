package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.controller.spring.ConnectorMessageProcessingProperties;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.persistence.service.DomibusConnectorPModeService;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Component
public class VerifyPModesStep {
    private static final Logger LOGGER = LogManager.getLogger(VerifyPModesStep.class);
    private final DomibusConnectorPModeService pModeService;
    private final ConnectorMessageProcessingProperties connectorMessageProcessingProperties;

    public VerifyPModesStep(
            DomibusConnectorPModeService pModeService,
            ConnectorMessageProcessingProperties connectorMessageProcessingProperties) {
        this.pModeService = pModeService;
        this.connectorMessageProcessingProperties = connectorMessageProcessingProperties;
    }

    private boolean executeStep(
            DomibusConnectorMessage domibusConnectorMessage,
            ConnectorMessageProcessingProperties.PModeVerificationMode verificationMode) {
        LOGGER.debug("Verifying PModes with verification mode [{}]", verificationMode);
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId = domibusConnectorMessage.getMessageLaneId();
        DomibusConnectorMessageDetails messageDetails = domibusConnectorMessage.getMessageDetails();

        if (verificationMode == ConnectorMessageProcessingProperties.PModeVerificationMode.RELAXED) {

            Optional<DomibusConnectorAction> action =
                    pModeService.getConfiguredSingle(businessDomainId, messageDetails.getAction());
            if (action.isPresent()) {
                messageDetails.setAction(action.get());
            } else {
                LOGGER.warn(
                        LoggingMarker.Log4jMarker.BUSINESS_LOG,
                        "The action [{}] is not configured on connector. Check your uploaded p-Modes!",
                        messageDetails.getAction()
                );
                // TODO: improve exception
                throw new RuntimeException("error, action not configured:" + messageDetails.getAction());
            }

            Optional<DomibusConnectorService> service =
                    pModeService.getConfiguredSingle(businessDomainId, messageDetails.getService());
            if (service.isPresent()) {
                messageDetails.setService(service.get());
            } else {
                LOGGER.warn(
                        LoggingMarker.Log4jMarker.BUSINESS_LOG,
                        "The service [{}] is not configured on connector. Check your uploaded p-Modes!",
                        messageDetails.getService()
                );
                // TODO: improve exception
                throw new RuntimeException("error, service not configured!" + messageDetails.getService());
            }

            if (!StringUtils.hasText(messageDetails.getToParty().getPartyIdType())) {
                LOGGER.debug(
                        "PMode verification mode is relaxed. Assuming ToParty PartyIdType [{}] as empty!",
                        messageDetails.getToParty().getPartyIdType()
                );
                messageDetails.getToParty().setPartyIdType(null);
            }

            Optional<DomibusConnectorParty> toParty =
                    pModeService.getConfiguredSingle(businessDomainId, messageDetails.getToParty());
            if (toParty.isPresent()) {
                messageDetails.setToParty(toParty.get());
            } else {
                LOGGER.warn(
                        LoggingMarker.Log4jMarker.BUSINESS_LOG,
                        "The toParty [{}] is not configured on connector. Check your uploaded p-Modes!",
                        messageDetails.getToParty()
                );

                // TODO: improve exception
                throw new RuntimeException("error, party not configured:" + messageDetails.getToParty());
            }

            if (!StringUtils.hasText(messageDetails.getFromParty().getPartyIdType())) {
                LOGGER.debug(
                        "PMode verification mode is relaxed. Assuming FromParty PartyIdType [{}] as empty!",
                        messageDetails.getFromParty().getPartyIdType()
                );
                messageDetails.getFromParty().setPartyIdType(null);
            }
            Optional<DomibusConnectorParty> fromParty =
                    pModeService.getConfiguredSingle(businessDomainId, messageDetails.getFromParty());
            if (fromParty.isPresent()) {
                messageDetails.setFromParty(fromParty.get());
            } else {
                LOGGER.warn(
                        LoggingMarker.Log4jMarker.BUSINESS_LOG,
                        "The toParty [{}] is not configured on connector. Check your uploaded p-Modes!",
                        messageDetails.getFromParty()
                );

                // TODO: improve exception
                throw new RuntimeException("error, party not configured:" + messageDetails.getFromParty());
            }
        }
        if (verificationMode == ConnectorMessageProcessingProperties.PModeVerificationMode.CREATE) {
            LOGGER.warn("PMode verification mode " + ConnectorMessageProcessingProperties.PModeVerificationMode.CREATE + " is not supported!");
        }
        if (verificationMode == ConnectorMessageProcessingProperties.PModeVerificationMode.STRICT) {
            LOGGER.warn("PMode verification mode " + ConnectorMessageProcessingProperties.PModeVerificationMode.STRICT + " is experimental feature!");
            // just don't complete p-Mode information => messagePersist has to do the work
        }

        return true;
    }

    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "VerifyPModes")
    public void verifyOutgoing(DomibusConnectorMessage message) {
        if (message.getMessageDetails().getFromParty().getRoleType() == null) {
            message.getMessageDetails().getFromParty().setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        }
        if (message.getMessageDetails().getToParty().getRoleType() == null) {
            message.getMessageDetails().getToParty().setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        }
        executeStep(message, connectorMessageProcessingProperties.getOutgoingPModeVerificationMode());
    }

    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "VerifyPModes")
    public void verifyIncoming(DomibusConnectorMessage message) {
        if (message.getMessageDetails().getFromParty().getRoleType() == null) {
            message.getMessageDetails().getFromParty().setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        }
        if (message.getMessageDetails().getToParty().getRoleType() == null) {
            message.getMessageDetails().getToParty().setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        }
        executeStep(message, connectorMessageProcessingProperties.getIncomingPModeVerificationMode());
    }
}
