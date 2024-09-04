/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.queues.listener;

import static eu.ecodex.connector.controller.queues.JmsConfiguration.TO_LINK_QUEUE_BEAN;

import eu.ecodex.connector.common.service.CurrentBusinessDomain;
import eu.ecodex.connector.controller.service.SubmitToLinkService;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * This class represents a listener for messages to be sent to a link partner.
 */
@Component
public class ToLinkPartnerListener {
    private static final Logger LOGGER = LogManager.getLogger(ToLinkPartnerListener.class);
    private final SubmitToLinkService submitToLink;

    public ToLinkPartnerListener(SubmitToLinkService submitToLink) {
        this.submitToLink = submitToLink;
    }

    /**
     * This method handles a message received by the ToLinkPartnerListener.
     * It retrieves the message ID, sets up the MDC context, and submits the message to the link.
     * If an exception occurs during submission, the transaction is rolled back and
     * the exception is rethrown.
     * Finally, the MDC context is cleared.
     *
     * @param message The DomibusConnectorMessage to be handled
     */
    @JmsListener(destination = TO_LINK_QUEUE_BEAN)
    @Transactional(rollbackFor = Throwable.class)
    @eu.ecodex.connector.lib.logging.MDC(
        name = LoggingMDCPropertyNames.MDC_DC_QUEUE_LISTENER_PROPERTY_NAME,
        value = "ToLinkPartnerListener"
    )
    public void handleMessage(DomibusConnectorMessage message) {
        var messageId = message.getConnectorMessageId().toString();
        try (var mdcCloseable = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
            messageId
        )) {
            CurrentBusinessDomain.setCurrentBusinessDomain(message.getMessageLaneId());
            submitToLink.submitToLink(message);
        } catch (Throwable throwable) {
            // because
            // DomibusConnectorSubmitToLinkException extends
            // DomibusConnectorMessageTransportException
            // DomibusConnectorMessageTransportException extends RuntimeException
            // if we do not catch this, then there won't be rollback nor retry
            LOGGER.error(
                "Cannot submit to link, throwing exception, transaction is rollback, "
                    + "Check DLQ.",
                throwable
            );
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw throwable;
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }
}
