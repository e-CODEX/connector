/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.processor.confirmation;

import eu.ecodex.connector.controller.exception.DomibusConnectorControllerException;
import eu.ecodex.connector.controller.exception.DomibusConnectorMessageException;
import eu.ecodex.connector.controller.processor.steps.MessageConfirmationStep;
import eu.ecodex.connector.controller.processor.steps.SubmitConfirmationAsEvidenceMessageStep;
import eu.ecodex.connector.controller.processor.util.ConfirmationCreatorService;
import eu.ecodex.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The CreateEvidenceTimeoutConfirmationStep class is responsible for creating and handling evidence
 * confirmations for timeout scenarios.
 */
@Component
public class CreateEvidenceTimeoutConfirmationStep {
    private static final Logger LOGGER =
        LogManager.getLogger(CreateEvidenceTimeoutConfirmationStep.class);
    private final ConfirmationCreatorService confirmationCreatorService;
    private final SubmitConfirmationAsEvidenceMessageStep submitConfirmationAsEvidenceMessageStep;
    private final MessageConfirmationStep messageConfirmationStep;

    /**
     * Represents a step in the process of creating evidence for a timeout confirmation.
     *
     * @param confirmationCreatorService              The service responsible for creating
     *                                                confirmations.
     * @param submitConfirmationAsEvidenceMessageStep The step responsible for submitting
     *                                                the confirmation as evidence message.
     * @param messageConfirmationStep                 The step responsible for confirming
     *                                                the message.
     */
    public CreateEvidenceTimeoutConfirmationStep(
        ConfirmationCreatorService confirmationCreatorService,
        SubmitConfirmationAsEvidenceMessageStep submitConfirmationAsEvidenceMessageStep,
        MessageConfirmationStep messageConfirmationStep) {
        this.confirmationCreatorService = confirmationCreatorService;
        this.submitConfirmationAsEvidenceMessageStep = submitConfirmationAsEvidenceMessageStep;
        this.messageConfirmationStep = messageConfirmationStep;
    }

    /**
     * Creates a relay remmd failure confirmation and sends it.
     * This method creates a relay remmd failure confirmation for a specified original message and
     * sends it to the backend. The confirmation is created using the ConfirmationCreator
     * Service. If the creation of the confirmation fails, a DomibusConnectorControllerException
     * is thrown. If there is an issue with the original message, a DomibusConnectorMessage
     * Exception is thrown.
     *
     * @param originalMessage The original message for which the relay remmd failure confirmation
     *                        is created.
     * @throws DomibusConnectorControllerException If there is an issue creating the relay remmd
     *                                             failure confirmation.
     * @throws DomibusConnectorMessageException    If there is an issue with the original message.
     * @see ConfirmationCreatorService
     * @see DomibusConnectorControllerException
     * @see DomibusConnectorMessageException
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME,
        value = "CreateRelayRemmdFailureAndSendIt"
    )
    public void createRelayRemmdFailureAndSendIt(DomibusConnectorMessage originalMessage)
        throws DomibusConnectorControllerException, DomibusConnectorMessageException {

        LOGGER.error(
            LoggingMarker.Log4jMarker.BUSINESS_LOG,
            "The RelayREMMDAcceptance/Rejection evidence timeout for "
                + "originalMessage {} timed out. Sending RELAY_REMMD_FAILURE to backend!",
            originalMessage.getMessageDetails().getEbmsMessageId()
        );

        DomibusConnectorMessageConfirmation nonDelivery =
            confirmationCreatorService.createRelayRemmdFailure(
                originalMessage,
                DomibusConnectorRejectionReason.RELAY_REMMD_TIMEOUT
            );
        processConfirmationForMessage(originalMessage, nonDelivery);
    }

    /**
     * Creates a non-delivery confirmation for a specified original message and sends it
     * to the backend.
     *
     * @param originalMessage The original message for which the non-delivery confirmation
     *                        is created.
     * @throws DomibusConnectorControllerException If there is an issue creating
     *                                             the non-delivery confirmation.
     * @throws DomibusConnectorMessageException    If there is an issue with the original message.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME,
        value = "CreateNonDeliveryAndSendIt"
    )
    public void createNonDeliveryAndSendIt(DomibusConnectorMessage originalMessage)
        throws DomibusConnectorControllerException, DomibusConnectorMessageException {

        LOGGER.error(
            LoggingMarker.Log4jMarker.BUSINESS_LOG,
            "The Delivery/NonDelivery evidence timeout for originalMessage {} timed out. "
                + "Sending NonDelivery to backend!",
            originalMessage.getMessageDetails().getEbmsMessageId()
        );

        DomibusConnectorMessageConfirmation nonDelivery =
            confirmationCreatorService.createNonDelivery(
                originalMessage,
                DomibusConnectorRejectionReason.DELIVERY_EVIDENCE_TIMEOUT
            );
        processConfirmationForMessage(originalMessage, nonDelivery);
    }

    /**
     * Processes the confirmation for a given message.
     * This method processes the confirmation for a business message, including storing
     * the confirmation and rejecting the business message if necessary. It then submits
     * the confirmation as an evidence message to the backend.
     *
     * @param originalMessage The original message for which the confirmation is being processed.
     * @param confirmation    The confirmation to be processed.
     */
    private void processConfirmationForMessage(DomibusConnectorMessage originalMessage,
                                               DomibusConnectorMessageConfirmation confirmation) {
        // process confirmation for business message (store confirmation, reject business message)
        messageConfirmationStep.processConfirmationForMessage(originalMessage, confirmation);
        // send confirmation to backend
        submitConfirmationAsEvidenceMessageStep.submitOppositeDirection(
            null, originalMessage, confirmation);
    }
}
