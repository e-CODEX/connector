package eu.domibus.connector.tools.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;


public class LoggingUtils {
    public static final Level REQUIRED_LEVEL = Level.TRACE;

    public static final String logPassword(Logger logger, Object password) {
        if (password == null) {
            return null;
        }
        if (logger.isTraceEnabled() || logger.getLevel().isLessSpecificThan(REQUIRED_LEVEL)) {
            return password.toString();
        } else {
            return String.format("**increase logger [%s] log level to [%s]] to see**", logger, REQUIRED_LEVEL);
        }
    }
}
