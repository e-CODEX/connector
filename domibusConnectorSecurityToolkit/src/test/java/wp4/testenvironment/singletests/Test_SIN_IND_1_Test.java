package wp4.testenvironment.singletests;

import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.checks.CheckProblem;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
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

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Check Container
 */
public class Test_SIN_IND_1_Test {

//	private static String Path_ValidContainer_ASIC = "/container/ValidContainer.asics";
//	private static String Path_ValidContainer_XML = "/container/ValidContainer.xml";
//
//	private static String Path_AlteredContainer_ASIC = "/container/AlteredContainer.asics";
//
//	static DSSECodexContainerService containerService;
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
//	}
//
//	/*
//	 * Variant 1 Check Valid Container
//	 */
//    @Test
//	@Disabled("TODO: repair test")
//    public void test_Valid_with_Respective_XML() throws Exception {
//
//    	InputStream aSiCStream = getInputStream(Path_ValidContainer_ASIC);
//    	InputStream xMLStream = getInputStream(Path_ValidContainer_XML);
//
//    	ECodexContainer container = containerService.receive(aSiCStream, xMLStream);
//
//    	aSiCStream.close();
//    	xMLStream.close();
//
//        CheckResult checkResult = containerService.check(container);
//
//        assertThat(checkResult.getProblems()).isEmpty();
//
//		Assertions.assertTrue(checkResult.isSuccessful());
//		Assertions.assertTrue(!checkResult.isFatal());
//		Assertions.assertTrue(!checkResult.isProblematic());
//    }
//
//	/*
//	 * Variant 2 Check Invalid Container
//	 */
//    @Test
//    public void test_Invalid_with_Respective_XML() throws Exception {
//
//    	InputStream aSiCStream = getInputStream(Path_AlteredContainer_ASIC);
//    	InputStream xMLStream = getInputStream(Path_ValidContainer_XML);
//
//    	ECodexContainer container = containerService.receive(aSiCStream, xMLStream);
//
//    	aSiCStream.close();
//    	xMLStream.close();
//
//        CheckResult checkResult = containerService.check(container);
//
//		Assertions.assertTrue(!checkResult.getProblems().isEmpty());
//
//		Assertions.assertTrue(!checkResult.isSuccessful());
//		Assertions.assertTrue(checkResult.isFatal());
//		Assertions.assertTrue(checkResult.isProblematic());
//
//        CheckProblem curProblem = checkResult.getProblems().get(0);
//
//		Assertions.assertNotSame(curProblem.getMessage(), null);
//		Assertions.assertNotSame(curProblem.getMessage(), "");
//
//		Assertions.assertTrue(curProblem.isFatal());
//    }
//
//    private static InputStream getInputStream(String path){
//
//		InputStream fis = null;
//		Resource f = null;
//
//		try {
//			f = new ClassPathResource(path);
//
//			if (!f.exists()) {
//				throw new IllegalArgumentException("Resource " + path + " does not exist!");
//			}
//
//
//	        fis = f.getInputStream();
//			assertThat(fis).isNotNull();
//
//			return fis;
//		} catch(Exception e) {
//			System.err.println("Exception within the files used for test Test_SIN_IND_1");
//			e.printStackTrace();
//			return null;
//		}
//    }
}
