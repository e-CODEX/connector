/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.processor;

import eu.ecodex.connector.controller.exception.DomibusConnectorControllerException;
import eu.ecodex.connector.controller.exception.DomibusConnectorMessageException;
import eu.ecodex.connector.controller.exception.DomibusConnectorMessageExceptionBuilder;
import eu.ecodex.connector.controller.processor.steps.CreateNewBusinessMessageInDbStep;
import eu.ecodex.connector.controller.processor.steps.LookupBackendNameStep;
import eu.ecodex.connector.controller.processor.steps.MessageConfirmationStep;
import eu.ecodex.connector.controller.processor.steps.ResolveEcodexContainerStep;
import eu.ecodex.connector.controller.processor.steps.SubmitConfirmationAsEvidenceMessageStep;
import eu.ecodex.connector.controller.processor.steps.SubmitMessageToLinkModuleQueueStep;
import eu.ecodex.connector.controller.processor.steps.VerifyPModesStep;
import eu.ecodex.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.ecodex.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.evidences.DomibusConnectorEvidencesToolkit;
import eu.ecodex.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.security.exception.DomibusConnectorSecurityException;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for processing business messages received by the Domibus Connector
 * and performing various steps involved in the processing.
 */
@Component(ToBackendBusinessMessageProcessor.GATEWAY_TO_BACKEND_MESSAGE_PROCESSOR)
public class ToBackendBusinessMessageProcessor implements DomibusConnectorMessageProcessor {
    public static final String GATEWAY_TO_BACKEND_MESSAGE_PROCESSOR
        = "GatewayToBackendMessageProcessor";
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ToBackendBusinessMessageProcessor.class);
    private final DomibusConnectorEvidencesToolkit evidencesToolkit;
    private final MessageConfirmationStep messageConfirmationStep;
    private final ResolveEcodexContainerStep resolveECodexContainerStep;
    private final CreateNewBusinessMessageInDbStep createNewBusinessMessageInDBStep;
    private final SubmitMessageToLinkModuleQueueStep submitMessageToLinkModuleQueueStep;
    private final LookupBackendNameStep lookupBackendNameStep;
    private final SubmitConfirmationAsEvidenceMessageStep submitAsEvidenceMessageToLink;
    private final VerifyPModesStep verifyPModesStep;

    /**
     * This class represents a processor for backend business messages.
     * It processes incoming messages by performing several steps, such as message confirmation,
     * resolving eCodex container, creating a new business message in the database,
     * submitting the message to the link module queue, submitting the confirmation as evidence
     * message, looking up the backend name, and verifying PModes.
     */
    public ToBackendBusinessMessageProcessor(
        DomibusConnectorEvidencesToolkit evidencesToolkit,
        MessageConfirmationStep messageConfirmationStep,
        ResolveEcodexContainerStep resolveECodexContainerStep,
        CreateNewBusinessMessageInDbStep createNewBusinessMessageInDBStep,
        SubmitMessageToLinkModuleQueueStep submitMessageToLinkModuleQueueStep,
        SubmitConfirmationAsEvidenceMessageStep submitAsEvidenceMessageToLink,
        LookupBackendNameStep lookupBackendNameStep, VerifyPModesStep verifyPModesStep) {
        this.evidencesToolkit = evidencesToolkit;
        this.submitAsEvidenceMessageToLink = submitAsEvidenceMessageToLink;
        this.messageConfirmationStep = messageConfirmationStep;
        this.resolveECodexContainerStep = resolveECodexContainerStep;
        this.createNewBusinessMessageInDBStep = createNewBusinessMessageInDBStep;
        this.submitMessageToLinkModuleQueueStep = submitMessageToLinkModuleQueueStep;
        this.lookupBackendNameStep = lookupBackendNameStep;
        this.verifyPModesStep = verifyPModesStep;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME,
        value = GATEWAY_TO_BACKEND_MESSAGE_PROCESSOR
    )
    public void processMessage(final DomibusConnectorMessage incomingMessage) {
        try (var closable = org.slf4j.MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_EBMS_MESSAGE_ID_PROPERTY_NAME,
            incomingMessage.getMessageDetails().getEbmsMessageId()
        )) {
            // verify pModes
            verifyPModesStep.verifyIncoming(incomingMessage);

            // lookup correct backend name
            lookupBackendNameStep.executeStep(incomingMessage);

            // persistMessage
            createNewBusinessMessageInDBStep.executeStep(incomingMessage);

            // process all with this business message transported confirmations
            messageConfirmationStep.processTransportedConfirmations(incomingMessage);

            // Create relayREMMD
            @SuppressWarnings("squid:S1135")
            // TODO: decide if this should maybe generated after msg successfully transported
            //  to backend link?
            DomibusConnectorMessageConfirmation relayREMMDEvidence =
                createRelayREMMDEvidence(incomingMessage, true);
            // process created relayREMMD (save it to db and attach it to transported message)
            messageConfirmationStep.processConfirmationForMessage(
                incomingMessage, relayREMMDEvidence);

            // send confirmation msg back
            submitAsEvidenceMessageToLink.submitOppositeDirection(
                null, incomingMessage, relayREMMDEvidence);

            // resolve ecodex-Container
            resolveECodexContainerStep.executeStep(incomingMessage);

            submitMessageToLinkModuleQueueStep.submitMessage(incomingMessage);

            LOGGER.info(
                LoggingMarker.BUSINESS_LOG,
                "Put processed incoming Business Message with EBMS ID [{}] from GW to Backend "
                    + "Link [{}] on to Link Queue",
                incomingMessage.getMessageDetails().getEbmsMessageId(),
                incomingMessage.getMessageDetails().getConnectorBackendClientName()
            );
        } catch (DomibusConnectorSecurityException e) {
            LOGGER.warn(
                "Security Exception occurred! Responding with RelayRemmdRejection "
                    + "ConfirmationMessage",
                e
            );
            DomibusConnectorMessageConfirmation negativeEvidence =
                createNonDeliveryEvidence(incomingMessage);
            messageConfirmationStep.processConfirmationForMessage(
                incomingMessage, negativeEvidence);
            // respond with negative evidence...
            submitAsEvidenceMessageToLink.submitOppositeDirection(
                null, incomingMessage, negativeEvidence);
            LOGGER.warn(
                LoggingMarker.BUSINESS_LOG,
                "Rejected processed incoming Business Message with EBMS ID [{}] from GW to "
                    + "Backend Link [{}] due security exception",
                incomingMessage.getMessageDetails().getEbmsMessageId(),
                incomingMessage.getMessageDetails().getConnectorBackendClientName()
            );
        }
    }

    private DomibusConnectorMessageConfirmation createNonDeliveryEvidence(
        DomibusConnectorMessage originalMessage)
        throws DomibusConnectorControllerException, DomibusConnectorMessageException {
        try {
            return evidencesToolkit.createEvidence(DomibusConnectorEvidenceType.NON_DELIVERY,
                                                   originalMessage,
                                                   DomibusConnectorRejectionReason.OTHER, ""
            );
        } catch (DomibusConnectorEvidencesToolkitException e) {
            DomibusConnectorMessageException evidenceBuildFailed =
                DomibusConnectorMessageExceptionBuilder.createBuilder()
                    .setMessage(originalMessage)
                    .setText("Error creating NonDelivery evidence for originalMessage!")
                    .setSource(this.getClass())
                    .setCause(e)
                    .build();
            LOGGER.error("Failed to create Evidence", evidenceBuildFailed);
            throw evidenceBuildFailed;
        }
    }

    private DomibusConnectorMessageConfirmation createRelayREMMDEvidence(
        DomibusConnectorMessage originalMessage, boolean isAcceptance)
        throws DomibusConnectorControllerException, DomibusConnectorMessageException {

        try {
            if (isAcceptance) {
                LOGGER.trace("relay is acceptance, generating RELAY_REMMD_ACCEPTANCE");
                return evidencesToolkit.createEvidence(
                    DomibusConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE,
                    originalMessage,
                    DomibusConnectorRejectionReason.OTHER, ""
                );
            } else {
                LOGGER.trace("relay is denied, generating RELAY_REMMD_REJECTION");
                return evidencesToolkit.createEvidence(
                    DomibusConnectorEvidenceType.RELAY_REMMD_REJECTION,
                    originalMessage,
                    DomibusConnectorRejectionReason.OTHER, ""
                );
            }
        } catch (DomibusConnectorEvidencesToolkitException e) {
            DomibusConnectorMessageException evidenceBuildFailed =
                DomibusConnectorMessageExceptionBuilder.createBuilder()
                    .setMessage(originalMessage)
                    .setText("Error creating RelayREMMD evidence for originalMessage!")
                    .setSource(this.getClass())
                    .setCause(e)
                    .build();
            LOGGER.error("Failed to create Evidence", evidenceBuildFailed);
            throw evidenceBuildFailed;
        }
    }
}
