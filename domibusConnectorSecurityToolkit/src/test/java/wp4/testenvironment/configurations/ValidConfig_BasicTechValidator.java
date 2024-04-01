package wp4.testenvironment.configurations;

import eu.ecodex.dss.model.CertificateStoreInfo;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import eu.ecodex.dss.util.tsl.LotlCreator;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.validation.executor.DocumentProcessExecutor;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;


// SUB-CONF-05
public class ValidConfig_BasicTechValidator {
    private static final Resource IGNORED_KEYSTORE_PATH = new ClassPathResource("/keystores/ignore_tom_store.jks");
    private static final String IGNORED_KEYSTORE_PASSWORD = "teststore";

    private static final DocumentProcessExecutor DEFAULT_PROCESS_EXECUTOR = new DefaultSignatureProcessExecutor();

    // No Proxy
    // SUB-CONF-05 Variant 1
    public static DSSECodexTechnicalValidationService get_BasicTechValidator_NoProxy_NoAuthCertConfig() throws
            IOException {

        DSSECodexTechnicalValidationService techValService = new DSSECodexTechnicalValidationService(
                ValidConfig_EtsiPolicy.etsiValidationPolicy(),
                ValidConfig_CertificateVerifier.get_WithProxy(),
                DEFAULT_PROCESS_EXECUTOR,
                Optional.empty(),
                Optional.empty()
        );

        //		techValService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());

        return techValService;
    }

    // No Proxy - With Authentication Certificate Verification
    // SUB-CONF-05 Variant 2
    public static DSSECodexTechnicalValidationService get_BasicTechValidator_NoProxy_WithAuthCertConfig() throws
            IOException {

        FileInputStream fis = ValidConfig_BasicTechValidator_AuthCertificateTSL.get_FileInputStream_with_TSL();

        DSSECodexTechnicalValidationService techValService = new DSSECodexTechnicalValidationService(
                ValidConfig_EtsiPolicy.etsiValidationPolicy(),
                ValidConfig_CertificateVerifier.get_WithProxy(),
                DEFAULT_PROCESS_EXECUTOR,
                Optional.of(LotlCreator.createTrustedListsCertificateSource(fis)),
                Optional.empty()
        );

        //		techValService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
        //		techValService.setAuthenticationCertificateTSL(fis);
        //
        //		try {
        //			techValService.initAuthenticationCertificateVerification();
        //		} catch (ECodexException e) {
        //			e.printStackTrace();
        //			Assertions.fail("Initialization of authentication certificate verification failed!", e);
        //		}

        IOUtils.closeQuietly(fis);

        return techValService;
    }

    public static ECodexTechnicalValidationService get_BasicTechValidator_WithSignatureFilter() throws IOException {
        CertificateStoreInfo certStore = new CertificateStoreInfo();
        certStore.setLocation(IGNORED_KEYSTORE_PATH);
        certStore.setPassword(IGNORED_KEYSTORE_PASSWORD);

        KeyStoreCertificateSource keyStoreCertificateSource =
                new KeyStoreCertificateSource(IGNORED_KEYSTORE_PATH.getInputStream(), "JKS", IGNORED_KEYSTORE_PASSWORD);

        DSSECodexTechnicalValidationService techValService = new DSSECodexTechnicalValidationService(
                ValidConfig_EtsiPolicy.etsiValidationPolicy(),
                ValidConfig_CertificateVerifier.get_WithProxy(),
                DEFAULT_PROCESS_EXECUTOR,
                Optional.empty(),
                Optional.of(keyStoreCertificateSource)
        );

        return techValService;
    }
}
