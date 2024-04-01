/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/checks/CheckProblem
 * .java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.model.checks;

/**
 * represents an issue. in case it is fatal, no further processing can be performed.
 * this class is immutable.
 *
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision : 1879 $ - $Date : 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class CheckProblem {
    private final boolean fatal;
    private final String message;

    /**
     * constructor for immutability
     *
     * @param fatal   indicator if not only a problem, but a serious failure also preventing further processing
     * @param message a message indicating the problem (should be in English)
     */
    public CheckProblem(final boolean fatal, final String message) {
        this.fatal = fatal;
        this.message = message;
    }

    /**
     * indicator if not only a problem, but a serious failure also preventing further processing
     *
     * @return the result
     */
    public boolean isFatal() {
        return fatal;
    }

    /**
     * a message indicating the problem (should be in English)
     *
     * @return the value
     */
    public String getMessage() {
        return message;
    }
}
