/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/checks/CheckResult.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.model.checks;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * holds the result of a check.
 * note that this class is somewhat immutable: if a problem has been added, it cannot be removed anymore.
 *
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class CheckResult {
    private final List<CheckProblem> problems = new LinkedList<CheckProblem>();

    /**
     * is the overall result successful?
     *
     * @return the value
     */
    public boolean isSuccessful() {
        return !isProblematic();
    }

    /**
     * is the overall result not successful?
     *
     * @return the value
     */
    public boolean isProblematic() {
        return problems != null && !problems.isEmpty();
    }

    /**
     * is the overall result not successful because of a fatal problem?
     *
     * @return the value
     */
    public boolean isFatal() {
        if (isSuccessful()) {
            return false;
        }
        for (final CheckProblem problem : problems) {
            if (problem.isFatal()) {
                return true;
            }
        }
        return false;
    }

    /**
     * gives access to an (unmodifiable) copy of the list of problems
     *
     * @return the value
     */
    public List<CheckProblem> getProblems() {
        return Collections.unmodifiableList(problems);
    }

    /**
     * adds a problem
     *
     * @param fatal   see {@link CheckProblem#isFatal()}
     * @param message see {@link CheckProblem#getMessage()}
     * @return this class' instance for chaining
     */
    public CheckResult addProblem(final boolean fatal, final String message) {
        return addProblem(new CheckProblem(fatal, message));
    }

    /**
     * adds a problem
     *
     * @param problem the value (may be null)
     * @return this class' instance for chaining
     */
    public CheckResult addProblem(final CheckProblem problem) {
        if (problem == null) {
            return this;
        }
        problems.add(problem);
        return this;
    }

    /**
     * adds a list of problems
     *
     * @param problems the value (may be null)
     * @return this class' instance for chaining
     */
    public CheckResult addProblems(final List<CheckProblem> problems) {
        if (problems == null) {
            return this;
        }
        for (final CheckProblem problem : problems) {
            addProblem(problem);
        }
        return this;
    }

    /**
     * adds the problems of another check
     *
     * @param result the value (may be null)
     * @return this class' instance for chaining
     */
    public CheckResult addProblems(final CheckResult result) {
        if (result == null) {
            return this;
        }
        // we use direct access to not create an unnecessary copy of the list
        return addProblems(result.problems);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CheckResult:").append('\n');
        for (final CheckProblem problem : problems) {

            stringBuilder.append('\t').append("Message: ");
            if (problem.isFatal()) {

                stringBuilder.append("(FATAL) ");
            }
            stringBuilder.append(problem.getMessage()).append('\n');
        }
        return stringBuilder.toString();
    }
}
