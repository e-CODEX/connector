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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss/DSSTokenValidationCreator.java $
 * $Revision: 1904 $
 * $Date: 2013-05-02 14:29:29 +0200 (jeu., 02 mai 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.token.OriginalValidationReportContainer;
import eu.ecodex.dss.model.token.Signature;
import eu.ecodex.dss.model.token.SignatureAttributes;
import eu.ecodex.dss.model.token.SignatureCertificate;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalValidationResult;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;
import eu.ecodex.dss.util.LogDelegate;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlToken;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.DocumentProcessExecutor;
import eu.europa.esig.dss.validation.reports.Reports;
import java.lang.ref.SoftReference;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * This class creates the token validation; the execution is thread-safe.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1904 $ - $Date: 2013-05-02 14:29:29 +0200 (jeu., 02 mai 2013) $
 */
@SuppressWarnings("squid:S1135")
class DSSTokenValidationCreator {
    private static final LogDelegate LOG = new LogDelegate(DSSTokenValidationCreator.class);
    private final CertificateVerifier certificateVerifier;
    private final DSSDocument businessDocument;
    private final DSSDocument detachedSignature;
    private final DocumentProcessExecutor processExecutor;
    private final EtsiValidationPolicy etsiValidationPolicy;
    private TokenValidation tokenValidation;
    private CertificateSource ignoredCertificateStore;
    /**
     * This holds the latest data of the threads.
     */
    private static final ThreadLocal<SoftReference<DecisionData>> DATA_CACHE = new ThreadLocal<>();

    /**
     * Initializes a new instance of the {@code DSSTokenValidationCreator} class. This class is
     * responsible for creating and managing the token validation object.
     *
     * @param etsiValidationPolicy The ETSI validation policy.
     * @param certificateVerifier  The certificate verifier.
     * @param businessDocument     The business document.
     * @param detachedSignature    The detached signature.
     * @param processExecutor      The document process executor.
     */
    DSSTokenValidationCreator(
        final EtsiValidationPolicy etsiValidationPolicy,
        final CertificateVerifier certificateVerifier,
        final DSSDocument businessDocument,
        final DSSDocument detachedSignature,
        DocumentProcessExecutor processExecutor) {
        this.etsiValidationPolicy = etsiValidationPolicy;
        this.certificateVerifier = certificateVerifier;
        this.businessDocument = businessDocument;
        this.detachedSignature = detachedSignature;
        this.processExecutor = processExecutor;
    }

    /**
     * Gives access to the created object.
     *
     * @return the value
     */
    public TokenValidation getResult() {
        return tokenValidation;
    }

    /**
     * creates the tokenValidation object, this method will be executed only if the object has not
     * been created before. see also <a
     * href="http://www.jira.e-codex.eu/browse/ECDX-25">http://www.jira.e-codex.eu/browse/ECDX-25</a>
     * to get details about the requirements
     *
     * @throws Exception as of the underlying logic
     */
    void run() throws Exception {
        if (tokenValidation != null) {
            LOG.lInfo("result was already created for this class instance. skipping execution.");
            return;
        }

        LOG.lInfo("creating result");

        tokenValidation = new TokenValidation();

        final var validationResult = new TechnicalValidationResult();
        final var validationVerification = new ValidationVerification();

        final List<Signature> signatures = new ArrayList<>();

        // Default TokenValidationCreator takes only a signed document into account
        validationVerification.setAuthenticationData(null);
        validationVerification.setSignatureData(signatures);

        tokenValidation.setTechnicalResult(validationResult);
        tokenValidation.setVerificationData(validationVerification);

        try {
            runImpl();
        } catch (Exception e) {
            LOG.mCause("run", e);

            // => set also the verification time if needed
            if (tokenValidation.getVerificationTime() == null) {
                tokenValidation.setVerificationTime(
                    DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
            }

            // => set also the original validation report if needed
            if (tokenValidation.getOriginalValidationReport() == null) {
                tokenValidation.setOriginalValidationReport(
                    new OriginalValidationReportContainer()
                );
            }

            validationResult.setTrustLevel(TechnicalTrustLevel.FAIL);
            validationResult.setComment(
                "An error occurred, while validating the signature via DSS.");
            LOG.lWarn(
                "b/o encountered exception: result determined to {}: {}",
                validationResult.getTrustLevel(), validationResult.getComment()
            );
        }
    }

    private void runImpl() throws Exception {
        final TechnicalValidationResult validationResult = tokenValidation.getTechnicalResult();
        // Create the validation report
        final SignedDocumentValidator validator;

        if (detachedSignature != null) {
            validator = SignedDocumentValidator.fromDocument(detachedSignature);
            var doc = businessDocument;
            List<DSSDocument> docList = new ArrayList<>();
            docList.add(doc);
            validator.setDetachedContents(docList);
        } else {
            LOG.lDetail("acquiring SignedDocumentValidator upon document via contained signature");
            validator = SignedDocumentValidator.fromDocument(businessDocument);
        }

        validator.setProcessExecutor(processExecutor);
        validator.setCertificateVerifier(certificateVerifier);

        // Validate the document and generate the validation report
        LOG.lDetail("validating document");
        // TODO: use config here...
        //  final InputStream resourceAsStream = DSSECodexContainerService.class
        //  .getResourceAsStream("/validation/102853/constraint.xml");

        var reports = validator.validateDocument(etsiValidationPolicy);
        final var simpleReport = reports.getSimpleReport();
        final var diagnosticData = reports.getDiagnosticData();
        final var signatures = validator.getSignatures();

        LOG.lDetail("DSS validation report created: {}", simpleReport);

        final var detailedReport = reports.getDetailedReport();
        LOG.lDetail("Detailed Report: \n{}", detailedReport);

        // set the verificationTime
        if (simpleReport.getValidationTime() != null) {
            final var calGreg = new GregorianCalendar();
            calGreg.setTime(simpleReport.getValidationTime());
            tokenValidation.setVerificationTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(calGreg));
        } else {
            // fall back to new date if no data in the report
            LOG.lInfo(
                "no time-information/verification-time found in DSS validation report. "
                    + "setting the current datetime.");
            tokenValidation.setVerificationTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        }
        LOG.lDetail("verification time resolved to {}", tokenValidation.getVerificationTime());

        // AK: Removed XMLDSIG as it is detected by DSS but doesn't give enough information
        List<AdvancedSignature> invalidSignatures = new ArrayList<>();

        for (AdvancedSignature curSignature : signatures) {
            if (simpleReport.getSignatureFormat(curSignature.getId()) != null
                && simpleReport.getSignatureFormat(curSignature.getId()).getSignatureForm()
                               .equals(SignatureForm.XAdES)) {
                invalidSignatures.add(curSignature);
            }
        }

        for (AdvancedSignature curSignature : invalidSignatures) {
            signatures.remove(curSignature);
        }

        // detect the significant signature = the latest one in time
        // actually the order returned by iText and hence DSS is not reliable in terms of
        // signing-time order
        // so, we sort the list accordingly
        // final AdvancedSignature lastSignature = SignatureTimeComparator.getLast(signatures);
        // LOG.lDetail("using the signature information at index {} in the DSS validation report
        // for further determination: {}", signatures.indexOf(lastSignature), lastSignature);

        // inspect the info and derive some diagnosis and validation data
        var decisionData = new ArrayList<DecisionData>();
        var idsToRemove = new ArrayList<String>();

        for (AdvancedSignature curSignature : signatures) {
            if (ignoredCertificateStore != null
                && !ignoredCertificateStore.getByPublicKey(
                curSignature
                    .getSigningCertificateToken()
                    .getCertificate()
                    .getPublicKey()
            ).isEmpty()) {
                LOG.lDetail(
                    "Removing curSignature [{}] because the according certificate is "
                        + "within ignore list",
                    curSignature
                );
                idsToRemove.add(curSignature.getId());
                diagnosticData.getSignatureIdList().remove(curSignature.getId());
            } else {
                var signature = new Signature();
                var tokenSignatureCertificate = new SignatureCertificate();

                final DecisionData data = computeData(curSignature, reports);
                decisionData.add(data);

                // propagate to token validation instance
                signature.setSigningTime(data.diagnosis.signingTime);

                tokenSignatureCertificate.setSubject(data.diagnosis.signingCertificateSubject);
                tokenSignatureCertificate.setCertificateValid(
                    data.validation.signatureCertStatus != TechnicalTrustLevel.FAIL);
                tokenSignatureCertificate.setIssuer(data.diagnosis.signingCertificateIssuer);
                tokenSignatureCertificate.setValidityAtSigningTime(
                    data.validation.signatureCertHistory != TechnicalTrustLevel.FAIL);

                var tokenSignatureAttributes = new SignatureAttributes();
                tokenSignatureAttributes.setSignatureFormat(data.diagnosis.signatureFormatLevel);
                tokenSignatureAttributes.setSignatureLevel(
                    data.diagnosis.signatureConclusion.name());
                tokenSignatureAttributes.setSignatureValid(data.validation.signatureComputation);
                // structure verification is always true if the DSS report is generated and
                // contains signature information.
                tokenSignatureAttributes.setStructureValid(true);

                var tokenSignatureResult = new TechnicalValidationResult();
                tokenSignatureResult.setTrustLevel(data.diagnosis.trustLevel);
                tokenSignatureResult.setComment(data.diagnosis.comment);

                signature.setCertificateInformation(tokenSignatureCertificate);
                signature.setSignatureInformation(tokenSignatureAttributes);
                signature.setTechnicalResult(tokenSignatureResult);

                tokenValidation.getVerificationData().addSignatureData(signature);
            }
        }

        if (signatures.isEmpty() || idsToRemove.size() == signatures.size()) {
            // No signature information in the validation report
            validationResult.setTrustLevel(TechnicalTrustLevel.FAIL);
            // http://www.jira.e-codex.eu/browse/ECDX-45: tokenSignature.setUnsigned(true);
            validationResult.setComment("Unable to find a signature.");

            // For the reason of downwardcompatibility, at least one Signature-Entry needs to be
            // present
            tokenValidation.getVerificationData().addSignatureData(new Signature());

            LOG.lWarn(
                "no valid signature information found in DSS validation report - "
                    + "setting FAIL level.");
        }

        TechnicalValidationResult result = tokenValidation.getTechnicalResult();
        LOG.lInfo(
            "General result determined to lowest level: {}: {}", result.getTrustLevel(),
            result.getComment()
        );

        removeSignaturesFromSimpleReport(simpleReport, idsToRemove);

        // Add the original report to the token validation
        LOG.lDetail("propagating DSS validation report");
        final var tReportContainer = new OriginalValidationReportContainer();
        tReportContainer.setReports(reports);
        tokenValidation.setOriginalValidationReport(tReportContainer);
    }

    private void removeSignaturesFromSimpleReport(
        final SimpleReport simpleReport, final List<String> idsToRemove) {
        var signatures = simpleReport.getJaxbModel().getSignatureOrTimestamp();
        var toRemove = new ArrayList<XmlToken>();

        for (XmlToken curSig : signatures) {
            if (idsToRemove.contains(curSig.getId())) {
                simpleReport.getSignatureIdList().remove(curSig.getId());
                toRemove.add(curSig);
            }
        }

        for (XmlToken curSig : toRemove) {
            signatures.remove(curSig);
        }
    }

    private DecisionData computeData(final AdvancedSignature signature, Reports reports)
        throws Exception {
        var diagnosticData = reports.getDiagnosticData();
        var simpleReport = reports.getSimpleReport();

        final String signatureId = signature.getId();
        final String certificateId = diagnosticData.getSigningCertificateId(signatureId);

        // compute some diagnosis data
        LOG.lDetail("computing the diagnosis data");
        final var signingTime = TechnicalValidationUtil.getSigningTime(signature, signatureId);
        final var signingCertificateToken =
            TechnicalValidationUtil.getCertificateToken(signature, certificateId);
        final var signingCertificate =
            TechnicalValidationUtil.getCertificate(signingCertificateToken);
        final var signingCertificateSubject =
            TechnicalValidationUtil.getSigningCertificateSubjectName(signingCertificate);
        final var signingCertificateIssuer =
            TechnicalValidationUtil.getSigningCertificateIssuerName(signingCertificate);
        final var signatureFormatLevel = TechnicalValidationUtil.getSignatureFormatLevelAsString(
            simpleReport,
            signatureId
        ); // PAdES-BES etc
        final var signatureConclusion =
            TechnicalValidationUtil.getSignatureConclusion(simpleReport, signatureId); // QES etc

        CertificateWrapper issuerCertificateWrapper;

        if (signingCertificateToken.isSelfSigned()) {
            issuerCertificateWrapper =
                TechnicalValidationUtil.getCertificateWrapper(
                    diagnosticData.getUsedCertificates(),
                    signingCertificateIssuer
                );
        } else {
            issuerCertificateWrapper =
                TechnicalValidationUtil.getCertificateWrapper(
                    diagnosticData.getUsedCertificates(),
                    signingCertificateIssuer
                );
        }

        final var issuingCertificateToken =
            TechnicalValidationUtil.getCertificateToken(
                signature,
                issuerCertificateWrapper.getId()
            );
        final X509Certificate issuingCertificate =
            TechnicalValidationUtil.getCertificate(issuingCertificateToken);

        // compute some validation attributes
        LOG.lDetail("computing the validation data");
        final boolean validSignatureComputation =
            TechnicalValidationUtil.checkSignatureCorrectness(simpleReport, signatureId);
        final boolean validSignatureConclusion =
            TechnicalValidationUtil.checkSignatureConclusion(
                simpleReport,
                reports.getDetailedReport(),
                signatureId
            );
        final boolean validSignatureFormat = !StringUtils.isEmpty(signatureFormatLevel);

        final TechnicalTrustLevel validSignatureCertStatus =
            TechnicalValidationUtil.checkCertificateRevocation(
                signingCertificateToken,
                diagnosticData.getCertificateRevocationStatus(
                    certificateId).isRevoked()
            );
        final TechnicalTrustLevel validSignatureCertHistory =
            TechnicalValidationUtil.checkCertificateValidity(signingCertificateToken, signingTime);
        final boolean validTrustAnchor =
            TechnicalValidationUtil.checkTrustAnchor(diagnosticData, certificateId);
        final TechnicalTrustLevel validIssuerCertStatus =
            TechnicalValidationUtil.checkCertificateRevocation(issuingCertificateToken);
        final TechnicalTrustLevel validIssuerCertHistory =
            TechnicalValidationUtil.checkCertificateValidity(issuingCertificateToken, signingTime);

        // log the data
        LOG.lDetail("data details:\n "
                        + "signingTime={}\n signatureFormatLevel={}\n signingCertificate={}\n "
                        + "signingCertificateSubject={}\n signingCertificateIssuer={}\n "
                        + "signatureConclusion={}\n issuerCertificate={}\n "
                        + "validSignatureComputation={}\n validSignatureCertStatus={}\n "
                        + "validSignatureCertHistory={}\n validTrustAnchor={}\n "
                        + "validSignatureConclusion={}\n validSignatureFormat={}\n "
                        + "validIssuerCertStatus={}\n validIssuerCertHistory={}\n",
                    signingTime, signatureFormatLevel,
                    signingCertificate, signingCertificateSubject, signingCertificateIssuer,
                    signatureConclusion, issuingCertificate, validSignatureComputation,
                    validSignatureCertStatus, validSignatureCertHistory,
                    validTrustAnchor, validSignatureConclusion, validSignatureFormat,
                    validIssuerCertStatus, validIssuerCertHistory
        );

        // create the data container and put it in the cache
        final var diagnosis =
            new DiagnosisData(signingTime, signingCertificate, signingCertificateSubject,
                              signingCertificateIssuer, signatureFormatLevel, signatureConclusion,
                              issuingCertificate, TechnicalTrustLevel.FAIL, "Not yet determined!"
            );
        final var validation = new ValidationData(
            validSignatureComputation,
            validSignatureConclusion,
            validSignatureFormat,
            validSignatureCertStatus,
            validSignatureCertHistory,
            validTrustAnchor,
            validIssuerCertStatus,
            validIssuerCertHistory
        );
        final var decision = new DecisionData(diagnosis, validation);

        determineDecision(decision);

        DATA_CACHE.set(new SoftReference<>(decision));

        return decision;
    }

    private void determineDecision(DecisionData decisionData) {
        // ########## PART A -> FAIL checks ##########################################
        LOG.lDetail("deciding: PART A -> FAIL checks");

        // 1. The signature has to be mathematical correct. Otherwise the result of the technical
        // validation has to be RED
        if (!decisionData.validation.signatureComputation) {
            decide(
                decisionData, TechnicalTrustLevel.FAIL,
                "The signature is not mathematically correct."
            );
            return;
        }
        // 2. If DSS can recognize and analyze the signature format (e.g. PAdES-BES), the final
        // conclusion can be GREEN, otherwise the final conclusion will be RED
        if (!decisionData.validation.signatureFormat) {
            decide(
                decisionData, TechnicalTrustLevel.FAIL,
                "The signature format could not be detected."
            );
            return;
        }
        // 3. QES, ADES_QC and ADES are allowed signatures levels and can create a GREEN result,
        // an UNDETERMINED signature level will not be allowed and the final conclusion will be RED
        if (!decisionData.validation.signatureConclusion) {
            decide(
                decisionData, TechnicalTrustLevel.FAIL,
                "The signature conclusion is not sufficient."
            );
            return;
        }
        // 4. The signing certificate at least has to be valid at the time of signing
        // (not revoked, not expired, recognizable by DSS).
        if (decisionData.validation.signatureCertStatus == TechnicalTrustLevel.FAIL
            || decisionData.validation.signatureCertHistory == TechnicalTrustLevel.FAIL) {
            decide(
                decisionData, TechnicalTrustLevel.FAIL,
                "The signature certificate is not valid at the time of signing "
                    + "(non-active or revoked)."
            );
            return;
        }

        // 5. The only certificate with the need to be checked is the issuing certificate for
        // the signing certificate.
        //    A validation down to a root certificate is not necessary.
        //    For this certificate, the same rules apply as they do for the signing certificate,
        //    with the addition, that the issuing certificate had to be valid at the time the
        //    signing certificate started to become valid.

        // checks that the signing certificate has be signed by the issuer certificate
        if (decisionData.validation.issuerCertStatus == TechnicalTrustLevel.FAIL) {
            decide(
                decisionData, TechnicalTrustLevel.FAIL,
                "The issuer certificate could not be detected or is invalid."
            );
            return;
        }
        if (decisionData.validation.issuerCertHistory
            != TechnicalTrustLevel.SUCCESSFUL) { // there is no SUFFICIENT for this check
            decide(
                decisionData, TechnicalTrustLevel.FAIL,
                "The issuer certificate is not valid at the time of signing "
                    + "(revoked, expired or not recognisable)."
            );
            return;
        }

        // ########## PART B -> SUFFICIENT checks ####################################
        LOG.lDetail("deciding: PART B -> SUFFICIENT checks");

        // 4-3. Being valid at the time of signing with an CRL and/or an OCSP being defined, but
        // none of them is reachable: YELLOW
        if (decisionData.validation.signatureCertStatus == TechnicalTrustLevel.SUFFICIENT
            || decisionData.validation.signatureCertHistory == TechnicalTrustLevel.SUFFICIENT) {
            decide(decisionData, TechnicalTrustLevel.SUFFICIENT,
                   "The signature certificate's validity at the time of signing could "
                       + "not be fully determined (OCSP/CRL data not available)."
            );
            return;
        }

        if (decisionData.diagnosis.signatureConclusion == SignatureQualification.QESIG) {
            // 5-1. In case of qualified signatures, the issuing certificate has to be present
            // and verifiable at a national TSL.
            //      Otherwise the signature can be assessed with “AdES_QC” and the comment “Unable
            //      to verify the certificates issuer at a national TSL”.
            if (!decisionData.validation.trustAnchor) {
                final var tokenSignatureAttributes =
                    tokenValidation.getVerificationData().getSignatureData().getFirst()
                                   .getSignatureInformation();
                tokenSignatureAttributes.setSignatureLevel(SignatureQualification.ADESIG_QC.name());
                decide(
                    decisionData, TechnicalTrustLevel.SUFFICIENT,
                    "Unable to verify the certificate's issuer at a national TSL."
                );
                return;
            }
        } else // noinspection ConstantConditions
            if (decisionData.diagnosis.signatureConclusion == SignatureQualification.ADESIG_QC
                || decisionData.diagnosis.signatureConclusion == SignatureQualification.ADESIG_QC) {
                // only for clarity
                // 5-2. In case of AdES and AdES_QC, the issuing certificate should be validated
                // against a TSL if possible
                //      and the result of the verification should be part of the technical
                //      validation part of the “Trust Ok”-Token.
                //      The outcome of this validation thereby does not affect the result of
                //      the technical validation.
            }

        // ########## PART C -> finally reached SUCCESSFUL ###########################
        LOG.lDetail("deciding: PART C -> finally reached SUCCESSFUL");

        // passed all the previous checks
        decide(decisionData, TechnicalTrustLevel.SUCCESSFUL, "The signature is valid.");
    }

    private void decide(
        final DecisionData data, final TechnicalTrustLevel level, final String comments) {

        DiagnosisData diag = data.getDiagnosis();

        if (diag != null) {
            diag.setTrustLevel(level);
            diag.setComment(comments);
            LOG.lInfo("result determined to {}: {}", level, comments);
        } else {
            LOG.lWarn("Result hasn't been set. Diagnosis Data missing!");
        }

        final TechnicalValidationResult r = tokenValidation.getTechnicalResult();

        // TODO: Just a dummy decider: Take the lowest result as the general result.
        TechnicalTrustLevel curLevel = r.getTrustLevel();
        if (curLevel == null
            || (curLevel.equals(TechnicalTrustLevel.SUCCESSFUL) && (
            level.equals(TechnicalTrustLevel.SUFFICIENT) || level.equals(
                TechnicalTrustLevel.FAIL)
        )
        )
            || (curLevel.equals(TechnicalTrustLevel.SUFFICIENT) && level.equals(
            TechnicalTrustLevel.FAIL)
        )) {
            r.setTrustLevel(level);
            r.setComment(comments);
        }
    }

    /**
     * Gives access to the latest data that was used for computing the decision in the current
     * thread.
     *
     * @return the data (if stored and not garbage collected in the meantime)
     */
    public static DecisionData getCachedDecisionData() {
        final SoftReference<DecisionData> ref = DATA_CACHE.get();
        return (ref == null) ? null : ref.get();
    }

    /**
     * Provides the data that is used to decide on the result; immutable.
     */
    @Getter
    public static class DecisionData {
        private final DiagnosisData diagnosis;
        private final ValidationData validation;
        private TechnicalTrustLevel level;

        /**
         * Constructor.
         *
         * @param diagnosis  The diagnosis data used for the decision.
         * @param validation The validation data used for the decision.
         */
        DecisionData(final DiagnosisData diagnosis, final ValidationData validation) {
            this.diagnosis = diagnosis;
            this.validation = validation;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "DecisionData{"
                + "\nlevel=\n" + level
                + "\nvalidation=\n" + validation
                + "\ndiagnosis=\n" + diagnosis
                + "\n}";
        }
    }

    /**
     * Provides the data that is used for the validation; immutable.
     */
    public static class DiagnosisData {
        public final XMLGregorianCalendar signingTime;
        public final X509Certificate signingCertificate;
        public final String signingCertificateIssuer;
        public final String signingCertificateSubject;
        public final String signatureFormatLevel;
        public final SignatureQualification signatureConclusion;
        public final X509Certificate issuerCertificate;
        private TechnicalTrustLevel trustLevel;
        private String comment;

        /**
         * Provides a container for holding diagnosis data used for validation.
         *
         * <p>The DiagnosisData class represents the diagnosis information on the validation of a
         * signature. It contains various properties related to the signature, such as signing time,
         * signing certificate, signature format level, signature conclusion, issuer certificate,
         * trust level, and comment.
         *
         * @param signingTime               The signing time of the signature.
         * @param signingCertificate        The signing certificate used for the signature.
         * @param signingCertificateSubject The subject of the signing certificate.
         * @param signingCertificateIssuer  The issuer of the signing certificate.
         * @param signatureFormatLevel      The format level of the signature.
         * @param signatureConclusion       The conclusion of the signature validation.
         * @param issuerCertificate         The issuer certificate of the signing certificate.
         * @param trustLevel                The technical trust level of the signature.
         * @param comment                   Additional comment or information about the signature
         *                                  diagnosis.
         */
        DiagnosisData(
            final XMLGregorianCalendar signingTime, final X509Certificate signingCertificate,
            final String signingCertificateSubject, final String signingCertificateIssuer,
            final String signatureFormatLevel,
            final SignatureQualification signatureConclusion,
            final X509Certificate issuerCertificate, final TechnicalTrustLevel trustLevel,
            final String comment) {
            this.signingTime = signingTime;
            this.signingCertificate = signingCertificate;
            this.signingCertificateSubject = signingCertificateSubject;
            this.signingCertificateIssuer = signingCertificateIssuer;
            this.signatureFormatLevel = signatureFormatLevel;
            this.signatureConclusion = signatureConclusion;
            this.issuerCertificate = issuerCertificate;
            this.trustLevel = trustLevel;
            this.comment = comment;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "DiagnosisData{"
                + "\n   signingTime=" + signingTime
                + "\n   signingCertificateIssuer='" + signingCertificateIssuer + '\''
                + "\n   signatureFormatLevel='" + signatureFormatLevel + '\''
                + "\n   signatureConclusion=" + signatureConclusion
                + "\n   signingCertificate=" + signingCertificate
                + "\n   issuerCertificate=" + issuerCertificate
                + "\n}";
        }

        protected void setComment(String comment) {
            this.comment = comment;
        }

        protected void setTrustLevel(TechnicalTrustLevel trustLevel) {
            this.trustLevel = trustLevel;
        }
    }

    /**
     * Provides the result of the validation; immutable.
     */
    public static class ValidationData {
        public final boolean signatureComputation;
        public final boolean signatureConclusion;
        public final boolean signatureFormat;
        public final TechnicalTrustLevel signatureCertStatus;
        public final TechnicalTrustLevel signatureCertHistory;
        public final boolean trustAnchor;
        public final TechnicalTrustLevel issuerCertStatus;
        public final TechnicalTrustLevel issuerCertHistory;

        /**
         * Represents the validation data for a particular validation process.
         *
         * @param signatureComputation Specifies whether the signature computation was successful.
         * @param signatureConclusion  Specifies the conclusion of the signature validation
         *                             process.
         * @param signatureFormat      Specifies whether the signature format is valid.
         * @param signatureCertStatus  Specifies the technical trust level for the signature
         *                             certificate status.
         * @param signatureCertHistory Specifies the technical trust level for the signature
         *                             certificate history.
         * @param trustAnchor          Specifies whether the trust anchor is valid.
         * @param issuerCertStatus     Specifies the technical trust level for the issuer
         *                             certificate status.
         * @param issuerCertHistory    Specifies the technical trust level for the issuer
         *                             certificate history.
         */
        ValidationData(
            final boolean signatureComputation, final boolean signatureConclusion,
            final boolean signatureFormat, final TechnicalTrustLevel signatureCertStatus,
            final TechnicalTrustLevel signatureCertHistory, final boolean trustAnchor,
            final TechnicalTrustLevel issuerCertStatus,
            final TechnicalTrustLevel issuerCertHistory) {
            this.signatureComputation = signatureComputation;
            this.signatureConclusion = signatureConclusion;
            this.signatureFormat = signatureFormat;
            this.signatureCertStatus = signatureCertStatus;
            this.signatureCertHistory = signatureCertHistory;
            this.trustAnchor = trustAnchor;
            this.issuerCertStatus = issuerCertStatus;
            this.issuerCertHistory = issuerCertHistory;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "ValidationData{"
                + "\n   signatureComputation=" + signatureComputation
                + "\n   signatureConclusion=" + signatureConclusion
                + "\n   signatureFormat=" + signatureFormat
                + "\n   signatureCertStatus=" + signatureCertStatus
                + "\n   signatureCertHistory=" + signatureCertHistory
                + "\n   trustAnchor=" + trustAnchor
                + "\n   issuerCertStatus=" + issuerCertStatus
                + "\n   issuerCertHistory=" + issuerCertHistory
                + "\n}";
        }
    }

    /**
     * compares two signatures (information) via the signing time in the order <code>null - t0 - t1
     * ...</code>. a signature without signing time will be treated as "lower" than one with. see
     * also {@link AdvancedSignature}
     */
    public static class SignatureTimeComparator implements Comparator<AdvancedSignature> {
        /**
         * a singleton that can be used to avoid re-instantiation.
         */
        public static final SignatureTimeComparator INSTANCE = new SignatureTimeComparator();

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(final AdvancedSignature o1, final AdvancedSignature o2) {
            final Date t1 = getSigningTime(o1);
            final Date t2 = getSigningTime(o2);
            if (t1 == null && t2 == null) {
                return 0;
            }
            if (t1 == null && t2 != null) {
                return -1;
            }
            if (t1 != null && t2 == null) {
                return 1;
            }
            return t1.compareTo(t2);
        }

        /**
         * Derives the signing time using the {@link AdvancedSignature}.
         *
         * @param advancedSignature the object
         * @return the date
         */
        private static Date getSigningTime(final AdvancedSignature advancedSignature) {
            try {
                return advancedSignature.getSigningTime();
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * c Creates a copy of the list in parameter and sort that using the
         * {@link eu.ecodex.dss.service.impl.dss.DSSTokenValidationCreator.SignatureTimeComparator}
         * singleton.
         *
         * @param signatures the non-null list
         * @return the sorted copy of the list
         */
        public static List<AdvancedSignature> createSortedList(
            final List<AdvancedSignature> signatures) {
            final List<AdvancedSignature> result = new ArrayList<>(signatures);
            sort(result);
            return result;
        }

        /**
         * Sorts a list using the
         * {@link eu.ecodex.dss.service.impl.dss.DSSTokenValidationCreator.SignatureTimeComparator}
         * singleton.
         *
         * @param signatures the list
         */
        public static void sort(final List<AdvancedSignature> signatures) {
            signatures.sort(INSTANCE);
        }

        /**
         * Retrieves the first (earliest) SignatureInformation in the list.
         *
         * @param infos the non-null list
         * @return the result (may be null)
         */
        public static AdvancedSignature getFirst(final List<AdvancedSignature> infos) {
            final List<AdvancedSignature> sorted = createSortedList(infos);
            return sorted.isEmpty() ? null : sorted.getFirst();
        }

        /**
         * Retrieves the last (latest) SignatureInformation in the list.
         *
         * @param signatures the non-null list
         * @return the result (may be null)
         */
        public static AdvancedSignature getLast(final List<AdvancedSignature> signatures) {
            final List<AdvancedSignature> sorted = createSortedList(signatures);
            return sorted.isEmpty() ? null : sorted.getLast();
        }
    }

    public void setIgnoredCertificatesStore(CertificateSource ignoredCertificatesStore) {
        this.ignoredCertificateStore = ignoredCertificatesStore;
    }
}
