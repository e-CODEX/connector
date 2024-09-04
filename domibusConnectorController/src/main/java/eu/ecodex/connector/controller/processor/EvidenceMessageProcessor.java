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

import eu.ecodex.connector.controller.exception.DCEvidenceNotRelevantException;
import eu.ecodex.connector.controller.processor.steps.EvidenceTriggerStep;
import eu.ecodex.connector.controller.processor.steps.MessageConfirmationStep;
import eu.ecodex.connector.controller.processor.steps.SubmitConfirmationAsEvidenceMessageStep;
import eu.ecodex.connector.controller.processor.steps.ValidateMessageConfirmationStep;
import eu.ecodex.connector.controller.processor.util.FindBusinessMessageByMsgId;
import eu.ecodex.connector.controller.queues.producer.ToCleanupQueue;
import eu.ecodex.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.domain.model.helper.DomainModelHelper;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * The EvidenceMessageProcessor class is responsible for processing evidence messages.
 * It implements the DomibusConnectorMessageProcessor interface.
 */
@Component
public class EvidenceMessageProcessor implements DomibusConnectorMessageProcessor {
    private static final Logger LOGGER = LogManager.getLogger(EvidenceMessageProcessor.class);
    private final EvidenceTriggerStep evidenceTriggerStep;
    private final FindBusinessMessageByMsgId findBusinessMessageByMsgId;
    private final MessageConfirmationStep messageConfirmationStep;
    private final ValidateMessageConfirmationStep validateMessageConfirmationStep;
    private final SubmitConfirmationAsEvidenceMessageStep submitConfirmationAsEvidenceMessageStep;
    private final ToCleanupQueue cleanupQueue;

    /**
     * The EvidenceMessageProcessor class is responsible for processing evidence message.
     */
    public EvidenceMessageProcessor(
        EvidenceTriggerStep evidenceTriggerStep,
        FindBusinessMessageByMsgId findBusinessMessageByMsgId,
        MessageConfirmationStep messageConfirmationStep,
        ValidateMessageConfirmationStep validateMessageConfirmationStep,
        SubmitConfirmationAsEvidenceMessageStep submitConfirmationAsEvidenceMessageStep,
        ToCleanupQueue cleanupQueue) {
        this.evidenceTriggerStep = evidenceTriggerStep;
        this.findBusinessMessageByMsgId = findBusinessMessageByMsgId;
        this.messageConfirmationStep = messageConfirmationStep;
        this.validateMessageConfirmationStep = validateMessageConfirmationStep;
        this.submitConfirmationAsEvidenceMessageStep = submitConfirmationAsEvidenceMessageStep;
        this.cleanupQueue = cleanupQueue;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME,
        value = "EvidenceMessageProcessor"
    )
    public void processMessage(DomibusConnectorMessage message) {
        try {
            boolean isEvidenceTrigger = DomainModelHelper.isEvidenceTriggerMessage(message);
            if (isEvidenceTrigger) {
                evidenceTriggerStep.executeStep(message);
            }

            var revertedDirection = DomibusConnectorMessageDirection.revert(
                message.getMessageDetails().getDirection()
            );
            DomibusConnectorMessage businessMsg =
                findBusinessMessageByMsgId.findBusinessMessageByIdAndDirection(
                    message,
                    revertedDirection
                );

            // set ref to backend message id (backendId of business message)
            String businessMessageBackendId = businessMsg.getMessageDetails().getBackendMessageId();
            LOGGER.debug("Setting refToBackendMessageId to [{}]", businessMessageBackendId);
            message.getMessageDetails().setRefToBackendMessageId(businessMessageBackendId);

            // set ref to message id (EBMSID of business message)
            String businessMessageEbmsId = businessMsg.getMessageDetails().getEbmsMessageId();
            LOGGER.debug("Setting refToMessageId to [{}]", businessMessageEbmsId);
            message.getMessageDetails().setRefToMessageId(businessMessageEbmsId);

            validateMessageConfirmationStep.executeStep(message);

            DomibusConnectorMessageConfirmation transportedConfirmation =
                message.getTransportedMessageConfirmations().getFirst();

            messageConfirmationStep.processConfirmationForMessage(
                businessMsg, transportedConfirmation
            );

            // if business message is rejected, confirmed trigger cleanup routine
            if (businessMsg.getMessageDetails().getConfirmed() != null
                || businessMsg.getMessageDetails().getRejected() != null) {
                cleanupQueue.putOnQueue(businessMsg);
            }

            submitConfirmationAsEvidenceMessageStep.submitOppositeDirection(
                message.getConnectorMessageId(), businessMsg, transportedConfirmation);

            if (isEvidenceTrigger && submitConfirmationAsEvidenceMessageStep
                .isSendCreatedTriggerEvidenceBack(businessMsg.getMessageLaneId())) {
                // send generated evidence back...this would be the same direction as the business
                // message...with new messageId
                LOGGER.debug("Sending by trigger created confirmation message back to backend");
                submitConfirmationAsEvidenceMessageStep.submitSameDirection(
                    null, businessMsg, transportedConfirmation);
            }

            LOGGER.info(
                LoggingMarker.Log4jMarker.BUSINESS_LOG,
                "Successfully processed evidence [{}] in direction [{}] for business message [{}]",
                transportedConfirmation.getEvidenceType(),
                message.getMessageDetails().getDirection(),
                businessMsg.getConnectorMessageId()
            );
        } catch (DCEvidenceNotRelevantException dcEvidenceNotRelevantException) {
            LOGGER.warn(
                LoggingMarker.Log4jMarker.BUSINESS_LOG,
                dcEvidenceNotRelevantException.getMessage()
            );
            LOGGER.debug(
                dcEvidenceNotRelevantException.getMessage(), dcEvidenceNotRelevantException);
        }
    }
}
