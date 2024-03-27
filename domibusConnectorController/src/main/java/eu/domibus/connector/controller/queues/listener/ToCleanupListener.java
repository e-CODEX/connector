package eu.domibus.connector.controller.queues.listener;


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

import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CLEANUP_QUEUE_BEAN;


@Component
public class ToCleanupListener {
    private static final Logger LOGGER = LogManager.getLogger(ToCleanupListener.class);
    private final CleanupMessageProcessor cleanupMessageProcessor;

    public ToCleanupListener(CleanupMessageProcessor cleanupMessageProcessor) {
        this.cleanupMessageProcessor = cleanupMessageProcessor;
    }

    @JmsListener(destination = TO_CLEANUP_QUEUE_BEAN)
    @Transactional(rollbackFor = Throwable.class)
    @eu.domibus.connector.lib.logging.MDC(
            name = LoggingMDCPropertyNames.MDC_DC_QUEUE_LISTENER_PROPERTY_NAME, value = "ToCleanupListener"
    )
    public void handleMessage(DomibusConnectorMessage message) {
        String messageId = message.getConnectorMessageId().toString();
        try (
                MDC.MDCCloseable mdcCloseable = MDC.putCloseable(
                        LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
                        messageId
                )
        ) {
            CurrentBusinessDomain.setCurrentBusinessDomain(message.getMessageLaneId());
            cleanupMessageProcessor.processMessage(message);
        } catch (Exception exc) {
            LOGGER.error("Cannot cleanup, throwing exception, transaction is rollback, Check DLQ.", exc);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw exc;
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }
}
