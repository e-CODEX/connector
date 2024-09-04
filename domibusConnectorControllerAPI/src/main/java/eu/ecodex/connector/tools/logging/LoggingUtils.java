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

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for logging related operations.
 */
@UtilityClass
public class LoggingUtils {
    public static final Level REQUIRED_LEVEL = Level.TRACE;

    /**
     * Logs the given password if the logger's log level is less specific than the required level.
     * Otherwise, returns a message indicating that the logger's log level needs to be increased to
     * see the password.
     *
     * @param logger   the logger instance to log the password
     * @param password the password to be logged
     * @return the logged password or a message indicating that the log level needs to be increased
     */
    public static String logPassword(Logger logger, Object password) {
        if (password == null) {
            return null;
        }
        if (logger.isTraceEnabled() || logger.getLevel().isLessSpecificThan(REQUIRED_LEVEL)) {
            return password.toString();
        } else {
            return String.format(
                "**increase logger [%s] log level to [%s]] to see**", logger, REQUIRED_LEVEL);
        }
    }
}
