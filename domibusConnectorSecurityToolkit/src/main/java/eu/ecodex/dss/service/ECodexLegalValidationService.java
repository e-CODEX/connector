/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/ECodexLegalValidationService.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service;

import eu.ecodex.dss.model.token.LegalValidationResult;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;

/**
 * The contract about creating the legal validation (result and report) on the {@link Token},
 * specifically on its {@link TokenValidation}.
 *
 * <p>this can be either a national implementation or the one of the DSS library
 *
 * <p>this class is called as a delegate by the DSS implementation of the
 * {@link eu.ecodex.dss.service.ECodexContainerService}.
 *
 * <p>implementors must guarantee that the object instances provided in parameters do not become
 * tampered = changed.
 *
 * <p>furthermore, the execution must be thread-safe.
 *
 * <p>Note that all the methods may - but only - throw a
 * {@link eu.ecodex.dss.service.ECodexException}.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public interface ECodexLegalValidationService {
    /**
     * Creates the legal validation (either national or DSS).
     *
     * <p>It must return a legal validation result object in any case!
     *
     * <p>IMPLEMENTORS: This method is allowed to throw only
     * {@link eu.ecodex.dss.service.ECodexException}!
     *
     * @param token the token with all the data necessary, esp. the token validation
     * @return the result
     * @throws eu.ecodex.dss.service.ECodexException wrapper around any exception occurred
     */
    LegalValidationResult create(final Token token) throws ECodexException;
}
