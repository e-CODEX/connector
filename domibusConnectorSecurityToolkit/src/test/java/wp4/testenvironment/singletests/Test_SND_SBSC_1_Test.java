package wp4.testenvironment.singletests;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.ContainerToFilesystem;
import wp4.testenvironment.configurations.ValidConfig_BasicLegalValidator;
import wp4.testenvironment.configurations.ValidConfig_BasicTechValidator;
import wp4.testenvironment.configurations.ValidConfig_BusinessContent;
import wp4.testenvironment.configurations.ValidConfig_CertificateVerifier;
import wp4.testenvironment.configurations.ValidConfig_SignatureParameters;
import wp4.testenvironment.configurations.ValidConfig_TokenIssuer;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.util.DocumentStreamUtil;
import eu.ecodex.dss.util.ZipStreamUtil;

/**
 * Contains tests being able to create a valid ASiC-S container using a the basic implementation 
 * of a signature-based system
 */
@Disabled("TODO: repair test") //TODO
public class Test_SND_SBSC_1_Test {
	
//	static DSSECodexContainerService containerService;
//
//	/**
//	 * Initializes all test cases with the same, working configuration.
//	 * Test case specific configurations are done within each test case itself.
//	 * @throws IOException
//	 */
//	@BeforeAll
//	public static void init() throws IOException
//	{
//		containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
//
//    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
//    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
//    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
//    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
//	}
//
//	/**
//	 * Within this test, a working configuration for the basic implementation
//	 * of a signature-based system
//	 * The test contains:
//	 * - A complete "BusinessContent" object with
//	 * &nbsp;&nbsp;- A PDF Business Document
//	 * &nbsp;&nbsp;- No Attachments
//	 * - A complete "TokenIssuer" object
//	 *
//	 * The respective test is SND_SBSC_1 - Variant 1 - PDF Business Document - No Attachments
//	 */
//    @Test
//    public void test_WithoutSignature() throws Exception {
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
//    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
//
//    	ECodexContainer container = containerService.create(content, issuer);
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
//        Assertions.assertEquals(LegalTrustLevel.NOT_SUCCESSFUL, container.getToken().getLegalValidationResult().getTrustLevel());
//        Assertions.assertEquals(TechnicalTrustLevel.FAIL, container.getToken().getTechnicalValidationResult().getTrustLevel());
//
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        ContainerToFilesystem.writeFiles("results/SND-SBSC-1_V1", container);
//    }
//
//	/**
//	 * Within this test, a working configuration for the basic implementation
//	 * of a signature-based system
//	 * The test contains:
//	 * - A complete "BusinessContent" object with
//	 * &nbsp;&nbsp;- A signed PDF Business Document
//	 * &nbsp;&nbsp;- A single Attachment
//	 * - A complete "TokenIssuer" object
//	 *
//	 * The respective test is SND_SBSC_1 - Variant 2 - Signed PDF Business Document - With Attachments
//	 */
//    @Test
//    public void test_WithSignature() throws Exception {
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
//    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
//
//    	ECodexContainer container = containerService.create(content, issuer);
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
//        Assertions.assertEquals(LegalTrustLevel.SUCCESSFUL, container.getToken().getLegalValidationResult().getTrustLevel());
//        Assertions.assertEquals(TechnicalTrustLevel.SUCCESSFUL, container.getToken().getTechnicalValidationResult().getTrustLevel());
//
//        CheckResult checkResult = containerService.check(container);
//
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        ContainerToFilesystem.writeFiles("results/SND-SBSC-1_V2", container);
//    }
//
//	/**
//	 * Within this test, a working configuration for the basic implementation
//	 * of a signature-based system
//	 * The test contains:
//	 * - A complete "BusinessContent" object with
//	 * &nbsp;&nbsp;- A signed, not supported PDF Business Document
//	 * &nbsp;&nbsp;- A single Attachment
//	 * - A complete "TokenIssuer" object
//	 *
//	 * The respective test is SND_SBSC_1 - Variant 3 - Signed PDF Business Document
//	 * - Signature not Supported by DSS - With Attachments
//	 */
//    @Test
//    public void test_UnsupportedSignature() throws Exception {
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_UnsupportedSignedFile_WithAttachments();
//    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
//
//    	ECodexContainer container = containerService.create(content, issuer);
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
//        Assertions.assertEquals(LegalTrustLevel.NOT_SUCCESSFUL, container.getToken().getLegalValidationResult().getTrustLevel());
//        Assertions.assertEquals(TechnicalTrustLevel.FAIL, container.getToken().getTechnicalValidationResult().getTrustLevel());
//
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        ContainerToFilesystem.writeFiles("results/SND-SBSC-1_V3", container);
//    }
//
//	/**
//	 * Within this test, a working configuration for the basic implementation
//	 * of a signature-based system
//	 * The test contains:
//	 * - A complete "BusinessContent" object with
//	 * &nbsp;&nbsp;- A PDF Business Document
//	 * &nbsp;&nbsp;- A detached Signature for the business document
//	 * &nbsp;&nbsp;- A single Attachment
//	 * - A complete "TokenIssuer" object
//	 *
//	 * The respective test is SND_SBSC_1 - Variant 4 - Detached-Signed PDF Business Document
//	 * - Signature Supported by DSS - With Attachments
//	 */
//    @Test
//    public void test_DetachedSignature() throws Exception {
//
//    	BusinessContent content = ValidConfig_BusinessContent.get_DetachedSignedFile_WithAttachments();
//    	TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
//
//    	ECodexContainer container = containerService.create(content, issuer);
//    	ContainerToFilesystem.writeFiles("results/SND-SBSC-1_V4", container);
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
//        Assertions.assertEquals(LegalTrustLevel.SUCCESSFUL, container.getToken().getLegalValidationResult().getTrustLevel());
//        Assertions.assertEquals(TechnicalTrustLevel.SUCCESSFUL, container.getToken().getTechnicalValidationResult().getTrustLevel());
//
//        Assertions.assertTrue(checkResult.isSuccessful());
//
//        ContainerToFilesystem.writeFiles("results/SND-SBSC-1_V4", container);
//    }
}
