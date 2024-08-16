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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/checks/AbstractChecker.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service.checks;

import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.checks.Checker;
import eu.ecodex.dss.util.LogDelegate;

/**
 * A basic class that is meant to be used, if logging is needed.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public abstract class AbstractChecker<T> implements Checker<T> {
    @SuppressWarnings("checkstyle:MemberName")
    protected final LogDelegate LOGGER;

    protected AbstractChecker() {
        LOGGER = new LogDelegate(getClass());
    }

    /**
     * a convenience method to add a problem, while addressing logging at the same time. fatal =
     * info; non-fatal = detail
     *
     * @param r       the result
     * @param fatal   indicator if not only a problem, but a serious failure also preventing further
     *                processing
     * @param message a message indicating the problem (should be in English)
     */
    protected void detect(final CheckResult r, final boolean fatal, final String message) {
        r.addProblem(fatal, message);
        if (fatal) {
            LOGGER.lInfo(message);
        } else {
            LOGGER.lDetail(message);
        }
    }
}
