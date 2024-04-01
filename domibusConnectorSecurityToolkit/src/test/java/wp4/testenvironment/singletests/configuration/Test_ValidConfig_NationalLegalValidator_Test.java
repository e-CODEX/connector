package wp4.testenvironment.singletests.configuration;


import org.junit.jupiter.api.Disabled;


// SUB-CONF-9
@Disabled("TODO: repair test") // TODO
public class Test_ValidConfig_NationalLegalValidator_Test {
    //	/**
    //	 * The respective test is SUB-CONF-9 - Variant 1 Full Data
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
    //    	containerService.setTechnicalValidationService(ValidConfig_AuthBasedNationalTechValidator
    //    	.get_AuthBasedNationalTechValidator());
    //    	containerService.setLegalValidationService(ValidConfig_NationalLegalValidator
    //    	.get_NationalLegalValidator_FullData());
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
    //	/**
    //	 * The respective test is SUB-CONF-6 - Variant 2 No Disclaimer
    //	 */
    //    @Test
    //    public void test_NoDisclaimer() throws Exception {
    //
    //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
    //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
    //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
    //    	pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
    //    	containerService.setTechnicalValidationService(ValidConfig_AuthBasedNationalTechValidator
    //    	.get_AuthBasedNationalTechValidator());
    //    	containerService.setLegalValidationService(ValidConfig_NationalLegalValidator
    //    	.get_NationalLegalValidator_NoDisclaimer());
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
    //
    //        CheckResult checkResult = containerService.check(container);
    //
    //		if (!checkResult.isSuccessful())
    //		{
    //			for(CheckProblem curProblem : checkResult.getProblems())
    //			{
    //				System.out.println(curProblem.getMessage());
    //			}
    //		}
    //		else
    //		{
    //			System.out.println("No problem discovered");
    //		}
    //        Assertions.assertTrue(checkResult.isSuccessful());
    //
    //
    //    }
}
