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

import eu.ecodex.connector.common.service.ConfigurationPropertyManagerService;
import eu.ecodex.connector.controller.exception.DomibusConnectorMessageExceptionBuilder;
import eu.ecodex.connector.controller.processor.steps.BuildEcodexContainerStep;
import eu.ecodex.connector.controller.processor.steps.CreateNewBusinessMessageInDbStep;
import eu.ecodex.connector.controller.processor.steps.GenerateEbmsIdStep;
import eu.ecodex.connector.controller.processor.steps.LookupGatewayNameStep;
import eu.ecodex.connector.controller.processor.steps.MessageConfirmationStep;
import eu.ecodex.connector.controller.processor.steps.SubmitConfirmationAsEvidenceMessageStep;
import eu.ecodex.connector.controller.processor.steps.SubmitMessageToLinkModuleQueueStep;
import eu.ecodex.connector.controller.processor.steps.VerifyPModesStep;
import eu.ecodex.connector.controller.processor.util.ConfirmationCreatorService;
import eu.ecodex.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.ecodex.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Takes a originalMessage from backend and creates evidences for it and also wraps it into
 * an asic container and delivers the originalMessage to the gateway.
 */
@Component
@SuppressWarnings("squid:S1135")
public class ToGatewayBusinessMessageProcessor implements DomibusConnectorMessageProcessor {
    public static final String BACKEND_TO_GATEWAY_MESSAGE_PROCESSOR_BEAN_NAME =
        "ToGatewayBusinessMessageProcessor";
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ToGatewayBusinessMessageProcessor.class);
    private final CreateNewBusinessMessageInDbStep createNewBusinessMessageInDBStep;
    private final BuildEcodexContainerStep buildECodexContainerStep;
    private final SubmitMessageToLinkModuleQueueStep submitMessageToLinkStep;
    private final MessageConfirmationStep messageConfirmationStep;
    private final ConfirmationCreatorService confirmationCreatorService;
    private final SubmitConfirmationAsEvidenceMessageStep submitAsEvidenceMessageToLink;
    private final LookupGatewayNameStep lookupGatewayNameStep;
    private final GenerateEbmsIdStep generateEbmsIdStep;
    private final VerifyPModesStep verifyPModesStep;
    private final ConfigurationPropertyManagerService configurationPropertyManagerService;

    /**
     * Class representing a processor that handles the processing of a business message
     * from backend to the gateway.
     */
    public ToGatewayBusinessMessageProcessor(
        CreateNewBusinessMessageInDbStep createNewBusinessMessageInDBStep,
        BuildEcodexContainerStep buildECodexContainerStep,
        SubmitMessageToLinkModuleQueueStep submitMessageToLinkStep,
        MessageConfirmationStep messageConfirmationStep,
        ConfirmationCreatorService confirmationCreatorService,
        SubmitConfirmationAsEvidenceMessageStep submitAsEvidenceMessageToLink,
        LookupGatewayNameStep lookupGatewayNameStep,
        GenerateEbmsIdStep generateEbmsIdStep,
        VerifyPModesStep verifyPModesStep,
        ConfigurationPropertyManagerService configurationPropertyManagerService) {
        this.submitAsEvidenceMessageToLink = submitAsEvidenceMessageToLink;
        this.createNewBusinessMessageInDBStep = createNewBusinessMessageInDBStep;
        this.buildECodexContainerStep = buildECodexContainerStep;
        this.submitMessageToLinkStep = submitMessageToLinkStep;
        this.messageConfirmationStep = messageConfirmationStep;
        this.confirmationCreatorService = confirmationCreatorService;
        this.lookupGatewayNameStep = lookupGatewayNameStep;
        this.generateEbmsIdStep = generateEbmsIdStep;
        this.verifyPModesStep = verifyPModesStep;
        this.configurationPropertyManagerService = configurationPropertyManagerService;
    }

    /**
     * Process a business message from backend to the gateway.
     *
     * @param message The business message to process
     */
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME,
        value = BACKEND_TO_GATEWAY_MESSAGE_PROCESSOR_BEAN_NAME
    )
    public void processMessage(DomibusConnectorMessage message) {
        try (var closable = org.slf4j.MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_BACKEND_MESSAGE_ID_PROPERTY_NAME,
            message.getMessageDetails().getBackendMessageId()
        )) {
            verifyPModesStep.verifyOutgoing(message);

            // buildEcodexContainerStep
            buildECodexContainerStep.executeStep(message);

            // set gateway name
            lookupGatewayNameStep.executeStep(message);

            // set ebms id, the EBMS id is set here, because it might be possible,
            // that a RELAY_REEMD_EVIDENCE
            // comes back from the remote connector before this connector has already received
            // the by the sending GW created EBMSID
            generateEbmsIdStep.executeStep(message);

            // persistence step
            createNewBusinessMessageInDBStep.executeStep(message);

            // create confirmation
            DomibusConnectorMessageConfirmation submissionAcceptanceConfirmation =
                confirmationCreatorService.createConfirmation(
                    DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message, null, null);
            // process created confirmation for message
            messageConfirmationStep.processConfirmationForMessage(
                message, submissionAcceptanceConfirmation);
            // append confirmation to message
            message.getTransportedMessageConfirmations().add(submissionAcceptanceConfirmation);

            // submit message to GW
            submitMessageToLinkStep.submitMessage(message);
            // submit evidence message to BACKEND
            // TODO: do this after submitMessage was successful! offload into TransportStateService
            submitAsEvidenceMessageToLink.submitOppositeDirection(
                null, message, submissionAcceptanceConfirmation);

            LOGGER.info(
                LoggingMarker.BUSINESS_LOG,
                "Put message with backendId [{}] to Gateway Link [{}] on toLink Queue.",
                message.getMessageDetails().getBackendMessageId(),
                message.getMessageDetails().getGatewayName()
            );
        } catch (DomibusConnectorEvidencesToolkitException ete) {
            LOGGER.error(
                "Could not generate evidence [{}] for originalMessage [{}]!",
                DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message
            );

            DomibusConnectorMessageConfirmation submissionRejection =
                confirmationCreatorService.createConfirmation(
                    DomibusConnectorEvidenceType.SUBMISSION_REJECTION, message,
                    DomibusConnectorRejectionReason.OTHER, ""
                );
            submitAsEvidenceMessageToLink.submitOppositeDirection(
                null, message, submissionRejection
            );

            DomibusConnectorMessageExceptionBuilder.createBuilder()
                .setMessage(message)
                .setText("Could not generate evidence submission acceptance! ")
                .setSource(this.getClass())
                .setCause(ete)
                .buildAndThrow();
        }
    }
}
