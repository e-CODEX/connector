package wp4.testenvironment.singletests;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.ContainerToFilesystem;
import wp4.testenvironment.configurations.*;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

/**
 * Contains tests either not being able to create a valid ASiC-S container or
 * being able to create an invalid ASiC-S container using a national implementation of a signature-based, system
 */
public class Test_SND_SNAT_2_Test {
	
//	/**
//	 * Within this test, a working configuration for a national implementation of a signature-based system is
//	 * meant to be tested with an invalid businessContent object.
//	 *
//	 * The respective test is SND_SNAT_2 - Variant 1 - Invalid businessContent object
//	 */
//    @Test
//    public void test_InvalidBusinessContent() throws Exception {
//
//    	final DSSECodexContainerService containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
//
//    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
//    	containerService.setTechnicalValidationService(ValidConfig_SigBasedNationalTechValidator.get_SigBasedNationalTechValidator());
//    	containerService.setLegalValidationService(ValidConfig_NationalLegalValidator.get_NationalLegalValidator_FullData());
//
//    	BusinessContent content = InvalidConfig_BusinessContent.get_EmptyContent();
//    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
//
//    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
//
//    	try {
//    		ECodexContainer container = containerService.create(content, issuer);
//
//    		ContainerToFilesystem.writeFiles("results/SND-SNAT-2_V1", container);
//
//    		CheckResult checkResult = containerService.check(container);
//
//    		if(checkResult.isSuccessful()) {
//    			Assertions.fail("The businesscontent was invalid and a valid container has been created! " +
//    					"The expected result either was an invalid container or an exception at the time of container creation!");
//    		}
//    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
//    		Assertions.assertEquals(e.getMessage(), "the parameter 'business content' is not valid");
//    	} catch(Exception ex) {
//    		Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
//    	}
//    }
//
//	/**
//	 * Within this test, a working configuration for a national implementation of a signature-based system is
//	 * meant to be tested with an invalid tokenIssuer object.
//	 *
//	 * The respective test is SND_SNAT_2 - Variant 2 - Invalid TokenIssuer object
//	 */
//    @Test
//    public void test_InvalidTokenIssuer() throws Exception {
//
//    	final DSSECodexContainerService containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
//
//    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
//    	containerService.setTechnicalValidationService(ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
//    	containerService.setLegalValidationService(ValidConfig_NationalLegalValidator.get_NationalLegalValidator_FullData());
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
//    	TokenIssuer issuer = InvalidConfig_TokenIssuer.get_SigBased_NoCountry();
//
//    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
//
//    	try {
//    		ECodexContainer container = containerService.create(content, issuer);
//
//    		ContainerToFilesystem.writeFiles("results/SND-SNAT-2_V2", container);
//
//    		CheckResult checkResult = containerService.check(container);
//
//    		if(checkResult.isSuccessful()) {
//    			Assertions.fail("The TokenIssuer was invalid and a valid container has been created! " +
//    					"The expected result either was an invalid container or an exception at the time of container creation!");
//    		}
//    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
//    		Assertions.assertEquals(e.getMessage(), "the parameter 'token issuer' is not valid");
//    	} catch(Exception ex) {
//    		Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
//    	}
//    }
//
//	/**
//	 * Within this test, a wrong configuration (invalid legal validator) for a national implementation of a signature-based system is
//	 * meant to be tested with an invalid tokenIssuer object.
//	 *
//	 * The respective test is SND_SNAT_2 - Variant 3 - Invalid Legal Validation Result
//	 */
//    @Test
//    public void test_InvalidLegalValidation() throws Exception {
//
//    	final DSSECodexContainerService containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
//
//    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
//    	containerService.setTechnicalValidationService(ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
//    	containerService.setLegalValidationService(InvalidConfig_NationalLegalValidator.get_NationalLegalValidator_EmptyResult());
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
//    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
//
//    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
//
//    	try {
//    		ECodexContainer container = containerService.create(content, issuer);
//
//    		ContainerToFilesystem.writeFiles("results/SND-SNAT-2_V3", container);
//
//    		CheckResult checkResult = containerService.check(container);
//
//    		if(checkResult.isSuccessful()) {
//    			Assertions.fail("The Legal Validation Result was invalid and a valid container has been created! " +
//    					"The expected result either was an invalid container or an exception at the time of container creation!");
//    		} else {
//    			Assertions.assertTrue(true);
//    		}
//    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
//    		Assertions.assertTrue(true);
//    	} catch(eu.ecodex.dss.service.ECodexException e) {
//    		Assertions.assertTrue(true);
//    	} catch(Exception ex) {
//    		Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
//    	}
//    }
//
//	/**
//	 * Within this test, a wrong configuration (invalid technical validator) for a national
//	 * implementation of a signature-based system is meant to be tested with an invalid
//	 * tokenIssuer object.
//	 *
//	 * The respective test is SND_SNAT_2 - Variant 4 - Invalid Technical Validation Result
//	 */
//    @Test
//    public void test_InvalidTechnicalValidation() throws Exception {
//
//    	final DSSECodexContainerService containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
//
//    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
//    	containerService.setTechnicalValidationService(InvalidConfig_SigBasedNationalTechValidator.get_SigBasedNationalTechValidator_NullResult());
//    	containerService.setLegalValidationService(ValidConfig_NationalLegalValidator.get_NationalLegalValidator_FullData());
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
//    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
//
//    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
//
//    	try {
//    		ECodexContainer container = containerService.create(content, issuer);
//
//    		ContainerToFilesystem.writeFiles("results/SND-SNAT-2_V4", container);
//
//    		CheckResult checkResult = containerService.check(container);
//
//    		if(checkResult.isSuccessful()) {
//    			Assertions.fail("The Technical Validation Result was invalid and a valid container has been created! " +
//    					"The expected result either was an invalid container or an exception at the time of container creation!");
//    		} else {
//    			Assertions.assertTrue(true);
//    		}
//    	} catch(eu.ecodex.dss.service.ECodexBusinessException e) {
//    		Assertions.assertTrue(true);
//    	} catch(eu.ecodex.dss.service.ECodexException e) {
//    		Assertions.assertTrue(true);
//    	} catch(Exception ex) {
//    		Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
//    	}
//    }
}
