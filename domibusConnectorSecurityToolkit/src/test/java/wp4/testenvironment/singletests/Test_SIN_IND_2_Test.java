package wp4.testenvironment.singletests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import wp4.testenvironment.configurations.ValidConfig_BasicLegalValidator;
import wp4.testenvironment.configurations.ValidConfig_BasicTechValidator;
import wp4.testenvironment.configurations.ValidConfig_CertificateVerifier;
import wp4.testenvironment.configurations.ValidConfig_SignatureParameters;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.util.DocumentStreamUtil;
import eu.ecodex.dss.util.ZipStreamUtil;

/*
 * Get Documents and Data from Container
 */
public class Test_SIN_IND_2_Test {
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
//	 * Variant 1 ASiC Document
//	 */
//    @Test
//    public void test_ASiC_Document() throws Exception {
//
//        // The eCodex container has been created
//        Assertions.assertNotNull(container);
//        // The ASiC document has been created and contains data
//		Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
//		Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
//    }
//
//	/*
//	 * Variant 2 XML Token
//	 */
//    @Test
//    public void test_XML_Token() throws Exception {
//
//        // The eCodex container has been created
//		Assertions.assertNotNull(container);
//
//        // The XML document has been created and contains data
//		Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
//    }
//
//	/*
//	 * Variant 3 PDF Token
//	 */
//    @Test
//    public void test_PDF_Token() throws Exception {
//
//        // The eCodex container has been created
//		Assertions.assertNotNull(container);
//
//        // The PDF document has been created and contains data
//		Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
//    }
//
//	/*
//	 * Variant 4 Business Document
//	 */
//    @Test
//    public void test_Business_Document() throws Exception {
//
//        // The eCodex container has been created
//		Assertions.assertNotNull(container);
//
//        // Check Contains Business Data
//		Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
//    }
//
//	/*
//	 * Variant 5 Business Attachments
//	 */
//    @Test
//    public void test_Attachments() throws Exception {
//
//        // The eCodex container has been created
//		Assertions.assertNotNull(container);
//
//        // The Attachments are in place
//		Assertions.assertNotNull(container.getBusinessAttachments());
//    }
//
//	/*
//	 * Variant 6 Token Object
//	 */
//    @Test
//	@Disabled("TODO: repair test") //TODO
//    public void test_Token_Object() throws Exception {
//
//        // The eCodex container has been created
//		Assertions.assertNotNull(container);
//
//		Assertions.assertNotNull(container.getToken());
//
//		Assertions.assertEquals(LegalTrustLevel.SUCCESSFUL, container.getToken().getLegalValidationResult().getTrustLevel());
//		Assertions.assertEquals(TechnicalTrustLevel.SUCCESSFUL, container.getToken().getTechnicalValidationResult().getTrustLevel());
//		Assertions.assertEquals(AdvancedSystemType.SIGNATURE_BASED, container.getToken().getAdvancedElectronicSystem());
//
//        CheckResult checkResult = containerService.check(container);
//
//		Assertions.assertTrue(checkResult.isSuccessful());
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
//			System.err.println("Exception within the files used for test Test_SIN_IND_2");
//			e.printStackTrace();
//			return null;
//		}
//    }
}
