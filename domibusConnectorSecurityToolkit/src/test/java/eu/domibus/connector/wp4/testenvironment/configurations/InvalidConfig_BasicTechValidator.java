/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.configurations;

import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import eu.ecodex.dss.util.tsl.LotlCreator;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import java.io.IOException;
import java.util.Optional;

/**
 * The InvalidConfig_BasicTechValidator class represents a utility class that provides a method to
 * retrieve a DSSECodexTechnicalValidationService object with invalid authentication certificate
 * configuration.
 *
 * <p>This class is intended for internal use only and should not be used directly by external
 * applications.
 */
@SuppressWarnings("checkstyle:TypeName")
public class InvalidConfig_BasicTechValidator {
    /**
     * Retrieves a DSSECodexTechnicalValidationService object with an invalid authentication
     * certificate configuration.
     *
     * <p>This method is intended for internal use only and should not be used directly by external
     * applications.
     *
     * @return a DSSECodexTechnicalValidationService object with an invalid authentication
     *      certificate configuration
     * @throws IOException if an I/O error occurs during the process
     */
    public static DSSECodexTechnicalValidationService
    get_BasicTechValidator_NoProxy_WithInvalidAuthCertConfig()
        throws IOException {
        return new DSSECodexTechnicalValidationService(
            ValidConfig_EtsiPolicy.etsiValidationPolicy(),
            ValidConfig_CertificateVerifier.get_WithProxy(),
            new DefaultSignatureProcessExecutor(), Optional.of(
            LotlCreator.createTrustedListsCertificateSource(
                InvalidConfig_BasicTechValidator_AuthCertificateTSL.get_Invalid_Path())),
            Optional.empty()
        );
    }
}
