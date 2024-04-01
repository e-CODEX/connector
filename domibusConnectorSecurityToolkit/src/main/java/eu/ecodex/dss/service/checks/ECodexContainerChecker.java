/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/checks
 * /ECodexContainerChecker.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.service.checks;

import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.*;
import eu.ecodex.dss.util.DocumentStreamUtil;
import eu.europa.esig.dss.model.DSSDocument;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;


/**
 * Tests the following rules :
 * <ul>
 * <li>The container is not null</li>
 * <li>The ASIC-S document is set and contains data</li>
 * <li>The business document is set and contains data</li>
 * <li>The trustOkToken PDF is set and contains data</li>
 * <li>The trustOkToken XML is set and contains data</li>
 * <li>The token is set</li>
 * </ul>
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class ECodexContainerChecker extends AbstractChecker<ECodexContainer> {
    private static boolean isMissing(final byte[] s) {
        return isMissing(s, true);
    }

    private static boolean isMissing(final byte[] s, final boolean strict) {
        return s == null || (strict && s.length == 0);
    }

    private static boolean isMissing(final Object s) {
        return s == null;
    }

    private static boolean isMissing(final String s) {
        return isMissing(s, true);
    }

    private static boolean isMissing(final String s, boolean strict) {
        return s == null || (strict && s.trim().isEmpty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckResult run(final ECodexContainer object) {
        final CheckResult r = new CheckResult();

        if (isMissing(object)) {
            detect(r, true, Findings.CONTAINER_MISSING);
            return r;
        }

        checkContainerDocuments(object, r);

        checkToken(object, r);

        return r;
    }

    private void checkToken(final ECodexContainer object, final CheckResult r) {
        final Token token = object.getToken();
        if (isMissing(token)) {
            detect(r, true, Findings.TOKEN_MISSING);
            return;
        }

        checkTokenDocument(r, token);

        final TokenIssuer issuer = token.getIssuer();
        if (isMissing(issuer)) {
            detect(r, true, Findings.TOKEN_ISSUER_MISSING);
        }

        checkTokenValidation(r, token);

        checkTokenSystemType(r, token);
    }

    private void checkTokenDocument(final CheckResult r, final Token token) {
        final TokenDocument document = token.getDocument();
        if (isMissing(document)) {
            detect(r, true, Findings.TOKEN_DOCUMENT_MISSING);
            return;
        }

        if (isMissing(document.getFilename())) {
            detect(r, true, Findings.TOKEN_DOCUMENT_FILENAME_MISSING);
        }
        if (isMissing(document.getType())) {
            detect(r, true, Findings.TOKEN_DOCUMENT_TYPE_MISSING);
        }
        if (isMissing(document.getDigestMethod())) {
            detect(r, true, Findings.TOKEN_DOCUMENT_DIGESTMETHOD_MISSING);
        }
        if (isMissing(document.getDigestValue())) {
            detect(r, true, Findings.TOKEN_DOCUMENT_DIGESTVALUE_MISSING);
        }
    }

    private void checkTokenValidation(final CheckResult r, final Token token) {
        final TokenValidation validation = token.getValidation();
        if (isMissing(validation)) {
            detect(r, true, Findings.TOKEN_VALIDATION_MISSING);
            return;
        }

        final XMLGregorianCalendar time = token.getValidationVerificationTime();
        if (isMissing(time)) {
            detect(r, true, Findings.TOKEN_VALIDATION_TIME_MISSING);
        }
        final ValidationVerification verification = token.getValidationVerificationData();
        if (isMissing(verification)) {
            detect(r, true, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_MISSING);
        }
        // further constraints of the verification object depend on the systemtype (see checkTokenSystemType())

        checkTokenValidationTechnical(r, token);
        checkTokenValidationLegal(r, token);
    }

    private void checkTokenValidationTechnical(final CheckResult r, final Token token) {
        final TechnicalValidationResult result = token.getTechnicalValidationResult();
        if (isMissing(result)) {
            detect(r, true, Findings.TOKEN_VALIDATION_TECHNICAL_RESULT_MISSING);
            return;
        }

        final TechnicalTrustLevel trustLevel = result.getTrustLevel();
        if (isMissing(trustLevel)) {
            detect(r, true, Findings.TOKEN_VALIDATION_TECHNICAL_RESULT_TRUSTLEVEL_MISSING);
        } else if (trustLevel != TechnicalTrustLevel.SUCCESSFUL) {
            if (isMissing(result.getComment())) {
                detect(r, false, Findings.TOKEN_VALIDATION_TECHNICAL_RESULT_COMMENT_MISSING);
            }
        }
    }

    private void checkTokenValidationLegal(final CheckResult r, final Token token) {
        final LegalValidationResult result = token.getLegalValidationResult();
        if (isMissing(result)) {
            detect(r, true, Findings.TOKEN_VALIDATION_LEGAL_RESULT_MISSING);
            return;
        }

        final LegalTrustLevel trustLevel = result.getTrustLevel();
        if (isMissing(trustLevel)) {
            detect(r, true, Findings.TOKEN_VALIDATION_LEGAL_RESULT_TRUSTLEVEL_MISSING);
        } else {
            if (isMissing(result.getDisclaimer())) {
                // AK: Actually, this one is allowed
                // detect(r, false, Findings.TOKEN_VALIDATION_LEGAL_RESULT_DISCLAIMER_MISSING);
            }
        }
    }

    private void checkTokenSystemType(final CheckResult r, final Token token) {
        final AdvancedSystemType systemType = token.getAdvancedElectronicSystem();
        if (isMissing(systemType)) {
            detect(r, true, Findings.TOKEN_ISSUER_SYSTEMTYPE_MISSING);
            return;
        }

        final ValidationVerification validationVerification = token.getValidationVerificationData();
        if (!isMissing(validationVerification)) { // the absence is checked and reported in checkTokenValidation()
            if (systemType == AdvancedSystemType.AUTHENTICATION_BASED) {
                checkTokenSystemTypeAuthBased(r, validationVerification);
            } else if (systemType == AdvancedSystemType.SIGNATURE_BASED) {
                checkTokenSystemTypeSigBased(r, validationVerification);
            }
        }
    }

    // klara: changed validation slightly
    private void checkTokenSystemTypeAuthBased(final CheckResult r, final ValidationVerification verification) {
        final AuthenticationInformation auth = verification.getAuthenticationData();
        final List<Signature> sig = verification.getSignatureData();

        if (isMissing(auth) && (isMissing(sig) || sig.isEmpty())) {
            detect(r, true, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_AUTHINFO_MISSING);
            return;
        }

        if (auth != null) {
            if (isMissing(auth.getUsernameSynonym())) {
                detect(r, false, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_AUTHINFO_USERNAME_MISSING);
            }
            if (isMissing(auth.getIdentityProvider())) {
                detect(r, false, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_AUTHINFO_IDENTITYPROVIDER_MISSING);
            }
            if (isMissing(auth.getTimeOfAuthentication())) {
                detect(r, false, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_AUTHINFO_TIME_MISSING);
            }
        }

        if (sig != null && !sig.isEmpty()) {
            checkTokenSystemTypeSigBased(r, verification);
        }
    }

    private void checkTokenSystemTypeSigBased(final CheckResult r, final ValidationVerification verification) {
        final List<Signature> sig = verification.getSignatureData();

        if (isMissing(sig)) {
            detect(r, true, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_MISSING);
            return;
        }

        /**
         * http://www.jira.e-codex.eu/browse/ECDX-45: take into account tokenSignature.isUnsigned();
         * only if the data was signed, the attributes are taken into account (the main object exists)
         */
        if (!sig.isEmpty()) {
            for (Signature signature : sig) {

                final SignatureCertificate cert = signature.getCertificateInformation();
                final SignatureAttributes atts = signature.getSignatureInformation();
                final XMLGregorianCalendar time = signature.getSigningTime();

                if (atts == null && cert == null && time == null && sig.size() == 1) {
                    // No validation needed. Document is unsigned in the way it was up to Version 1.08.4
                } else {
                    if (isMissing(cert)) {
                        detect(r, true, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_CERTINFO_MISSING);
                    } else {
                        if (isMissing(cert.getIssuer())) {
                            detect(
                                    r,
                                    false,
                                    Findings.TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_CERTINFO_ISSUER_MISSING
                            );
                        }
                    }

                    if (isMissing(atts)) {
                        detect(r, true, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_SIGINFO_MISSING);
                    } else {

                        if (isMissing(atts.getSignatureFormat())) {
                            detect(r, false,
                                   Findings.TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_SIGINFO_FORMAT_MISSING
                            );
                        }
                        if (isMissing(atts.getSignatureLevel())) {
                            detect(r, false, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_SIGINFO_LEVEL_MISSING);
                        }
                    }

                    if (isMissing(time)) {
                        detect(r, true, Findings.TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_TIME_MISSING);
                    }
                }
            }
        }
    }

    private void checkContainerDocuments(final ECodexContainer object, final CheckResult r) {
        final DSSDocument asicDocument = object.getAsicDocument();
        if (isMissing(asicDocument)) {
            detect(r, true, Findings.CONTAINER_ASIC_MISSING);
        } else if (!DocumentStreamUtil.hasData(asicDocument)) {
            detect(r, true, Findings.CONTAINER_ASIC_DATA_MISSING);
        }

        final DSSDocument businessDocument = object.getBusinessDocument();
        if (isMissing(businessDocument)) {
            detect(r, true, Findings.CONTAINER_BUSINESS_MISSING);
        } else if (!DocumentStreamUtil.hasData(businessDocument)) {
            detect(r, true, Findings.CONTAINER_BUSINESS_DATA_MISSING);
        }

        final DSSDocument signatureDocument = object.getBusinessSignature();
        if (!isMissing(signatureDocument)) {
            if (!DocumentStreamUtil.hasData(signatureDocument)) {
                detect(r, true, Findings.CONTAINER_SIGNATURE_DATA_MISSING);
            }
        }

        final DSSDocument tokenPDF = object.getTokenPDF();
        if (isMissing(tokenPDF)) {
            detect(r, true, Findings.CONTAINER_TOKENPDF_MISSING);
        } else if (!DocumentStreamUtil.hasData(tokenPDF)) {
            detect(r, true, Findings.CONTAINER_TOKENPDF_DATA_MISSING);
        }

        final DSSDocument tokenXML = object.getTokenXML();
        if (isMissing(tokenXML)) {
            detect(r, true, Findings.CONTAINER_TOKENXML_MISSING);
        } else if (!DocumentStreamUtil.hasData(tokenXML)) {
            detect(r, true, Findings.CONTAINER_TOKENXML_DATA_MISSING);
        }
    }

    /**
     * holds the messages for the known issues
     */
    public interface Findings {
        String CONTAINER_MISSING = "the eCodexContainer must not be null";
        String TOKEN_MISSING = "the eCodexContainer must not have a null/empty token object";
        String TOKEN_DOCUMENT_MISSING = "the eCodexContainer's token object must not have a null/empty document " +
                "object";
        String TOKEN_DOCUMENT_FILENAME_MISSING =
                "the eCodexContainer's token document object must not have a null/empty filename";
        String TOKEN_DOCUMENT_TYPE_MISSING =
                "the eCodexContainer's token document object must not have a null/empty type";
        String TOKEN_DOCUMENT_DIGESTMETHOD_MISSING =
                "the eCodexContainer's token document object must not have a null/empty digestmethod";
        String TOKEN_DOCUMENT_DIGESTVALUE_MISSING =
                "the eCodexContainer's token document object must not have a null/empty digestvalue";
        String TOKEN_VALIDATION_MISSING =
                "the eCodexContainer's token object must not have a null/empty validation object";
        String TOKEN_VALIDATION_TIME_MISSING =
                "the eCodexContainer's token validation object must not have a null/empty verificationtime object";
        String TOKEN_VALIDATION_TECHNICAL_RESULT_MISSING =
                "the eCodexContainer's token validation object must not have a null/empty technical result object";
        String TOKEN_VALIDATION_TECHNICAL_RESULT_TRUSTLEVEL_MISSING =
                "the eCodexContainer's token technical validationresult object must not have a null/empty trustlevel " +
                        "object";
        String TOKEN_VALIDATION_TECHNICAL_RESULT_COMMENT_MISSING =
                "the eCodexContainer's token technical validationresult object should not have a null/empty comment, " +
                        "if the trustlevel is not SUCCESSFUL";
        String TOKEN_VALIDATION_LEGAL_RESULT_MISSING =
                "the eCodexContainer's token validation object must not have a null/empty legal result object";
        String TOKEN_VALIDATION_LEGAL_RESULT_TRUSTLEVEL_MISSING =
                "the eCodexContainer's token legal validationresult object must not have a null/empty trustlevel " +
                        "object";
        String TOKEN_VALIDATION_LEGAL_RESULT_DISCLAIMER_MISSING =
                "the eCodexContainer's token legal validationresult object should not have a null/empty disclaimer";
        String TOKEN_ISSUER_MISSING = "the eCodexContainer's token object must not have a null/empty issuer object";
        String TOKEN_ISSUER_SYSTEMTYPE_MISSING =
                "the eCodexContainer's token issuer object must not have a null/empty advancedsystemtype object";
        String TOKEN_VALIDATION_VERIFICATIONDATA_MISSING =
                "the eCodexContainer's token object must not have a null/empty validationverification object";
        String TOKEN_VALIDATION_VERIFICATIONDATA_AUTHINFO_MISSING =
                "the eCodexContainer's token object must not have a null/empty authenticationinformation object, if " +
                        "the system is AUTHENTICATION_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_AUTHINFO_USERNAME_MISSING =
                "the eCodexContainer's token authenticationinformation object should not have a null/empty " +
                        "usernamesynonym, if the system is AUTHENTICATION_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_AUTHINFO_IDENTITYPROVIDER_MISSING =
                "the eCodexContainer's token authenticationinformation object should not have a null/empty " +
                        "identityprovider, if the system is AUTHENTICATION_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_AUTHINFO_TIME_MISSING =
                "the eCodexContainer's token authenticationinformation object should not have a null/empty " +
                        "timeofauthentication, if the system is AUTHENTICATION_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_MISSING =
                "the eCodexContainer's token object must not have a null/empty signature object, if the system is " +
                        "SIGNATURE_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_CERTINFO_MISSING =
                "the eCodexContainer's token signature object must not have a null/empty signaturecertificate object," +
                        " " +
                        "if the system is SIGNATURE_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_CERTINFO_ISSUER_MISSING =
                "the eCodexContainer's token signaturecertificate object should not have a null/empty issuer object, " +
                        "if the system is SIGNATURE_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_SIGINFO_MISSING =
                "the eCodexContainer's token signature object must not have a null/empty signatureattributes object, " +
                        "if the system is SIGNATURE_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_SIGINFO_FORMAT_MISSING =
                "the eCodexContainer's token signatureattributes object should not have a null/empty signatureformat " +
                        "object, if the system is SIGNATURE_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_SIGINFO_LEVEL_MISSING =
                "the eCodexContainer's token signatureattributes object should not have a null/empty signaturelevel " +
                        "object, if the system is SIGNATURE_BASED";
        String TOKEN_VALIDATION_VERIFICATIONDATA_SIGDATA_TIME_MISSING =
                "the eCodexContainer's token signature object must not have a null/empty signingTime object, if the " +
                        "system is SIGNATURE_BASED";
        String CONTAINER_ASIC_MISSING = "the eCodexContainer must not have a null/empty asic document";
        String CONTAINER_ASIC_DATA_MISSING = "the eCodexContainer must not have a null/empty asic data stream";
        String CONTAINER_BUSINESS_MISSING = "the eCodexContainer must not have a null/empty business document";
        String CONTAINER_BUSINESS_DATA_MISSING = "the eCodexContainer must not have a null/empty business data stream";
        String CONTAINER_SIGNATURE_DATA_MISSING =
                "the eCodexContainer must not have a null/empty detached signature data stream";
        String CONTAINER_TOKENPDF_MISSING = "the eCodexContainer must not have a null/empty token PDF document";
        String CONTAINER_TOKENPDF_DATA_MISSING = "the eCodexContainer must not have a null/empty token PDF data " +
                "stream";
        String CONTAINER_TOKENXML_MISSING = "the eCodexContainer must not have a null/empty token XML document";
        String CONTAINER_TOKENXML_DATA_MISSING = "the eCodexContainer must not have a null/empty token XML data " +
                "stream";
    }
}
