/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.tools.logging;

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
