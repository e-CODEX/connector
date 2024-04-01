package wp4.testenvironment.singletests.configuration;

// SUB-CONF-5
public class Test_ValidConfig_SigBasedBasicTechValidator_Test {
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
    //		containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService,
    //		signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor,
    //		asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
    //    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator
    //    	.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //    	containerService.setLegalValidationService(ValidConfig_NationalLegalValidator
    //    	.get_NationalLegalValidator_FullData());
    //	}
    //
    //	/**
    //	 * The respective test is SUB-CONF-5 - Variant 1
    //	 */
    //	@Test
    //	@Disabled("repair test!") //TODO
    //    public void test_PDF_without_Attachments() throws Exception {
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
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
