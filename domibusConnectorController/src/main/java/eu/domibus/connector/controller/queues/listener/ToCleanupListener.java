/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.queues.listener;

import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CLEANUP_QUEUE_BEAN;

import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.controller.processor.CleanupMessageProcessor;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * The ToCleanupListener class is a component that listens to a JMS queue and handles messages
 * for cleanup.
 * It is responsible for processing the messages and invoking the CleanupMessageProcessor class
 * to perform the cleanup.
 */
@Component
public class ToCleanupListener {
    private static final Logger LOGGER = LogManager.getLogger(ToCleanupListener.class);
    private final CleanupMessageProcessor cleanupMessageProcessor;

    public ToCleanupListener(CleanupMessageProcessor cleanupMessageProcessor) {
        this.cleanupMessageProcessor = cleanupMessageProcessor;
    }

    /**
     * Processes a message received from a JMS queue for cleanup.
     *
     * @param message The message to be processed.
     */
    @JmsListener(destination = TO_CLEANUP_QUEUE_BEAN)
    @Transactional(rollbackFor = Throwable.class)
    @eu.domibus.connector.lib.logging.MDC(
        name = LoggingMDCPropertyNames.MDC_DC_QUEUE_LISTENER_PROPERTY_NAME,
        value = "ToCleanupListener"
    )
    public void handleMessage(DomibusConnectorMessage message) {
        var messageId = message.getConnectorMessageId().toString();
        try (var mdcCloseable = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME, messageId)) {
            CurrentBusinessDomain.setCurrentBusinessDomain(message.getMessageLaneId());
            cleanupMessageProcessor.processMessage(message);
        } catch (Exception exc) {
            LOGGER.error(
                "Cannot cleanup, throwing exception, transaction is rollback, Check DLQ.", exc);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw exc;
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }
}
