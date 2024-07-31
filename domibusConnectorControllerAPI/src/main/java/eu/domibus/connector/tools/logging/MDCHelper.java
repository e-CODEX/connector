/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.tools.logging;

import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import lombok.experimental.UtilityClass;
import org.slf4j.MDC;

/**
 * The MDCHelper class provides utility methods to manipulate the Mapped Diagnostic Context (MDC)
 * for logging purposes.
 */
@UtilityClass
public class MDCHelper {
    public static void setProcessor(String processor) {
        MDC.put(LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME, processor);
    }

    public static void clearProcessor() {
        MDC.remove(LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME);
    }
}
