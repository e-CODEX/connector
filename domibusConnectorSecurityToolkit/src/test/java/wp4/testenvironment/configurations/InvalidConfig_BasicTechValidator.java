package wp4.testenvironment.configurations;

import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import eu.ecodex.dss.util.tsl.LotlCreator;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;

import java.io.IOException;
import java.util.Optional;


public class InvalidConfig_BasicTechValidator {
    public static DSSECodexTechnicalValidationService get_BasicTechValidator_NoProxy_WithInvalidAuthCertConfig() throws
            IOException {

        DSSECodexTechnicalValidationService techValService = new DSSECodexTechnicalValidationService(
                ValidConfig_EtsiPolicy.etsiValidationPolicy(),
                ValidConfig_CertificateVerifier.get_WithProxy(),
                new DefaultSignatureProcessExecutor(),
                Optional.of(LotlCreator.createTrustedListsCertificateSource(
                        InvalidConfig_BasicTechValidator_AuthCertificateTSL.get_Invalid_Path())),
                Optional.empty()
        );

        //		techValService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
        //		techValService.setAuthenticationCertificateTSL(InvalidConfig_BasicTechValidator_AuthCertificateTSL
        //		.get_Invalid_Path());

        //		try {
        //			techValService.initAuthenticationCertificateVerification();
        //		} catch (ECodexException e) {
        //			Assertions.fail("Initialization of authentication certificate verification failed!", e);
        //		}

        return techValService;
    }
}
