/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.configurations;

import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureCheckers;
import eu.ecodex.dss.service.impl.dss.DSSSignatureChecker;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The ValidConfig_SignatureCheckers class provides methods for obtaining configured instances of
 * DSSSignatureChecker for different signature types.
 */
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_SignatureCheckers {
    /**
     * Returns a SignatureCheckers instance that contains three different DSSSignatureChecker
     * instances for checking signatures on ASiC documents, XML tokens, and PDF tokens.
     *
     * @return a SignatureCheckers instance
     */
    public static SignatureCheckers getSignatureCheckers() {
        return new SignatureCheckers(
            asicsSignatureChecker(),
            xmlTokenSignatureChecker(),
            pdfTokenSignatureChecker()
        );
    }

    /**
     * Returns a DSSSignatureChecker instance configured specifically for PDF token signatures.
     *
     * <p>This method creates and configures a DSSSignatureChecker for PDF token signatures.
     * The name of the signature checker is set to "TokenPdfSignatureChecker". It uses an ECodex
     * Container.TokenPdfTypeECodex as the token type. The certificate verifier is set to the result
     * of the eCodexContainerCertificateVerifier() method. The validation constraints are set to the
     * result of the signatureValidation ConstraintsXml() method. The process executor is set to a
     * DefaultSignatureProcessExecutor instance.
     *
     * @return a DSSSignatureChecker&lt;ECodexContainer.TokenPdfTypeECodex&gt; instance configured
     *      for PDF token signatures.
     */
    private static DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex>
    pdfTokenSignatureChecker() {
        return DSSSignatureChecker
            .builder()
            .withSignatureCheckerName("TokenPdfSignatureChecker")
            .withCertificateVerifier(eCodexContainerCertificateVerifier())
            .withValidationConstraints(signatureValidationConstraintsXml())
            .withProcessExecutor(new DefaultSignatureProcessExecutor())
            .build(new ECodexContainer.TokenPdfTypeECodex());
    }

    private static DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex>
    xmlTokenSignatureChecker() {
        return DSSSignatureChecker
            .builder()
            .withSignatureCheckerName("TokenPdfSignatureChecker")
            .withCertificateVerifier(eCodexContainerCertificateVerifier())
            .withValidationConstraints(signatureValidationConstraintsXml())
            .withProcessExecutor(new DefaultSignatureProcessExecutor())
            .build(new ECodexContainer.TokenXmlTypesECodex());
    }

    private static DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex>
    asicsSignatureChecker() {
        return DSSSignatureChecker
            .builder()
            .withCertificateVerifier(eCodexContainerCertificateVerifier())
            .withSignatureCheckerName("TokenPdfSignatureChecker")
            .withValidationConstraints(signatureValidationConstraintsXml())
            .withProcessExecutor(new DefaultSignatureProcessExecutor())
            .withConnectorCertificateSource(connectorCertificateSource())
            .build(new ECodexContainer.AsicDocumentTypeECodex());
    }

    private static CertificateSource connectorCertificateSource() {
        try {
            return new KeyStoreCertificateSource(
                new ClassPathResource("/keystores/signature_store.jks").getInputStream(), "JKS",
                "teststore"
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Resource signatureValidationConstraintsXml() {
        return new ClassPathResource("/102853/container_constraint.xml");
    }

    /**
     * Returns a CertificateVerifier instance that is specifically configured for eCodex container
     * certificates.
     *
     * <p>This method creates and configures a CommonCertificateVerifier instance for eCodex
     * container
     * certificates. It uses the ValidConfig_CertificateVerifier.get_NoProxy() method to retrieve
     * the certificate verifier without using a proxy. If an IOException occurs while retrieving the
     * certificate verifier, a RuntimeException is thrown.
     *
     * @return a CertificateVerifier instance configured for eCodex container certificates
     * @throws RuntimeException if an IOException occurs while retrieving the certificate verifier
     */
    @SuppressWarnings("checkstyle:MethodName")
    private static CertificateVerifier eCodexContainerCertificateVerifier() {
        try {
            return ValidConfig_CertificateVerifier.get_NoProxy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
