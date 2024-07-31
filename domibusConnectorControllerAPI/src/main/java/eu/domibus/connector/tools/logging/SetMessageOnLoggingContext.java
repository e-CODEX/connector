/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.tools.logging;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import lombok.experimental.UtilityClass;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;

/**
 * The SetMessageOnLoggingContext class provides methods to set the connector message ID on the
 * logging MDC context.
 */
@UtilityClass
public class SetMessageOnLoggingContext {
    /**
     * Puts the connector message id of the passed message in the logging MDC context does nothing
     * if domibusConnectorMessage parameter is null.
     *
     * @param domibusConnectorMessage the message
     */
    public static void putConnectorMessageIdOnMDC(
        @Nullable DomibusConnectorMessage domibusConnectorMessage) {
        if (domibusConnectorMessage != null) {
            var connectorMessageId = domibusConnectorMessage.getConnectorMessageIdAsString();
            putConnectorMessageIdOnMDC(connectorMessageId);
        } else {
            putConnectorMessageIdOnMDC((String) null);
        }
    }

    /**
     * Puts the connector message id provided as string in the logging MDC context.
     *
     * @param domibusConnectorMessageId - the String to set on MDC
     */
    public static void putConnectorMessageIdOnMDC(@Nullable String domibusConnectorMessageId) {
        if (domibusConnectorMessageId != null) {
            MDC.put(
                LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
                domibusConnectorMessageId
            );
        } else {
            MDC.remove(LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME);
        }
    }
}
