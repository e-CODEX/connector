/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/ECodexException.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.service;

/**
 * this is the only type of exceptions that the service methods should expose.
 * so, they should catch all exceptions deemed as "library-scoped" and wrap them accordingly.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class ECodexException extends Exception {
    /**
     * see {@link RuntimeException#RuntimeException()}
     */
    public ECodexException() {
    }

    /**
     * see {@link RuntimeException#RuntimeException(String)}
     *
     * @param message textual information
     */
    public ECodexException(final String message) {
        super(message);
    }

    /**
     * see {@link RuntimeException#RuntimeException(Throwable)}
     *
     * @param cause the parent cause
     */
    public ECodexException(final Throwable cause) {
        super(cause);
    }

    /**
     * see {@link RuntimeException#RuntimeException(String, Throwable)}
     *
     * @param message textual information
     * @param cause   the parent cause
     */
    public ECodexException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * wraps a non-ecodexexception
     *
     * @param e the catched exception
     * @return the wrapped (or not-wrapped) exception
     */
    public static ECodexException wrap(final Exception e) {
        if (e instanceof ECodexException) {
            return (ECodexException) e;
        } else {
            return new ECodexException(e);
        }
    }
}
