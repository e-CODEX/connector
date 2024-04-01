package wp4.testenvironment.singletests.configuration;


import org.junit.jupiter.api.Disabled;


// SUB-CONF-10
@Disabled("TODO: repair test")
public class Test_ValidConfig_BasicLegalValidator_Test {
    //	/**
    //	 * The respective test is SUB-CONF-9 - Variant 1
    //	 */
    //    @Test
    //    public void test_FullData() throws Exception {
    //
    //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
    //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
    //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
    //    	pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
    //    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator
    //    	.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
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
