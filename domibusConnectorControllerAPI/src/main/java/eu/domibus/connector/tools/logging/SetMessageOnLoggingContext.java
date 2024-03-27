package eu.domibus.connector.tools.logging;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;


public class SetMessageOnLoggingContext {
    /**
     * puts the connector message id of the passed message
     * in the logging MDC context
     * does nothing if domibusConnectorMessage parameter is null
     *
     * @param domibusConnectorMessage the message
     */
    public static void putConnectorMessageIdOnMDC(@Nullable DomibusConnectorMessage domibusConnectorMessage) {
        if (domibusConnectorMessage != null) {
            String connectorMessageId = domibusConnectorMessage.getConnectorMessageIdAsString();
            putConnectorMessageIdOnMDC(connectorMessageId);
        } else {
            putConnectorMessageIdOnMDC((String) null);
        }
    }

    /**
     * puts the connector message id provided as string
     * in the logging MDC context
     *
     * @param domibusConnectorMessageId - the String to set on MDC
     */
    public static void putConnectorMessageIdOnMDC(@Nullable String domibusConnectorMessageId) {
        if (domibusConnectorMessageId != null) {
            MDC.put(LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME, domibusConnectorMessageId);
        } else {
            MDC.remove(LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME);
        }
    }
}
