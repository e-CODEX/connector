package wp4.testenvironment.singletests.configuration;

import java.io.IOException;


import eu.ecodex.dss.service.ECodexException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import wp4.testenvironment.configurations.InvalidConfig_SignatureParameters;
import wp4.testenvironment.configurations.ValidConfig_BasicLegalValidator;
import wp4.testenvironment.configurations.ValidConfig_BasicTechValidator;
import wp4.testenvironment.configurations.ValidConfig_BusinessContent;
import wp4.testenvironment.configurations.ValidConfig_CertificateVerifier;
import wp4.testenvironment.configurations.ValidConfig_TokenIssuer;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;

/**
 * Contains tests being able to create a valid ASiC-S container using various
 * possibilities to configure the connector certificates.
 *  SUB-CONF-2
 */
@Disabled
public class Test_InvalidConfig_ConnectorCertificate_Test {
	
	static DSSECodexContainerService containerService;
	
	/**
	 * Initializes all test cases with the same, working configuration.
	 * Test case specific configurations are done within each test case itself. 
	 * @throws IOException 
	 */
	@BeforeAll
	static public void init() throws IOException {
//		containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
//
//    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
//    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
//    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
	}
	
	/*
	 * Variant 1 No Private Key
	 */
	@Test
    public void test_NoKey() {

//		containerService.setContainerSignatureParameters(InvalidConfig_SignatureParameters.get_SignatureParameters_NoPrivateKey());
		
    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
		
//		try {
//			@SuppressWarnings("unused")
//			ECodexContainer container = containerService.create(content, issuer);
//		} catch(eu.ecodex.dss.service.ECodexException e) {
//			Assertions.assertEquals("java.security.InvalidKeyException: Key must not be null", e.getMessage());
//		}
    }
	
	/*
	 * Variant 2 No Certificate
	 */
	@Test
    public void test_NoCert() {

//		containerService.setContainerSignatureParameters(InvalidConfig_SignatureParameters.get_SignatureParameters_NoCertificate());
		
    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
		
//		try {

//			ECodexContainer container = containerService.create(content, issuer);
//		} catch(eu.ecodex.dss.service.ECodexException e) {
//			Assertions.assertEquals("java.lang.NullPointerException", e.getMessage());
//		}
    }
	
	/*
	 * Variant 3 No Certificate Chain
	 */
	@Test
    public void test_NoChain() {

//		containerService.setContainerSignatureParameters(InvalidConfig_SignatureParameters.get_SignatureParameters_NoCertificateChain());
		
    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
		
//		try {
//			@SuppressWarnings("unused")
//			ECodexContainer container = containerService.create(content, issuer);
//		} catch(eu.ecodex.dss.service.ECodexException e) {
//			Assertions.assertEquals("java.lang.NullPointerException", e.getMessage());
//		}
    }
	
	/*
	 * Variant 4 No Digest Algorithm
	 */
	@Test
    public void test_NoDigest() {

//		containerService.setContainerSignatureParameters(InvalidConfig_SignatureParameters.get_SignatureParameters_NoDigestAlgorithm());
		
    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();

    	Assertions.assertThrows(eu.ecodex.dss.service.ECodexException.class, () -> {
//			ECodexContainer container = containerService.create(content, issuer);
		});
    }
	
	/*
	 * Variant 5 No Signature Algorithm
	 */
	@Test
    public void test_NoEncryption() {

//		containerService.setContainerSignatureParameters(InvalidConfig_SignatureParameters.get_SignatureParameters_NoEncryptionAlgorithm());
		
    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();

		Assertions.assertThrows(ECodexException.class, () -> {
//			ECodexContainer container = containerService.create(content, issuer);
		});

	}
}

