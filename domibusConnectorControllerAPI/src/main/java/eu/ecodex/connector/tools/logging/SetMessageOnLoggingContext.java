/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.tools.logging;

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
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
