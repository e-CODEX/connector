/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/checks
 * /AbstractChecker.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.service.checks;

import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.checks.Checker;
import eu.ecodex.dss.util.LogDelegate;


/**
 * a basic class that is meant to be used, if logging is needed
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public abstract class AbstractChecker<T> implements Checker<T> {
    protected final LogDelegate LOG;

    protected AbstractChecker() {
        LOG = new LogDelegate(getClass());
    }

    /**
     * a convenience method to add a problem, while addressing logging at the same time.
     * fatal = info; non-fatal = detail
     *
     * @param r       the result
     * @param fatal   see {@link eu.ecodex.dss.model.checks.CheckProblem#isFatal()}
     * @param message see {@link eu.ecodex.dss.model.checks.CheckProblem#getMessage()}
     */
    protected void detect(final CheckResult r, final boolean fatal, final String message) {
        r.addProblem(fatal, message);
        if (fatal) {
            LOG.lInfo(message);
        } else {
            LOG.lDetail(message);
        }
    }
}
