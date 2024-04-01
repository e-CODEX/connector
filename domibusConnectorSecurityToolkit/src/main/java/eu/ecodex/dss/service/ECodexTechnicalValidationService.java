/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service
 * /ECodexTechnicalValidationService.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.service;

import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.europa.esig.dss.model.DSSDocument;


/**
 * <p>the contract about creating the technical validation (result and report) on the (main) business document</p>
 * <p>this can be either a national implementation or the one of the DSS library</p>
 * <p>this class is called as a delegate by the DSS implementation of the {@link ECodexContainerService}.</p>
 * <p>implementors must guarantee that the object instances provided in parameters do not become tampered = changed.</p>
 * <p>furthermore, the execution must be thread-safe.</p>
 * <p>
 * note that all the methods may - but only - throw a {@link ECodexException}.
 * <p>
 * <p>
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public interface ECodexTechnicalValidationService {
    /**
     * creates the technical validation including the result and possibly a ValidationReport (either national or DSS)
     * <p>
     * It must return a token validation object in any case!
     * If something fails , the token validation must indicate a trust level "FAIL" and must contain a comment that
     * explains the reason.
     * <p>
     * The TokenValidation has to be created according to the business rules, e.g. the distinction between
     * authentication-based and signature-based is important!
     * <p>
     * IMPLEMENTORS: This method is allowed to throw only {@link ECodexException}!
     *
     * @param businessDocument  the document for which a validation = trust has to be created; this may be signed or
     *                          not
     * @param detachedSignature the optional document holding a signature for the document (= DETACHED)
     * @return the result
     * @throws ECodexException wrapper around any exception occurred
     */
    TokenValidation create(
            final DSSDocument businessDocument,
            final DSSDocument detachedSignature) throws ECodexException;

    /**
     * creates a PDF document to be used as the human readable part of the token.pdf for the validation report
     * (details)
     * <p>
     * <p>
     * IMPLEMENTORS: This method is allowed to throw only {@link ECodexException}!
     *
     * @param token the root-object giving access to the
     *              {@link eu.ecodex.dss.model.token.OriginalValidationReportContainer}
     * @return a document in PDF format which will be appended to the token.pdf. it gives details about the validation
     * in human-readable form.
     * @throws ECodexException wrapper around any exception occurred
     */
    DSSDocument createReportPDF(final Token token) throws ECodexException;
}
