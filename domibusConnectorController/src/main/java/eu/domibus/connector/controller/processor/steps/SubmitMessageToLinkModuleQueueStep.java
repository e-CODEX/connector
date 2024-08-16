/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.controller.queues.producer.ToLinkQueue;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * This class represents a step in the message processing pipeline that submits the message to
 * the Link Module queue.
 */
@Service
public class SubmitMessageToLinkModuleQueueStep implements MessageProcessStep {
    private static final Logger LOGGER =
        LogManager.getLogger(SubmitMessageToLinkModuleQueueStep.class);
    private final ToLinkQueue toLinkQueue;

    public SubmitMessageToLinkModuleQueueStep(ToLinkQueue toLinkQueue) {
        this.toLinkQueue = toLinkQueue;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
        value = "SubmitMessageToLinkModuleQueueStep")
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        submitMessage(domibusConnectorMessage);
        return true;
    }

    public void submitMessage(DomibusConnectorMessage message) {
        toLinkQueue.putOnQueue(message);
    }

    /**
     * Submits message to ConnectorToLinkQueue,
     * but before this is done, the following message details are overwritten with
     * the switched originalMessageDetails
     * see {@link DomainModelHelper#switchMessageDirection(DomibusConnectorMessageDetails)}.
     *
     * <ul>
     *     <li>direction</li>
     *     <li>fromParty</li>
     *     <li>toParty</li>
     *     <li>originalSender</li>
     *     <li>finalRecipient</li>
     * </ul>
     *
     * @param originalMessage The original message for which the direction details will be switched.
     * @param message         The message to be submitted with the opposite direction details.
     */
    public void submitMessageOpposite(DomibusConnectorMessage originalMessage,
                                      DomibusConnectorMessage message) {
        DomibusConnectorMessageDetails switchedDetails =
            DomainModelHelper.switchMessageDirection(originalMessage.getMessageDetails());
        DomibusConnectorMessageDetails msgDetails = message.getMessageDetails();

        msgDetails.setDirection(switchedDetails.getDirection());
        msgDetails.setFromParty(switchedDetails.getFromParty());
        msgDetails.setToParty(switchedDetails.getToParty());
        msgDetails.setOriginalSender(switchedDetails.getOriginalSender());
        msgDetails.setFinalRecipient(switchedDetails.getFinalRecipient());
        LOGGER.debug("Message Direction attributes are changed to [{}]", msgDetails);
        submitMessage(message);
    }
}
