package wp4.testenvironment.singletests.configuration;

import org.junit.jupiter.api.Disabled;


/**
 * Contains tests being able to create a valid ASiC-S container using various
 * possibilities to configure the connector certificates.
 * - SUB-CONF-1
 */
@Disabled("TODO: repair test") // TODO
public class Test_ValidConfig_ConnectorCertificate_Test {
    //	static DSSECodexContainerService containerService;
    //
    //	/**
    //	 * Initializes all test cases with the same, working configuration.
    //	 * Test case specific configurations are done within each test case itself.
    //	 */
    //	@BeforeAll
    //	static public void init() {
    //		containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService,
    //		signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor,
    //		asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
    //
    //    	containerService.setTechnicalValidationService(ValidConfig_AuthBasedNationalTechValidator
    //    	.get_AuthBasedNationalTechValidator());
    //    	containerService.setLegalValidationService(ValidConfig_NationalLegalValidator
    //    	.get_NationalLegalValidator_FullData());
    //	}
    //
    //	@Test
    //    public void test_JKS_Configuration() throws Exception {
    //
    //		containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //    	ECodexContainer container = containerService.create(content, issuer);
    //
    //    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //        // The eCodex container has been created
    //        Assertions.assertNotNull(container);
    //        // The ASiC document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
    //        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
    //        // The PDF document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
    //        // The XML document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
    //        Assertions.assertNotNull(container.getToken());
    //        //Check Contains Business Data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
    //        CheckResult checkResult = containerService.check(container);
    //        Assertions.assertTrue(checkResult.isSuccessful());
    //    }
    //
    //	@Test
    //    public void test_PKCS12_Configuration() throws Exception {
    //
    //		containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getPKCS12Configuration());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //    	ECodexContainer container = containerService.create(content, issuer);
    //
    //    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //        // The eCodex container has been created
    //        Assertions.assertNotNull(container);
    //        // The ASiC document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
    //        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
    //        // The PDF document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
    //        // The XML document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
    //        Assertions.assertNotNull(container.getToken());
    //        //Check Contains Business Data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
    //        CheckResult checkResult = containerService.check(container);
    //
    //        Assertions.assertTrue(checkResult.isSuccessful());
    //    }
    //
    //	/**
    //	 * In case of failure the reason could be that "JKS" is not the default keystore type of the Java VM
    //	 * @throws Exception
    //	 */
    //	@Test
    //    public void test_JKSConfig_By_SigParamFactory() throws Exception {
    //
    //		containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //		.getJKSConfig_By_SigParamFactory());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //    	ECodexContainer container = containerService.create(content, issuer);
    //
    //    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //        // The eCodex container has been created
    //        Assertions.assertNotNull(container);
    //        // The ASiC document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
    //        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
    //        // The PDF document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
    //        // The XML document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
    //        Assertions.assertNotNull(container.getToken());
    //        //Check Contains Business Data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
    //        CheckResult checkResult = containerService.check(container);
    //
    //        Assertions.assertTrue(checkResult.isSuccessful());
    //    }
    //
    //	/**
    //	 * This test is optional!
    //	 * In case of failure the reason could be that "PKCS12" is not the default keystore type of the Java VM
    //	 * @throws Exception
    //	 */
    //	// Deactivated: Fails automatically when Java default keystore type is set to JKS
    //	//@Test
    //    public void test_PKCS12Config_By_SigParamFactory() throws Exception {
    //
    //		try{
    //			containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //			.getPKCS12Config_By_SigParamFactory());
    //		} catch (IOException ex) {
    //			Assertions.fail("This test is optional! The usage of PKCS12 keystores only works with PKCS12 being the
    //			default keystore type within the used Java VM.", ex);
    //		}
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //    	ECodexContainer container = containerService.create(content, issuer);
    //
    //    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //        // The eCodex container has been created
    //        Assertions.assertNotNull(container);
    //        // The ASiC document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
    //        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
    //        // The PDF document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
    //        // The XML document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
    //        Assertions.assertNotNull(container.getToken());
    //        //Check Contains Business Data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
    //        CheckResult checkResult = containerService.check(container);
    //
    //        Assertions.assertTrue(checkResult.isSuccessful());
    //    }
    //
    //	@Test
    //    public void test_JKS_Configuration_SHA256() throws Exception {
    //
    //		containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //		.getJKSConfiguration_SHA256());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //    	ECodexContainer container = containerService.create(content, issuer);
    //
    //    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //        // The eCodex container has been created
    //        Assertions.assertNotNull(container);
    //        // The ASiC document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
    //        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
    //        // The PDF document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
    //        // The XML document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
    //        Assertions.assertNotNull(container.getToken());
    //        //Check Contains Business Data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
    //        CheckResult checkResult = containerService.check(container);
    //        Assertions.assertTrue(checkResult.isSuccessful());
    //    }
    //
    //	@Test
    //    public void test_JKS_Configuration_SHA512() throws Exception {
    //
    //		containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //		.getJKSConfiguration_SHA512());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //    	ECodexContainer container = containerService.create(content, issuer);
    //
    //    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //        // The eCodex container has been created
    //        Assertions.assertNotNull(container);
    //        // The ASiC document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
    //        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
    //        // The PDF document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
    //        // The XML document has been created and contains data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
    //        Assertions.assertNotNull(container.getToken());
    //        //Check Contains Business Data
    //        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
    //        CheckResult checkResult = containerService.check(container);
    //        Assertions.assertTrue(checkResult.isSuccessful());
    //    }
}

