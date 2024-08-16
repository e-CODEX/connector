/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/ECodexBusinessException.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service;

import eu.ecodex.dss.model.checks.CheckProblem;
import eu.ecodex.dss.model.checks.CheckResult;
import java.util.List;
import lombok.Getter;

/**
 * Used for non-technical exceptions in order to indicate rule violations. i.e. the token
 * structure's integrity must be good; or if the token issuer has an authentication-based system,
 * then authentication information must be provided.
 *
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@Getter
public class ECodexBusinessException extends ECodexException {
    private final CheckResult checkResult;

    /**
     * The default constructor for ECodexBusinessException.
     *
     * @param message     The message
     * @param checkResult The check result
     */
    public ECodexBusinessException(final String message, final CheckResult checkResult) {
        super(message);
        this.checkResult = checkResult;
    }

    /**
     * Get check result details.
     *
     * @return The details
     */
    public String getCheckResultDetails() {
        return createCheckResultDetails(checkResult);
    }

    /**
     * Creates a textual representation of the problems found.
     *
     * @param checkResult the container for the problems
     * @return a text in case the checkResult is not successful
     */
    public static String createCheckResultDetails(final CheckResult checkResult) {
        if (checkResult == null) {
            return null;
        }
        if (checkResult.isSuccessful()) {
            return "";
        }

        final List<CheckProblem> problems = checkResult.getProblems();

        final var stringBuilder = new StringBuilder(problems.size() * 20 + 30);
        if (checkResult.isFatal()) {
            stringBuilder.append("! the total result is FATAL !\n");
        } else if (checkResult.isProblematic()) {
            stringBuilder.append("! the total result is problematic !\n");
        }
        stringBuilder.append("detected ").append(problems.size())
          .append((problems.size() == 1) ? " problem" : " problem(s)");
        for (final CheckProblem problem : problems) {
            stringBuilder.append("\n----\n");
            if (problem.isFatal()) {
                stringBuilder.append("[FATAL] ");
            }
            stringBuilder.append(problem.getMessage());
        }

        return stringBuilder.toString();
    }
}
