/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/checks/CheckProblem.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.checks;

import lombok.Data;

/**
 * represents an issue. in case it is fatal, no further processing can be performed. this class is
 * immutable.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@Data
public class CheckProblem {
    private final boolean fatal;
    private final String message;

    /**
     * Constructor for immutability.
     *
     * @param fatal   indicator if not only a problem, but a serious failure also preventing further
     *                processing
     * @param message a message indicating the problem (should be in English)
     */
    public CheckProblem(final boolean fatal, final String message) {
        this.fatal = fatal;
        this.message = message;
    }
}
