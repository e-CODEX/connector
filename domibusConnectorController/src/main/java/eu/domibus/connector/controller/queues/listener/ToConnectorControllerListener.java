/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.queues.listener;

import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CONNECTOR_QUEUE_BEAN;

import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.controller.processor.EvidenceMessageProcessor;
import eu.domibus.connector.controller.processor.ToBackendBusinessMessageProcessor;
import eu.domibus.connector.controller.processor.ToGatewayBusinessMessageProcessor;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Controller listener class that handles messages received from the To Connector queue.
 * It processes different types of messages based on their directions and message types.
 */
@Component
public class ToConnectorControllerListener {
    private static final Logger LOGGER = LogManager.getLogger(ToConnectorControllerListener.class);
    private final ToGatewayBusinessMessageProcessor toGatewayBusinessMessageProcessor;
    private final ToBackendBusinessMessageProcessor toBackendBusinessMessageProcessor;
    private final EvidenceMessageProcessor evidenceMessageProcessor;

    /**
     * Listener class that handles messages from the connector queue and processes them.
     */
    public ToConnectorControllerListener(
        ToGatewayBusinessMessageProcessor toGatewayBusinessMessageProcessor,
        ToBackendBusinessMessageProcessor toBackendBusinessMessageProcessor,
        EvidenceMessageProcessor evidenceMessageProcessor) {
        this.toGatewayBusinessMessageProcessor = toGatewayBusinessMessageProcessor;
        this.toBackendBusinessMessageProcessor = toBackendBusinessMessageProcessor;
        this.evidenceMessageProcessor = evidenceMessageProcessor;
    }

    /**
     * Handles a message from the connector queue and processes it.
     *
     * @param message The DomibusConnectorMessage to be processed.
     * @throws IllegalArgumentException if the message or message details are null.
     * @throws IllegalStateException    if an illegal message format is received.
     * @throws Exception                if there is an error in processing the message.
     */
    @JmsListener(destination = TO_CONNECTOR_QUEUE_BEAN)
    @Transactional(rollbackFor = Exception.class)
    @eu.domibus.connector.lib.logging.MDC(
        name = LoggingMDCPropertyNames.MDC_DC_QUEUE_LISTENER_PROPERTY_NAME,
        value = "ToConnectorControllerListener"
    )
    public void handleMessage(DomibusConnectorMessage message) {
        if (message == null || message.getMessageDetails() == null) {
            throw new IllegalArgumentException("Message and Message Details must not be null");
        }
        var messageId = message.getConnectorMessageId().toString();
        try (var mdcCloseable = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
            messageId
        )) {
            CurrentBusinessDomain.setCurrentBusinessDomain(message.getMessageLaneId());
            DomibusConnectorMessageDirection direction = message.getMessageDetails().getDirection();
            if (DomainModelHelper.isEvidenceMessage(message)) {
                evidenceMessageProcessor.processMessage(message);
            } else if (DomainModelHelper.isBusinessMessage(message)
                && direction.getTarget() == MessageTargetSource.GATEWAY) {
                toGatewayBusinessMessageProcessor.processMessage(message);
            } else if (DomainModelHelper.isBusinessMessage(message)
                && direction.getTarget() == MessageTargetSource.BACKEND) {
                toBackendBusinessMessageProcessor.processMessage(message);
            } else {
                throw new IllegalStateException("Illegal Message format received!");
            }
        } catch (Exception exc) {
            LOGGER.error(
                LoggingMarker.Log4jMarker.BUSINESS_LOG,
                "Failed to process message due [{}]! Check Dead Letter Queue and technical "
                    + "logs for details!",
                exc.getMessage()
            );
            String error = "Failed to process message due: " + exc.getMessage();
            LOGGER.error(error, exc);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw exc;
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }
}
