package wp4.testenvironment.singletests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
import eu.ecodex.dss.service.ContainerFileDefinitions;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.util.ZipStreamUtil;
import eu.europa.esig.dss.model.DSSDocument;
import util.ContainerToFilesystem;

/*
 * Add Signature to Container
 */
public class Test_SIN_IND_3_Test {
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
//	 * Variant 1 (Checks taken from official Lib-Tests)
//	 */
//    @Test
//	@Disabled("TODO: repair test") //TODO
//    public void test_Add_Signature() throws Exception {
//
//        // The eCodex container has been created
//		Assertions.assertNotNull(container);
//
//        ECodexContainer container2 = containerService.addSignature(container);
//
//        Assertions.assertNotNull(container2);
//        Assertions.assertNotNull(container2.getAsicDocument());
//
//		// check for a new instance
//        Assertions.assertTrue(container != container2);
//
//		// check for the _same_ attributes
//        Assertions.assertTrue(container.getBusinessDocument() == container2.getBusinessDocument());
//        Assertions.assertTrue(container.getTokenPDF() == container2.getTokenPDF());
//        Assertions.assertTrue(container.getTokenXML() == container2.getTokenXML());
//        Assertions.assertTrue(container.getToken() == container2.getToken());
//
//		// check that the asic docs are not the same
//        Assertions.assertTrue(container.getAsicDocument() != container2.getAsicDocument());
//		// even not on binary level
//        Assertions.assertTrue(!binaryEquals(container.getAsicDocument(), container2.getAsicDocument()));
//
//		// extract the signatures and check that they differ
//		final DSSDocument signatures1 = ZipStreamUtil.extract(container.getAsicDocument(), ContainerFileDefinitions.SIGNATURES_REF);
//		Assertions.assertNotNull(signatures1);
//		final DSSDocument signatures2 = ZipStreamUtil.extract(container2.getAsicDocument(), ContainerFileDefinitions.SIGNATURES_REF);
//		Assertions.assertNotNull(signatures2);
//
//		Assertions.assertTrue(!binaryEquals(signatures1, signatures2));
//
//		ContainerToFilesystem.writeFiles("results/SIN_IND_3", container2);
//
//        CheckResult checkResult = containerService.check(container2);
//
//        Assertions.assertTrue(checkResult.isSuccessful());
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
//
//    static boolean binaryEquals(final DSSDocument doc1, final DSSDocument doc2) {
//		try {
//			final InputStream in1 = doc1.openStream();
//			final InputStream in2 = doc2.openStream();
//			final ByteArrayOutputStream bos1 = new ByteArrayOutputStream(16 * 1024);
//			final ByteArrayOutputStream bos2 = new ByteArrayOutputStream(16 * 1024);
//			if (in1 != null) {
//				IOUtils.copy(in1, bos1);
//			}
//			if (in2 != null) {
//				IOUtils.copy(in2, bos2);
//			}
//			final byte[] bytes1 = bos1.toByteArray();
//			final byte[] bytes2 = bos2.toByteArray();
//			return Arrays.equals(bytes1, bytes2);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
}
