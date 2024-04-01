package wp4.testenvironment.singletests.configuration;

// SUB-CONF-16
public class Test_InvalidConfig_TokenIssuer_Test {
    //	/*
    //	 * Variant 1 - Authentication Based & Signature Based - No AdvancedElectronicSystem
    //	 */
    //	@Test
    //    public void test_No_System() throws Exception {
    //
    //    	final DSSECodexContainerService containerService = new DSSECodexContainerService(
    //    			technicalValidationService,
    //				legalValidationService,
    //				signingParameters,
    //				certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker,
    //				xmlTokenSignatureChecker, pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //    	.getJKSConfig_By_SigParamFactory());
    //    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator
    //    	.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
    //    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = InvalidConfig_TokenIssuer.get_NoAdvancedElectronicSystem();
    //
    //    	try {
    //    		ECodexContainer container = containerService.create(content, issuer);
    //
    //    		CheckResult checkResult = containerService.check(container);
    //
    //    		if(checkResult.isSuccessful()) {
    //				Assertions.fail("The Token Issuer was invalid and a valid container has been created! " +
    //    					"The expected result either was an invalid container or an exception at the time of
    //    					container creation!");
    //    		}
    //    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
    //    		Assertions.assertTrue(true);
    //    	} catch(eu.ecodex.dss.service.ECodexException keyEx) {
    //			Assertions.assertTrue(true);
    //    	} catch(Exception ex) {
    //			Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //    	}
    //    }
    //
    //	/*
    //	 * Variant 2 - Authentication Based - No Country
    //	 */
    //	@Test
    //    public void test_Auth_No_Country() throws Exception {
    //
    //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
    //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
    //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
    //    	pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //    	.getJKSConfig_By_SigParamFactory());
    //    	containerService.setTechnicalValidationService(ValidConfig_AuthBasedNationalTechValidator
    //    	.get_AuthBasedNationalTechValidator());
    //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
    //    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = InvalidConfig_TokenIssuer.get_AuthBased_NoCountry();
    //
    //    	try {
    //    		ECodexContainer container = containerService.create(content, issuer);
    //
    //    		CheckResult checkResult = containerService.check(container);
    //
    //    		if(checkResult.isSuccessful()) {
    //				Assertions.fail( "The Token Issuer was invalid and a valid container has been created! " +
    //    					"The expected result either was an invalid container or an exception at the time of
    //    					container creation!");
    //    		}
    //    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
    //			Assertions.assertTrue(true);
    //    	} catch(eu.ecodex.dss.service.ECodexException keyEx) {
    //			Assertions.assertTrue(true);
    //    	} catch(Exception ex) {
    //			Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //    	}
    //    }
    //
    //	/*
    //	 * Variant 3 - Authentication Based - No Service Provider
    //	 */
    //	@Test
    //    public void test_Auth_No_Provider() throws Exception {
    //
    //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
    //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
    //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
    //    	pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //    	.getJKSConfig_By_SigParamFactory());
    //    	containerService.setTechnicalValidationService(ValidConfig_AuthBasedNationalTechValidator
    //    	.get_AuthBasedNationalTechValidator());
    //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
    //    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = InvalidConfig_TokenIssuer.get_AuthBased_NoServiceProvider();
    //
    //    	try {
    //    		ECodexContainer container = containerService.create(content, issuer);
    //
    //    		CheckResult checkResult = containerService.check(container);
    //
    //    		if(checkResult.isSuccessful()) {
    //    			Assertions.fail("The Token Issuer was invalid and a valid container has been created! " +
    //    					"The expected result either was an invalid container or an exception at the time of
    //    					container creation!");
    //    		}
    //    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
    //			Assertions.assertTrue(true);
    //    	} catch(eu.ecodex.dss.service.ECodexException keyEx) {
    //			Assertions.assertTrue(true);
    //    	} catch(Exception ex) {
    //			Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //    	}
    //    }
    //
    //	/*
    //	 * Variant 4 - Authentication Based - Not-ISO-3166 Country
    //	 */
    //	@Test
    //    public void test_Auth_No_ISO_Country() throws Exception {
    //
    //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
    //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
    //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
    //    	pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //    	.getJKSConfig_By_SigParamFactory());
    //    	containerService.setTechnicalValidationService(ValidConfig_AuthBasedNationalTechValidator
    //    	.get_AuthBasedNationalTechValidator());
    //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
    //    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = InvalidConfig_TokenIssuer.get_AuthBased_NoISOCountry();
    //
    //    	try {
    //    		ECodexContainer container = containerService.create(content, issuer);
    //
    //    		CheckResult checkResult = containerService.check(container);
    //
    //    		if(checkResult.isSuccessful()) {
    //				Assertions.fail("The Token Issuer was invalid and a valid container has been created! " +
    //    					"The expected result either was an invalid container or an exception at the time of
    //    					container creation!");
    //    		}
    //    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
    //			Assertions.assertTrue(true);
    //    	} catch(eu.ecodex.dss.service.ECodexException keyEx) {
    //			Assertions.assertTrue(true);
    //    	} catch(Exception ex) {
    //			Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //    	}
    //    }
    //
    //	/*
    //	 * Variant 5 - Signature Based - No Country
    //	 */
    //	@Test
    //    public void test_Sig_No_Country() throws Exception {
    //
    //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
    //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
    //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
    //    	pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //    	.getJKSConfig_By_SigParamFactory());
    //    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator
    //    	.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
    //    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = InvalidConfig_TokenIssuer.get_SigBased_NoCountry();
    //
    //    	try {
    //    		ECodexContainer container = containerService.create(content, issuer);
    //
    //    		CheckResult checkResult = containerService.check(container);
    //
    //    		if(checkResult.isSuccessful()) {
    //				Assertions.fail("The Token Issuer was invalid and a valid container has been created! " +
    //    					"The expected result either was an invalid container or an exception at the time of
    //    					container creation!");
    //    		}
    //    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
    //			Assertions.assertTrue(true);
    //    	} catch(eu.ecodex.dss.service.ECodexException keyEx) {
    //			Assertions.assertTrue(true);
    //    	} catch(Exception ex) {
    //			Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //    	}
    //    }
    //
    //	/*
    //	 * Variant 6 - Signature Based - No Service Provider
    //	 */
    //	@Test
    //    public void test_Sig_No_Provider() throws Exception {
    //
    //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
    //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
    //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
    //    	pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //    	.getJKSConfig_By_SigParamFactory());
    //    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator
    //    	.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
    //    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = InvalidConfig_TokenIssuer.get_SigBased_NoServiceProvider();
    //
    //    	try {
    //    		ECodexContainer container = containerService.create(content, issuer);
    //
    //    		CheckResult checkResult = containerService.check(container);
    //
    //    		if(checkResult.isSuccessful()) {
    //				Assertions.fail("The Token Issuer was invalid and a valid container has been created! " +
    //    					"The expected result either was an invalid container or an exception at the time of
    //    					container creation!");
    //    		}
    //    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
    //			Assertions.assertTrue(true);
    //    	} catch(eu.ecodex.dss.service.ECodexException keyEx) {
    //			Assertions.assertTrue(true);
    //    	} catch(Exception ex) {
    //			Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //    	}
    //    }
    //
    //	/*
    //	 * Variant 7 - Signature Based - Not-ISO-3166 Country
    //	 */
    //	@Test
    //    public void test_Sig_No_ISO_Country() throws Exception {
    //
    //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
    //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
    //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
    //    	pdfTokenSignatureChecker);
    //
    //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
    //    	.getJKSConfig_By_SigParamFactory());
    //    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator
    //    	.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
    //    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //    	TokenIssuer issuer = InvalidConfig_TokenIssuer.get_SigBased_NoISOCountry();
    //
    //    	try {
    //    		ECodexContainer container = containerService.create(content, issuer);
    //
    //    		CheckResult checkResult = containerService.check(container);
    //
    //    		if(checkResult.isSuccessful()) {
    //				Assertions.fail("The Token Issuer was invalid and a valid container has been created! " +
    //    					"The expected result either was an invalid container or an exception at the time of
    //    					container creation!");
    //    		}
    //    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
    //			Assertions.assertTrue(true);
    //    	} catch(eu.ecodex.dss.service.ECodexException keyEx) {
    //			Assertions.assertTrue(true);
    //    	} catch(Exception ex) {
    //			Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //    	}
    //    }
}
