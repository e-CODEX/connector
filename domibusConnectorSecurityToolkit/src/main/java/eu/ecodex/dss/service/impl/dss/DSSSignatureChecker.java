package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.DocumentProcessExecutor;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class DSSSignatureChecker<T extends ECodexContainer.ECodexDSSDocumentType> {
    private final T retriever;
    private final Logger LOGGER;
    private final CertificateVerifier certificateVerifier;
    private final DocumentProcessExecutor processExecutor;
    private final CertificateSource connectorCertificateSource;
    private final Optional<Resource> validationConstraints;
    private DSSSignatureChecker(
            T retriever,
            CertificateVerifier certificateVerifier,
            DocumentProcessExecutor documentValidator,
            CertificateSource certificateSource,
            String signatureCheckerName,
            Optional<Resource> validationConstraints) {
        this.retriever = retriever;
        LOGGER = LogManager.getLogger(DSSSignatureChecker.class.getName() + "." + signatureCheckerName);
        this.certificateVerifier = certificateVerifier;
        this.processExecutor = documentValidator;
        this.connectorCertificateSource = certificateSource;
        this.validationConstraints = validationConstraints;
    }
    public static DSSSignatureCheckerBuilder builder() {
        return new DSSSignatureCheckerBuilder();
    }

    /**
     * checks the validity of the signatures of a document
     *
     * @param eCodexContainer this holds the document with the signature
     * @return the result
     */
    public CheckResult checkSignature(ECodexContainer eCodexContainer) {
        return checkSignature(retriever.getDSSDocument(eCodexContainer));
    }

    /**
     * checks the validity of the signatures of a document
     *
     * @param dssDocument this holds the signatures
     * @return the result
     */
    private CheckResult checkSignature(final DSSDocument dssDocument) {
        Objects.requireNonNull(certificateVerifier, "the certificate verifier has not been set");

        final CheckResult checkResult = new CheckResult();

        final SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(dssDocument);
        validator.setProcessExecutor(processExecutor);

        validator.setCertificateVerifier(certificateVerifier);

        Reports reports;
        if (validationConstraints.isPresent()) {
            try {
                final InputStream resourceAsStream = validationConstraints.get()
                                                                          .getInputStream();
                // DSSECodexContainerService.class.getResourceAsStream("/validation/102853/container_constraint.xml");
                LOGGER.debug("Validating document with validation constraints [{}]", validationConstraints.get());
                reports = validator.validateDocument(resourceAsStream);
            } catch (IOException e) {
                throw new RuntimeException("Cannot open validation constraint", e);
            }
        } else {
            reports = validator.validateDocument();
        }

        final SimpleReport simpleReport = reports.getSimpleReport();
        //		LOG.lInfo("Simple Report:\n{}", simpleReport);
        LOGGER.debug("Detailed Report of [{}]:\n{}", dssDocument.getName(), reports.getXmlDetailedReport());
        // final DetailedReport detailedReport = validator.getDetailedReport();
        final DiagnosticData diagnosticData = reports.getDiagnosticData();
        final List<AdvancedSignature> signatures = validator.getSignatures();
        if (signatures.isEmpty()) {

            checkResult.addProblem(
                    true,
                    "The validation report [" + dssDocument.getName() + "] does not contain any signature information"
            );
            return checkResult;
        }

        for (final AdvancedSignature signature : signatures) {
            if (connectorCertificateSource != null) {
                // check that the signing signature is contained in the keystore holding (all) the certificates of the
                // connectors
                final CertificateToken signingCertificateToken =
                        TechnicalValidationUtil.getSigningCertificateToken(signature);
                final X509Certificate sigCert = TechnicalValidationUtil.getCertificate(signingCertificateToken);

                if (connectorCertificateSource.getCertificates().stream()
                                              .anyMatch(c -> c.getDSSId().equals(signingCertificateToken.getDSSId()))) {
                    LOGGER.trace(
                            "The Certificate with id [{}] has been found in the (configured) certificates",
                            signingCertificateToken.getDSSId()
                    );
                } else {
                    checkResult.addProblem(true, "The signature is not contained in the (configured) certificates");
                }
            } else {
                LOGGER.debug(
                        "No certificate source has been provided. No check if certificate is in source will be " +
                                "performed"
                );
            }
            final String signatureId = signature.getId();
            final Indication indication = simpleReport.getIndication(signatureId);
            if (Indication.PASSED.equals(indication) || Indication.TOTAL_PASSED.equals(indication)) {
                continue;
            }
            final List<String> errors =
                    simpleReport.getQualificationErrors(signatureId).stream().map(m -> m.getValue())
                                .collect(Collectors.toList());
            final String message;
            if (errors.isEmpty()) {

                final DigestAlgorithm signatureDigestAlgorithm =
                        diagnosticData.getSignatureDigestAlgorithm(signatureId);
                final EncryptionAlgorithm signatureEncryptionAlgorithm =
                        diagnosticData.getSignatureEncryptionAlgorithm(signatureId);
                final SignatureAlgorithm algorithm =
                        SignatureAlgorithm.getAlgorithm(signatureEncryptionAlgorithm, signatureDigestAlgorithm);
                message =
                        "The validation report [" + dssDocument.getName() + "] contains an invalid verification " +
                                "result" +
                                " for the signature algorithm: " + algorithm;
            } else {
                String newErrorMessage = "";
                for (String error : errors) {

                    newErrorMessage += (newErrorMessage.isEmpty() ? "" : "/") + error;
                }
                message = newErrorMessage;
            }
            checkResult.addProblem(true, message);
        }
        return checkResult;
    }

    public static class DSSSignatureCheckerBuilder {
        private CertificateVerifier certificateVerifier;
        private DocumentProcessExecutor processExecutor;
        private CertificateSource connectorCertificateSource;
        private Resource validationConstraints;
        private String signatureCheckerName;

        public <C extends ECodexContainer.ECodexDSSDocumentType> DSSSignatureChecker<C> build(C ecxDocType) {
            Objects.requireNonNull(certificateVerifier, "CertificateVerifier is not allowed to be null!");

            return new DSSSignatureChecker<C>(
                    ecxDocType,
                    certificateVerifier,
                    processExecutor,
                    connectorCertificateSource,
                    signatureCheckerName,
                    Optional.ofNullable(validationConstraints)
            );
        }

        public DSSSignatureCheckerBuilder withCertificateVerifier(CertificateVerifier certificateVerifier) {
            this.certificateVerifier = certificateVerifier;
            return this;
        }

        public DSSSignatureCheckerBuilder withProcessExecutor(DocumentProcessExecutor processExecutor) {
            this.processExecutor = processExecutor;
            return this;
        }

        public DSSSignatureCheckerBuilder withConnectorCertificateSource(CertificateSource connectorCertificateSource) {
            this.connectorCertificateSource = connectorCertificateSource;
            return this;
        }

        public DSSSignatureCheckerBuilder withValidationConstraints(Resource validationConstraints) {
            this.validationConstraints = validationConstraints;
            return this;
        }

        public DSSSignatureCheckerBuilder withSignatureCheckerName(String signatureCheckerName) {
            this.signatureCheckerName = signatureCheckerName;
            return this;
        }
    }
}
