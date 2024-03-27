package eu.domibus.connector.tools.logging;

import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import org.slf4j.MDC;


public class MDCHelper {
    public static void setProcessor(String processor) {
        MDC.put(LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME, processor);
    }

    public static void clearProcessor() {
        MDC.remove(LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME);
    }
}
