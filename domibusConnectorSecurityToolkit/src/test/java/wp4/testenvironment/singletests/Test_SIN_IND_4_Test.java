package wp4.testenvironment.singletests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import eu.ecodex.dss.model.checks.CheckProblem;
import org.apache.commons.io.IOUtils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import wp4.testenvironment.configurations.ValidConfig_BasicLegalValidator;
import wp4.testenvironment.configurations.ValidConfig_BasicTechValidator;
import wp4.testenvironment.configurations.ValidConfig_CertificateVerifier;
import wp4.testenvironment.configurations.ValidConfig_SignatureParameters;
import eu.ecodex.dss.model.CertificateStoreInfo;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.EnvironmentConfiguration;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Check Signature Against e-CODEX Connector Keystore
 */
public class Test_SIN_IND_4_Test {
//
//	private static final Resource INVALID_JKS_KEYSTORE_PATH = new ClassPathResource("/keystores/keystore.jks");
//	private static final String INVALID_JKS_KEYSTORE_PASSWORD = "test123";
//
//	private static final Resource VALID_JKS_KEYSTORE_PATH = new ClassPathResource("/keystores/trust_store.jks");
//	private static final String VALID_JKS_KEYSTORE_PASSWORD = "teststore";
//
//	private static final Resource VALID_PKCS_KEYSTORE_PATH = new ClassPathResource("/keystores/signature_store.p12");
//	private static final String VALID_PKCS_KEYSTORE_PASSWORD = "teststore";
//
//	private static String Path_ValidContainer_ASIC = "src/test/resources/container/ValidContainer.asics";
//	private static String Path_ValidContainer_XML = "src/test/resources/container/ValidContainer.xml";
//
//	static DSSECodexContainerService containerService;
//	static ECodexContainer container;
//
//	/**
//	 * Initializes all test cases with the same, working configuration.
//	 * Test case specific configurations are done within each test case itself.
//	 * @throws IOException
//	 */
//	@BeforeAll
//	static public void init() throws IOException {
//		containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
//
//    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
//    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
//    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
//    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
//
//    	InputStream aSiCStream = getInputStream(Path_ValidContainer_ASIC);
//    	InputStream xMLStream = getInputStream(Path_ValidContainer_XML);
//
//    	try {
//			container = containerService.receive(aSiCStream, xMLStream);
//		} catch (ECodexException e) {
//			System.err.println("Exception wwhile reading the test-container for Test_SIN_IND_2");
//			e.printStackTrace();
//		} finally {
//			IOUtils.closeQuietly(aSiCStream);
//			IOUtils.closeQuietly(xMLStream);
//		}
//	}
//
//	/*
//	 * Variant 1 JKS Keystore - Signing certificate in keystore
//	 */
//    @Test
//	@Disabled("TODO: repair test") //TODO
//    public void test_In_ks() throws Exception {
//
//    	EnvironmentConfiguration conf = new EnvironmentConfiguration();
//    	CertificateStoreInfo certStore = new CertificateStoreInfo();
//
//    	certStore.setLocation(VALID_JKS_KEYSTORE_PATH);
//    	certStore.setPassword(VALID_JKS_KEYSTORE_PASSWORD);
//
//    	conf.setConnectorCertificates(certStore);
//    	containerService.setEnvironmentConfiguration(conf);
//
//    	CheckResult result = containerService.check(container);
//
//    	Assertions.assertTrue(result.isSuccessful());
//    }
//
//	/*
//	 * Variant 2 JKS Keystore - Signing certificate not in keystore
//	 */
//    @Test
//    public void test_Not_in_ks() throws Exception {
//
//    	EnvironmentConfiguration conf = new EnvironmentConfiguration();
//    	CertificateStoreInfo certStore = new CertificateStoreInfo();
//
//    	certStore.setLocation(INVALID_JKS_KEYSTORE_PATH);
//    	certStore.setPassword(INVALID_JKS_KEYSTORE_PASSWORD);
//
//    	conf.setConnectorCertificates(certStore);
//    	containerService.setEnvironmentConfiguration(conf);
//
//    	CheckResult result = containerService.check(container);
//
//    	Assertions.assertTrue(!result.isSuccessful());
//    	assertThat(result.getProblems()).extracting(CheckProblem::getMessage).contains(new String[] {"The signature is not contained in the (configured) certificates for the e-CODEX connectors."});
//    }
//
//	/*
//	 * Optional: Variant 3 PKCS#12 Keystore - Signing certificate in keystore
//	 */
//    @Test
//    public void test_PKCS12() throws Exception {
//
//    	EnvironmentConfiguration conf = new EnvironmentConfiguration();
//    	CertificateStoreInfo certStore = new CertificateStoreInfo();
//
//    	certStore.setLocation(VALID_PKCS_KEYSTORE_PATH);
//    	certStore.setPassword(VALID_PKCS_KEYSTORE_PASSWORD);
//
//    	conf.setConnectorCertificates(certStore);
//
//    	try {
//    		containerService.setEnvironmentConfiguration(conf);
//    	} catch (RuntimeException ec) {
//    		Assertions.assertTrue(true);
//    	} catch (Exception ex) {
//    		Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
//    	}
//    }
//
//    private static InputStream getInputStream(String path){
//
//		FileInputStream fis = null;
//		File f = null;
//
//		try {
//			f = new File(path);
//
//	        fis = new FileInputStream(f);
//
//			return fis;
//		} catch(Exception e) {
//			System.err.println("Exception within the files used for test Test_SIN_IND_3");
//			e.printStackTrace();
//			return null;
//		}
//    }
}
