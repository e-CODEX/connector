/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.jaxb.object.Message;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.DocumentProcessExecutor;
import eu.europa.esig.dss.validation.reports.Reports;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

/**
 * The DSSSignatureChecker class is responsible for checking the validity of signatures in a
 * document.
 *
 * <p>This class uses a builder pattern for instantiation. It requires a CertificateVerifier, a
 * DocumentProcessExecutor, a CertificateSource, and optionally a validationConstraints Resource.
 * The signatureCheckerName is an optional parameter for identifying the signature checker
 * instance.
 */
public class DSSSignatureChecker<T extends ECodexContainer.ECodexDSSDocumentType> {
    private final T retriever;

    public static DSSSignatureCheckerBuilder builder() {
        return new DSSSignatureCheckerBuilder();
    }

    /**
     * A builder class for creating instances of DSSSignatureChecker. This class provides methods
     * for setting various parameters and building the DSSSignatureChecker object.
     */
    public static class DSSSignatureCheckerBuilder {
        private CertificateVerifier certificateVerifier;
        private DocumentProcessExecutor processExecutor;
        private CertificateSource connectorCertificateSource;
        private Resource validationConstraints;
        private String signatureCheckerName;

        /**
         * Builds a new instance of the DSSSignatureChecker class with the provided parameters.
         *
         * @param ecxDocType The document type for the DSSSignatureChecker.
         * @return A new instance of the DSSSignatureChecker class.
         * @throws NullPointerException If the certificateVerifier parameter is null.
         */
        public <C extends ECodexContainer.ECodexDSSDocumentType> DSSSignatureChecker<C> build(
            C ecxDocType) {
            Objects.requireNonNull(
                certificateVerifier, "CertificateVerifier is not allowed to be null!");

            return new DSSSignatureChecker<>(
                ecxDocType,
                certificateVerifier,
                processExecutor,
                connectorCertificateSource,
                signatureCheckerName,
                Optional.ofNullable(validationConstraints)
            );
        }

        public DSSSignatureCheckerBuilder withCertificateVerifier(
            CertificateVerifier certificateVerifier) {
            this.certificateVerifier = certificateVerifier;
            return this;
        }

        public DSSSignatureCheckerBuilder withProcessExecutor(
            DocumentProcessExecutor processExecutor) {
            this.processExecutor = processExecutor;
            return this;
        }

        public DSSSignatureCheckerBuilder withConnectorCertificateSource(
            CertificateSource connectorCertificateSource) {
            this.connectorCertificateSource = connectorCertificateSource;
            return this;
        }

        public DSSSignatureCheckerBuilder withValidationConstraints(
            Resource validationConstraints) {
            this.validationConstraints = validationConstraints;
            return this;
        }

        public DSSSignatureCheckerBuilder withSignatureCheckerName(String signatureCheckerName) {
            this.signatureCheckerName = signatureCheckerName;
            return this;
        }
    }

    @SuppressWarnings("checkstyle:MemberName")
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
        LOGGER =
            LogManager.getLogger(DSSSignatureChecker.class.getName() + "." + signatureCheckerName);
        this.certificateVerifier = certificateVerifier;
        this.processExecutor = documentValidator;
        this.connectorCertificateSource = certificateSource;
        this.validationConstraints = validationConstraints;
    }

    /**
     * Checks the validity of the signatures of a document.
     *
     * @param container this holds the document with the signature
     * @return the result
     */
    public CheckResult checkSignature(ECodexContainer container) {
        return checkSignature(retriever.getDSSDocument(container));
    }

    /**
     * Checks the validity of the signatures of a document.
     *
     * @param dssDocument this holds the signatures
     * @return the result
     */
    private CheckResult checkSignature(final DSSDocument dssDocument) {
        Objects.requireNonNull(certificateVerifier, "the certificate verifier has not been set");

        final var checkResult = new CheckResult();

        final var validator = SignedDocumentValidator.fromDocument(dssDocument);
        validator.setProcessExecutor(processExecutor);

        validator.setCertificateVerifier(certificateVerifier);

        Reports reports;
        if (validationConstraints.isPresent()) {
            try {
                final var resourceAsStream = validationConstraints
                    .get()
                    .getInputStream();
                LOGGER.debug(
                    "Validating document with validation constraints [{}]",
                    validationConstraints.get()
                );
                reports = validator.validateDocument(resourceAsStream);
            } catch (IOException e) {
                throw new RuntimeException("Cannot open validation constraint", e);
            }
        } else {
            reports = validator.validateDocument();
        }

        final var simpleReport = reports.getSimpleReport();
        LOGGER.debug(
            "Detailed Report of [{}]:\n{}", dssDocument.getName(), reports.getXmlDetailedReport());
        final var diagnosticData = reports.getDiagnosticData();
        final List<AdvancedSignature> signatures = validator.getSignatures();
        if (signatures.isEmpty()) {
            checkResult.addProblem(
                true, "The validation report [" + dssDocument.getName()
                    + "] does not contain any signature information");
            return checkResult;
        }

        for (final AdvancedSignature signature : signatures) {

            if (connectorCertificateSource != null) {
                // check that the signing signature is contained in the keystore holding (all)
                // the certificates of the connectors
                final var signingCertificateToken =
                    TechnicalValidationUtil.getSigningCertificateToken(signature);
                TechnicalValidationUtil.getCertificate(signingCertificateToken);

                if (connectorCertificateSource.getCertificates().stream().anyMatch(
                    c -> c.getDSSId().equals(signingCertificateToken.getDSSId()))) {
                    LOGGER.trace(
                        "The Certificate with id [{}] has been found in the (configured)"
                            + " certificates",
                        signingCertificateToken.getDSSId()
                    );
                } else {
                    checkResult.addProblem(
                        true, "The signature is not contained in the (configured)"
                            + " certificates");
                }
            } else {
                LOGGER.debug(
                    "No certificate source has been provided. No check if certificate is in source"
                        + " will be performed");
            }
            final String signatureId = signature.getId();
            final var indication = simpleReport.getIndication(signatureId);
            if (Indication.PASSED.equals(indication) || Indication.TOTAL_PASSED.equals(
                indication)) {
                continue;
            }
            final List<String> errors =
                simpleReport.getQualificationErrors(signatureId)
                            .stream()
                            .map(Message::getValue)
                            .toList();
            final String message;
            if (errors.isEmpty()) {
                final var signatureDigestAlgorithm =
                    diagnosticData.getSignatureDigestAlgorithm(signatureId);
                final var signatureEncryptionAlgorithm =
                    diagnosticData.getSignatureEncryptionAlgorithm(signatureId);
                final var algorithm = SignatureAlgorithm.getAlgorithm(
                    signatureEncryptionAlgorithm,
                    signatureDigestAlgorithm
                );
                message = "The validation report [" + dssDocument.getName()
                    + "] contains an invalid verification result for the signature algorithm: "
                    + algorithm;
            } else {
                var newErrorMessage = new StringBuilder();
                for (String error : errors) {
                    newErrorMessage
                        .append((newErrorMessage.isEmpty()) ? "" : "/")
                        .append(error);
                }
                message = newErrorMessage.toString();
            }
            checkResult.addProblem(true, message);
        }
        return checkResult;
    }
}
