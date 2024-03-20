package wp4.testenvironment.singletests.configuration;


import eu.ecodex.dss.service.impl.dss.ConnectorCertificatesStore;
import eu.ecodex.dss.util.tsl.LotlCreator;
import eu.europa.esig.dss.validation.executor.DocumentProcessExecutor;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import wp4.testenvironment.configurations.ValidConfig_BasicLegalValidator;
import wp4.testenvironment.configurations.ValidConfig_BusinessContent;
import wp4.testenvironment.configurations.ValidConfig_SignatureParameters;
import wp4.testenvironment.configurations.ValidConfig_TokenIssuer;
import wp4.testenvironment.configurations.ValidConfig_CertificateVerifier;
import wp4.testenvironment.configurations.ValidConfig_BasicTechValidator_AuthCertificateTSL;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import eu.ecodex.dss.util.DocumentStreamUtil;
import eu.ecodex.dss.util.ZipStreamUtil;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import util.ContainerToFilesystem;

import java.util.Optional;

@Disabled("TODO: repair test")
public class Test_ValidConfig_BasicTechValidator_AuthCertificateTSL_Test {
//
//	static DSSECodexContainerService containerService;
//
//
//
//	@BeforeAll
//	static public void init() {
//		containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
//
//    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
//    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
//
//	}
//
//
//    private static DocumentProcessExecutor processExecutor = new DefaultSignatureProcessExecutor();
//	private Optional<ConnectorCertificatesStore> emptyOptional = Optional.empty();
//
//	/*
//	 * Variant 1 - Configuration by URIString using TSL
//	 */
//    @Test
//    public void test_Configuration_by_URIString_using_TSL() throws Exception {
//
//    	DSSECodexTechnicalValidationService validationService = new DSSECodexTechnicalValidationService(ValidConfig_CertificateVerifier.get_WithProxy(), processExecutor, Optional.of(LotlCreator.createTrustedListsCertificateSource(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_URIString_with_TSL())), Optional.empty());
//
////    	validationService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
////    	validationService.setAuthenticationCertificateTSL(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_URIString_with_TSL());
////    	validationService.initAuthenticationCertificateVerification();
//
//    	containerService.setTechnicalValidationService(validationService);
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
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
//        // Check Contains Business Data
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
//        // The Attachments are in place
//        Assertions.assertNotNull(container.getBusinessAttachments());
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));
//
//        CheckResult checkResult = containerService.check(container);
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        // Result of the token must be SUCCESSFUL as the certificate has to be present within the TSL
//        Assertions.assertTrue(container.getToken().getLegalValidationResultTrustLevel().equals(LegalTrustLevel.SUCCESSFUL));
//    }
//
//	/*
//	 * Variant 2 - Configuration by FileInputStream using TSL
//	 */
//    @Test
//    public void test_Configuration_by_FileInputStream_using_TSL() throws Exception {
//
//    	DSSECodexTechnicalValidationService validationService = new DSSECodexTechnicalValidationService(ValidConfig_CertificateVerifier.get_WithProxy(),
//                processExecutor,
//                Optional.of(LotlCreator.createTrustedListsCertificateSource(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_FileInputStream_with_TSL())),
//                Optional.empty());
//
////    	validationService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
////
////    	validationService.setAuthenticationCertificateTSL(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_FileInputStream_with_TSL());
////    	validationService.initAuthenticationCertificateVerification();
//
//    	containerService.setTechnicalValidationService(validationService);
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
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
//        // Check Contains Business Data
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
//        // The Attachments are in place
//        Assertions.assertNotNull(container.getBusinessAttachments());
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));
//
//        CheckResult checkResult = containerService.check(container);
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        // Result of the token must be SUCCESSFUL as the certificate has to be present within the TSL
//        Assertions.assertTrue(container.getToken().getLegalValidationResultTrustLevel().equals(LegalTrustLevel.SUCCESSFUL));
//    }
//
//	/*
//	 * Variant 3 - Configuration by ByteArray using TSL
//	 */
//    @Test
//    public void test_Configuration_by_ByteArray_using_TSL() throws Exception {
//
//    	DSSECodexTechnicalValidationService validationService = new DSSECodexTechnicalValidationService(ValidConfig_CertificateVerifier.get_WithProxy(),
//                processExecutor,
//                Optional.of(LotlCreator.createTrustedListsCertificateSource(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_ByteArray_with_TSL())),
//                Optional.empty());
//
////    	validationService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
////
////    	validationService.setAuthenticationCertificateTSL(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_ByteArray_with_TSL());
////    	validationService.initAuthenticationCertificateVerification();
//
//    	containerService.setTechnicalValidationService(validationService);
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
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
//        // Check Contains Business Data
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
//        // The Attachments are in place
//        Assertions.assertNotNull(container.getBusinessAttachments());
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));
//
//        CheckResult checkResult = containerService.check(container);
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        // Result of the token must be SUCCESSFUL as the certificate has to be present within the TSL
//        Assertions.assertTrue(container.getToken().getLegalValidationResultTrustLevel().equals(LegalTrustLevel.SUCCESSFUL));
//    }
//
//	/*
//	 * Variant 4 - Configuration by URIString using LOTL
//	 */
//    @Test
//    public void test_Configuration_by_URIString_using_LOTL() throws Exception {
//
//    	DSSECodexTechnicalValidationService validationService = new DSSECodexTechnicalValidationService(ValidConfig_CertificateVerifier.get_WithProxy(),
//                processExecutor,
//                Optional.of(LotlCreator.createTrustedListsCertificateSource(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_URIString_with_LOTL(), true, null)),
//                emptyOptional);
//
////    	validationService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
////    	validationService.setAuthenticationCertificateTSL(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_URIString_with_LOTL());
////    	validationService.isAuthenticationCertificateLOTL(true);
////    	validationService.initAuthenticationCertificateVerification();
//
//    	containerService.setTechnicalValidationService(validationService);
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
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
//        // Check Contains Business Data
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
//        // The Attachments are in place
//        Assertions.assertNotNull(container.getBusinessAttachments());
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));
//
//        CheckResult checkResult = containerService.check(container);
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        // Result of the token must be SUCCESSFUL as the certificate has to be present within the TSL
//        Assertions.assertTrue(container.getToken().getLegalValidationResultTrustLevel().equals(LegalTrustLevel.SUCCESSFUL));
//    }
//
//	/*
//	 * Variant 5 - Configuration by FileInputStream using LOTL
//	 */
//    @Test
//    public void test_Configuration_by_FileInputStream_using_LOTL() throws Exception {
//
//    	DSSECodexTechnicalValidationService validationService = new DSSECodexTechnicalValidationService(ValidConfig_CertificateVerifier.get_WithProxy(),
//                processExecutor,
//                Optional.of(LotlCreator.createTrustedListsCertificateSource(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_FileInputStream_with_LOTL(), true, null)),
//                emptyOptional);
//
////    	validationService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
////
////    	validationService.setAuthenticationCertificateTSL(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_FileInputStream_with_LOTL());
////    	validationService.isAuthenticationCertificateLOTL(true);
////    	validationService.initAuthenticationCertificateVerification();
//
//    	containerService.setTechnicalValidationService(validationService);
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
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
//        // Check Contains Business Data
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
//        // The Attachments are in place
//        Assertions.assertNotNull(container.getBusinessAttachments());
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));
//
//        CheckResult checkResult = containerService.check(container);
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        // Result of the token must be SUCCESSFUL as the certificate has to be present within the TSL
//        Assertions.assertTrue(container.getToken().getLegalValidationResultTrustLevel().equals(LegalTrustLevel.SUCCESSFUL));
//    }
//
//	/*
//	 * Variant 6 - Configuration by ByteArray using LOTL
//	 */
//    @Test
//    public void test_Configuration_by_ByteArray_using_LOTL() throws Exception {
//
//    	DSSECodexTechnicalValidationService validationService = new DSSECodexTechnicalValidationService(ValidConfig_CertificateVerifier.get_WithProxy(),
//                processExecutor,
//                Optional.of(LotlCreator.createTrustedListsCertificateSource(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_ByteArray_with_LOTL(), true, null)),
//                emptyOptional);
//
////    	validationService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
////
////    	validationService.setAuthenticationCertificateTSL(ValidConfig_BasicTechValidator_AuthCertificateTSL.get_ByteArray_with_LOTL());
////    	validationService.isAuthenticationCertificateLOTL(true);
////    	validationService.initAuthenticationCertificateVerification();
//
//    	containerService.setTechnicalValidationService(validationService);
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
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
//        // Check Contains Business Data
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
//        // The Attachments are in place
//        Assertions.assertNotNull(container.getBusinessAttachments());
//        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));
//
//        CheckResult checkResult = containerService.check(container);
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        ContainerToFilesystem.writeFiles("results/Test", container);
//
//        // Result of the token must be SUCCESSFUL as the certificate has to be present within the TSL
//        Assertions.assertTrue(container.getToken().getLegalValidationResultTrustLevel().equals(LegalTrustLevel.SUCCESSFUL));
//    }
}
