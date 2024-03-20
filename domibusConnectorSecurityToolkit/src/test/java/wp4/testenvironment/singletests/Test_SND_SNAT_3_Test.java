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
 * Contains tests not being able to create a ASiC-S container due to invalid configuration
 */
public class Test_SND_SNAT_3_Test {
	
//	/**
//	 * Within this test the configuration of an invalid connector certificate is tested.
//	 *
//	 * The respective test is SND_SNAT_3 - Variant 1 - Invalid Connector Certificate Configuration
//	 */
//    @Test
//    public void test_InvalidSignatureConfiguration() throws Exception {
//
//    	final DSSECodexContainerService containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
//
//    	containerService.setContainerSignatureParameters(InvalidConfig_SignatureParameters.get_SignatureParameters_NoPrivateKey());
//    	containerService.setTechnicalValidationService(ValidConfig_SigBasedNationalTechValidator.get_SigBasedNationalTechValidator());
//    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
//    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
//
//    	containerService.setCertificateVerifier(new CommonCertificateVerifier());
//
//    	try {
//    		ECodexContainer container = containerService.create(content, issuer);
//
//    		ContainerToFilesystem.writeFiles("results/SND-SNAT-3_V1", container);
//
//    		CheckResult checkResult = containerService.check(container);
//
//    		if(checkResult.isSuccessful()) {
//    			Assertions.fail("The businesscontent was invalid and a valid container has been created! " +
//    					"The expected result either was an invalid container or an exception at the time of container creation!");
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
