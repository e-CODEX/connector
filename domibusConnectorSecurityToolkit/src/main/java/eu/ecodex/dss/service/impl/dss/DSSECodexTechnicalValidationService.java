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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss/DSSECodexTechnicalValidationService.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.token.AuthenticationCertificate;
import eu.ecodex.dss.model.token.OriginalValidationReportContainer;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.ecodex.dss.util.LogDelegate;
import eu.ecodex.dss.util.PdfValidationReportService;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateValidity;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.DocumentProcessExecutor;
import eu.europa.esig.dss.validation.reports.Reports;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

/**
 * The DSS implementation of the services.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@SuppressWarnings("squid:S1135")
public class DSSECodexTechnicalValidationService implements ECodexTechnicalValidationService {
    private static final LogDelegate LOG =
        new LogDelegate(DSSECodexTechnicalValidationService.class);
    public static final String CREATE_REPORT_PDF_METHOD = "createReportPDF";
    public static final String CREATE_METHOD = "create";
    private final EtsiValidationPolicy etsiValidationPolicy;
    private final CertificateVerifier certificateVerifier;
    private final DocumentProcessExecutor processExecutor;
    private final Optional<TrustedListsCertificateSource> trustedListCertificatesSource;
    private final Optional<CertificateSource> ignoredCertificatesStore;

    /**
     * Creates a new instance of DSSECodexTechnicalValidationService with the given parameters.
     *
     * @param etsiValidationPolicy          The ETSI validation policy.
     * @param certificateVerifier           The certificate verifier.
     * @param processExecutor               The document process executor.
     * @param trustedListCertificatesSource The trusted list certificate source (optional).
     * @param ignoredCertificatesStore      The ignored certificates store (optional).
     */
    public DSSECodexTechnicalValidationService(
        EtsiValidationPolicy etsiValidationPolicy,
        CertificateVerifier certificateVerifier,
        DocumentProcessExecutor processExecutor,
        Optional<TrustedListsCertificateSource> trustedListCertificatesSource,
        Optional<CertificateSource> ignoredCertificatesStore) {
        this.etsiValidationPolicy = etsiValidationPolicy;
        this.certificateVerifier = certificateVerifier;
        this.processExecutor = processExecutor;
        this.trustedListCertificatesSource = trustedListCertificatesSource;
        this.ignoredCertificatesStore = ignoredCertificatesStore;
    }

    /**
     * {@inheritDoc} The method {@code #setProcessExecutor} must be called before.
     */
    @Override
    public TokenValidation create(
        final DSSDocument businessDocument, final DSSDocument detachedSignature)
        throws ECodexException {
        LOG.mEnter(CREATE_METHOD, businessDocument, detachedSignature);
        try {
            final var delegate = new DSSTokenValidationCreator(
                etsiValidationPolicy,
                certificateVerifier,
                businessDocument,
                detachedSignature,
                processExecutor
            );
            delegate.setIgnoredCertificatesStore(ignoredCertificatesStore.orElse(null));
            delegate.run();
            return delegate.getResult();
        } catch (Exception e) {
            LOG.mCause(CREATE_METHOD, e, businessDocument, detachedSignature);
            throw ECodexException.wrap(e);
        } finally {
            LOG.mExit(CREATE_METHOD, businessDocument, detachedSignature);
        }
    }

    /**
     * {@inheritDoc} The report must contain exactly one object of type {@link DiagnosticData}.
     */
    @Override
    public DSSDocument createReportPDF(final Token token) throws ECodexException {
        LOG.mEnter(CREATE_REPORT_PDF_METHOD, token);
        try {
            return createReportPDFImpl(token);
        } catch (Exception e) {
            LOG.mCause(CREATE_REPORT_PDF_METHOD, e, token);
            throw ECodexException.wrap(e);
        } finally {
            LOG.mExit(CREATE_REPORT_PDF_METHOD, token);
        }
    }

    private DSSDocument createReportPDFImpl(final Token token) throws ECodexException {
        if (token == null) {
            throw new ECodexException("the token (in parameter) must not be null");
        }
        final var tokenValidation = token.getValidation();
        if (tokenValidation == null) {
            throw new ECodexException("the token (in parameter) must have a validation object");
        }
        final ValidationVerification tokenVerificationData = tokenValidation.getVerificationData();
        if (tokenVerificationData == null) {
            throw new ECodexException(
                "the token (in parameter) must have a validation object with an existing "
                    + "verification data"
            );
        }

        final OriginalValidationReportContainer report =
            tokenValidation.getOriginalValidationReport();

        if (report == null) {
            // return an empty document
            return new InMemoryDocument(new byte[0]);
        }

        final List<Object> reportDatas = report.getAny();
        if (reportDatas == null || reportDatas.isEmpty()) {
            // return an empty document
            return new InMemoryDocument(new byte[0]);
        }

        final var pdfStream = new ByteArrayOutputStream();

        try {
            if (report.getReports() != null) {
                var reports = report.getReports();
                final var diagnosticData = reports.getDiagnosticData();
                final var simpleReport = reports.getSimpleReport();
                // create and write the pdf version to the stream
                final var pdfService = new PdfValidationReportService();
                pdfService.createReport(diagnosticData, simpleReport, pdfStream);
                return new InMemoryDocument(
                    pdfStream.toByteArray(), "dss-report.pdf", MimeTypeEnum.PDF);
            } else {
                return new InMemoryDocument(new byte[0]);
            }
        } catch (Exception e) {
            throw ECodexException.wrap(e);
        } finally {
            IOUtils.closeQuietly(pdfStream);
        }
    }

    // klara: Added method for certificate verification against TSL of authentication-certificates

    /**
     * This method checks whether the certificate a signature has been created with on a given
     * document (within the document or detached) is present within a defined TSL. Before being able
     * to verify the TSL (trustedListCertificatesSource) needs to be configured
     *
     * @param businessDocument  The business document.
     * @param detachedSignature The detached signature.
     * @return The authentication certificate validation result.
     * @throws ECodexException If an error occurs during the verification process.
     */
    protected AuthenticationCertificate verifyAuthenticationCertificate(
        final DSSDocument businessDocument, final DSSDocument detachedSignature)
        throws ECodexException {
        var validationResult = new AuthenticationCertificate();

        if (trustedListCertificatesSource.isPresent()) {
            X509Certificate signatureCert = getCertificate(businessDocument, detachedSignature);
            if (signatureCert != null) {
                var subjectName = signatureCert.getSubjectX500Principal();
                List<CertificateToken> tslCerts =
                    trustedListCertificatesSource.get().getCertificates();
                if (tslCerts != null && !tslCerts.isEmpty()) {
                    for (CertificateToken curCertAndContext : tslCerts) {
                        if (curCertAndContext.getCertificate().getSubjectX500Principal()
                                             .equals(subjectName)
                            && curCertAndContext.getCertificate().equals(signatureCert)) {
                            validationResult.setValidationSuccessful(true);
                        }
                    }
                } else {
                    validationResult.setValidationSuccessful(false);
                    LOG.lInfo(
                        "TSL of authentication certificates was null or empty - "
                            + "No certificate will be deemed to be valid."
                    );
                }
            }
        } else {
            validationResult.setValidationSuccessful(false);
            LOG.lInfo(
                "Attribute authenticationCertificateTSL has not been set - Validation of "
                    + "signature certificate against TSL of authentication service providers will "
                    + "always fail!"
            );
        }
        return validationResult;
    }

    // klara
    private X509Certificate getCertificate(
        DSSDocument businessDocument, DSSDocument detachedSignature) throws ECodexException {

        X509Certificate signatureCert = null;

        if (detachedSignature == null) {
            InputStream is = null;
            ByteArrayOutputStream baos = null;

            try {
                is = businessDocument.openStream();
                baos = new ByteArrayOutputStream();

                int bytesRead;
                var data = new byte[16384];

                while ((bytesRead = is.read(data, 0, data.length)) != -1) {
                    baos.write(data, 0, bytesRead);
                }

                baos.flush();
                byte[] document = baos.toByteArray();

                var file = new InMemoryDocument(document);

                try {
                    var validator = SignedDocumentValidator.fromDocument(file);
                    validator.setCertificateVerifier(
                        // TODO: use CertificateVerifier factory
                        new CommonCertificateVerifier(true)
                    );

                    Reports rep = validator.validateDocument();
                    String certId = rep.getDiagnosticData().getFirstSignatureId();

                    List<AdvancedSignature> signatures = validator.getSignatures();
                    Iterator<AdvancedSignature> it = signatures.listIterator();

                    Date lastSignature = null;

                    // klara: This could become troublesome with timezones
                    while (it.hasNext()) {
                        AdvancedSignature curSig = it.next();
                        if (lastSignature == null || lastSignature.after(curSig.getSigningTime())) {

                            var candidates = curSig.getCandidatesForSigningCertificate();

                            signatureCert = candidates.getCertificateValidityList()
                                                      .stream()
                                                      .map(CertificateValidity::getCertificateToken)
                                                      .filter(
                                                          c -> c.getDSSIdAsString().equals(certId))
                                                      .map(CertificateToken::getCertificate)
                                                      .findFirst().orElse(null);
                        }
                    }
                } catch (DSSException ex) {
                    /* This exception is thrown by "SignedDocumentValidator.fromDocument(file)"
                    in case of
                     * - Document Type is not recognized (No PDF, ASiC-S, XML or CMS file)
                     * - File is too short (Length < 5 Byte)
                     *
                     *  No reason to react here. Result is "No signing certificate in place"
                     */
                }
            } catch (IOException e) {
                throw new ECodexException(e);
            } finally {
                if (is != null) {
                    IOUtils.closeQuietly(is);
                }
                if (baos != null) {
                    IOUtils.closeQuietly(baos);
                }
            }
        }

        return signatureCert;
    }
}
