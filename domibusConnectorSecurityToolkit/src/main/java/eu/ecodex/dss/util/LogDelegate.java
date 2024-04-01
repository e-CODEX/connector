/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/LogDelegate.java $
 * $Revision: 2032 $
 * $Date: 2013-05-17 09:52:32 +0200 (ven., 17 mai 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * encapsulates the actual logging behind and provides some convenience methods
 *
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 2032 $ - $Date: 2013-05-17 09:52:32 +0200 (ven., 17 mai 2013) $
 */
public class LogDelegate {
    protected final Class clazz;
    protected final String className;
    protected final Logger log;

    public LogDelegate(final Class<?> clazz) {
        this.clazz = clazz;
        className = (clazz == null) ? null : clazz.getName();
        // we want to have a logger in any case
        log = LoggerFactory.getLogger((clazz == null) ? getClass() : clazz);
    }

    /**
     * pre-concatenates the class name to the message, because
     * the final logger will report the methods of this class unfortunately.
     * if detectMethod is true, the invoking "business" method will be detected via reflection and appended to the
     * class name.
     *
     * @param message      the value
     * @param detectMethod the value
     * @return the text for the log-method invocation
     */
    protected String prepareMessage(final String message, final boolean detectMethod) {
        if (className == null) {
            return message;
        }
        final StringBuilder m = new StringBuilder(256); // a suitable length to avoid arraycopy
        m.append(className);
        if (detectMethod) {
            final StackTraceElement[] stes =
                    new Throwable().getStackTrace(); // no possibility to reduce the array length
            // 0 = this
            // 1 = log-method
            // 2 = business method
            // possibly we could ignore e.g. synthetic methods (via instrumentation/aspects) or such with specific
            // annotations
            // but that should be an improvement in slf4j
            final StackTraceElement ste = stes[2];
            m.append('#').append(ste.getMethodName());
            // + linenumber
            if (ste.getLineNumber() > 0) {
                m.append('@').append(ste.getLineNumber());
            }
        }
        m.append(" | ").append(message);
        return m.toString();
    }

    /**
     * used to signal the entering of a method (debug level)
     *
     * @param method     the name of the method
     * @param parameters the values in parameter
     */
    public void mEnter(final String method, final Object... parameters) {
        try {
            mEnterImpl(method, parameters);
        } catch (Exception e) {
            // ignored by purpose
        }
    }

    private void mEnterImpl(final String method, final Object[] parameters) {
        if (!log.isDebugEnabled()) {
            return;
        }
        final String m = prepareMessage(method, false);
        log.debug(m + "-enter", parameters);
    }

    /**
     * used to signal the successful exiting of a method (debug level)
     *
     * @param method     the name of the method
     * @param parameters the values in parameter
     */
    public void mExit(final String method, final Object... parameters) {
        try {
            mExitImpl(method, parameters);
        } catch (Exception e) {
            // ignored by purpose
        }
    }

    private void mExitImpl(final String method, final Object[] parameters) {
        if (!log.isDebugEnabled()) {
            return;
        }
        final String m = prepareMessage(method, false);
        log.debug(m + "-exit", parameters);
    }

    /**
     * used to signal a problem during execution (debug level for the parameters and error for the cause)
     *
     * @param method     the name of the method
     * @param cause      the occurred exception letting the execution fail
     * @param parameters the values in parameter
     */
    public void mCause(final String method, final Throwable cause, final Object... parameters) {
        try {
            mCauseImpl(method, cause, parameters);
        } catch (Exception e) {
            // ignored by purpose
        }
    }

    private void mCauseImpl(final String method, final Throwable cause, final Object[] parameters) {
        final boolean debugEnabled = log.isDebugEnabled();
        final boolean errorEnabled = log.isErrorEnabled();
        if (!debugEnabled && !errorEnabled) {
            return;
        }
        final String m = prepareMessage(method, false);
        if (debugEnabled && parameters != null && parameters.length > 0) {
            log.debug(m + "-cause", parameters);
        }
        if (errorEnabled) {
            log.error(m + "-cause", cause);
        }
    }

    /**
     * used to give information on a configuration (trace level)
     *
     * @param message    the text
     * @param parameters the values (to be put in the message)
     */
    public void lConfig(final String message, final Object... parameters) {
        try {
            lConfigImpl(message, parameters);
        } catch (Exception e) {
            // ignored by purpose
        }
    }

    private void lConfigImpl(final String message, final Object[] parameters) {
        if (!log.isTraceEnabled()) {
            return;
        }
        final String m = prepareMessage(message, true);
        log.trace(m, parameters);
    }

    /**
     * used to log something important (error level)
     *
     * @param message    the text
     * @param cause      the exception
     * @param parameters the values (to be put in the message)
     */
    public void lError(final String message, final Throwable cause, final Object... parameters) {
        try {
            lErrorImpl(message, cause, parameters);
        } catch (Exception e) {
            // ignored by purpose
        }
    }

    private void lErrorImpl(final String message, final Throwable cause, final Object[] parameters) {
        if (!log.isErrorEnabled()) {
            return;
        }
        final String m = prepareMessage(message, true);
        if (parameters != null && parameters.length > 0) {
            log.error(m, parameters);
        }
        log.error(m, cause);
    }

    /**
     * used to log something important (error level)
     *
     * @param message    the text
     * @param parameters the values (to be put in the message)
     */
    public void lError(final String message, final Object... parameters) {
        try {
            lErrorImpl(message, parameters);
        } catch (Exception e) {
            // ignored by purpose
        }
    }

    private void lErrorImpl(final String message, final Object[] parameters) {
        if (!log.isErrorEnabled()) {
            return;
        }
        final String m = prepareMessage(message, true);
        log.error(m, parameters);
    }

    /**
     * used to log something important (warn level)
     *
     * @param message    the text
     * @param parameters the values (to be put in the message)
     */
    public void lWarn(final String message, final Object... parameters) {
        try {
            lWarnImpl(message, parameters);
        } catch (Exception e) {
            // ignored by purpose
        }
    }

    private void lWarnImpl(final String message, final Object[] parameters) {
        if (!log.isWarnEnabled()) {
            return;
        }
        final String m = prepareMessage(message, true);
        log.warn(m, parameters);
    }

    /**
     * used to give some meaningful information on the execution (debug level)
     *
     * @param message    the text
     * @param parameters the values (to be put in the message)
     */
    public void lInfo(final String message, final Object... parameters) {
        try {
            lInfoImpl(message, parameters);
        } catch (Exception e) {
            // ignored by purpose
        }
    }

    private void lInfoImpl(final String message, final Object[] parameters) {
        if (!log.isDebugEnabled()) {
            return;
        }
        final String m = prepareMessage(message, true);
        log.debug(m, parameters);
    }

    /**
     * used to give additional detailed information on the execution e.g. for problem resolution (trace level)
     *
     * @param message    the text
     * @param parameters the values (to be put in the message)
     */
    public void lDetail(final String message, final Object... parameters) {
        try {
            lDetailImpl(message, parameters);
        } catch (Exception e) {
            // ignored by purpose
        }
    }

    private void lDetailImpl(final String message, final Object[] parameters) {
        if (!log.isTraceEnabled()) {
            return;
        }
        final String m = prepareMessage(message, true);
        log.trace(m, parameters);
    }
}
