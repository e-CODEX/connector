/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
