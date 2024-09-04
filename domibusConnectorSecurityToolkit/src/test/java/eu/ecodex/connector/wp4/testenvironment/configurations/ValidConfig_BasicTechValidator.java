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

import eu.ecodex.dss.model.CertificateStoreInfo;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import eu.ecodex.dss.util.tsl.LotlCreator;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.validation.executor.DocumentProcessExecutor;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The ValidConfig_BasicTechValidator class provides static methods to obtain instances of
 * DSSECodexTechnicalValidationService with different configurations for basic technical validation.
 * This class does not contain any public constructors as all the methods are static.
 */
// SUB-CONF-05
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_BasicTechValidator {
    private static final Resource IGNORED_KEYSTORE_PATH =
        new ClassPathResource("/keystores/ignore_tom_store.jks");
    private static final String IGNORED_KEYSTORE_PASSWORD = "teststore";
    private static final DocumentProcessExecutor DEFAULT_PROCESS_EXECUTOR =
        new DefaultSignatureProcessExecutor();

    /**
     * Retrieves an instance of the DSSECodexTechnicalValidationService with no proxy and
     * authentication certificate configuration.
     *
     * <p>This method returns a DSSECodexTechnicalValidationService object initialized with a valid
     * configuration for validating technical aspects of DSSE submissions, without using a proxy and
     * without performing authentication certificate verification.
     *
     * @return the DSSECodexTechnicalValidationService with no proxy and without authentication
     *      certificate verification
     * @throws IOException if an I/O error occurs when retrieving the configuration file
     */
    // No Proxy
    // SUB-CONF-05 Variant 1
    public static DSSECodexTechnicalValidationService
    get_BasicTechValidator_NoProxy_NoAuthCertConfig()
        throws IOException {

        return new DSSECodexTechnicalValidationService(
            ValidConfig_EtsiPolicy.etsiValidationPolicy(),
            ValidConfig_CertificateVerifier.get_WithProxy(),
            DEFAULT_PROCESS_EXECUTOR, Optional.empty(),
            Optional.empty()
        );
    }

    /**
     * Retrieves an instance of the DSSECodexTechnicalValidationService with no proxy and
     * authentication certificate verification. This variant represents SUB-CONF-05 Variant 2.
     *
     * @return the DSSECodexTechnicalValidationService with no proxy and authentication certificate
     *      verification
     * @throws IOException if an I/O error occurs when retrieving the configuration file
     */
    // No Proxy - With Authentication Certificate Verification
    // SUB-CONF-05 Variant 2
    public static DSSECodexTechnicalValidationService
    get_BasicTechValidator_NoProxy_WithAuthCertConfig()
        throws IOException {
        var fis = ValidConfig_BasicTechValidator_AuthCertificateTSL.get_FileInputStream_with_TSL();
        var techValService =
            new DSSECodexTechnicalValidationService(ValidConfig_EtsiPolicy.etsiValidationPolicy(),
                                                    ValidConfig_CertificateVerifier.get_WithProxy(),
                                                    DEFAULT_PROCESS_EXECUTOR, Optional.of(
                LotlCreator.createTrustedListsCertificateSource(fis)), Optional.empty()
            );

        IOUtils.closeQuietly(fis);

        return techValService;
    }

    /**
     * Retrieves an instance of the DSSECodexTechnicalValidationService with the signature filter
     * applied. The method initializes a CertificateStoreInfo object with a specified location and
     * password. It then creates a KeyStoreCertificateSource object with the location and password
     * of the certificate store. Finally, it creates an instance of the
     * DSSECodexTechnicalValidationService with the necessary parameters and returns it.
     *
     * @return an instance of the DSSECodexTechnicalValidationService with the signature filter
     *      applied
     * @throws IOException if an I/O error occurs when retrieving the configuration file or loading
     *                     the keystore
     */
    public static ECodexTechnicalValidationService get_BasicTechValidator_WithSignatureFilter()
        throws IOException {

        var certStore = new CertificateStoreInfo();
        certStore.setLocation(IGNORED_KEYSTORE_PATH);
        certStore.setPassword(IGNORED_KEYSTORE_PASSWORD);

        var keyStoreCertificateSource = new KeyStoreCertificateSource(
            IGNORED_KEYSTORE_PATH.getInputStream(),
            "JKS",
            IGNORED_KEYSTORE_PASSWORD
        );

        return new DSSECodexTechnicalValidationService(
            ValidConfig_EtsiPolicy.etsiValidationPolicy(),
            ValidConfig_CertificateVerifier.get_WithProxy(),
            DEFAULT_PROCESS_EXECUTOR,
            Optional.empty(),
            Optional.of(keyStoreCertificateSource)
        );
    }
}
