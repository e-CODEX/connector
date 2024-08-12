/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.singletests;

import org.junit.jupiter.api.Disabled;

/**
 * Contains tests being able to create a valid ASiC-S container using the basic implementation of
 * a signature-based system.
 */
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair / Complete the  tests")
public class Test_SND_SBSC_1_Plus_Filter_Test {
    // static DSSECodexContainerService containerService;
    //
    // /**
    //  * Initializes all test cases with the same, working configuration. Test case specific
    //  * configurations are done within each test case itself.
    //  *
    //  * @throws IOException
    //  */
    // @BeforeAll
    // public static void init() throws IOException {
    //     containerService =
    //         new DSSECodexContainerService(technicalValidationService, legalValidationService,
    //                                       signingParameters, certificateVerifier,
    //                                       connectorCertificatesSource, processExecutor,
    //                                       asicsSignatureChecker, xmlTokenSignatureChecker,
    //                                       pdfTokenSignatureChecker
    //         );
    //
    //     containerService.setContainerSignatureParameters(
    //         ValidConfig_SignatureParameters.getJKSConfiguration());
    //     containerService.setTechnicalValidationService(
    //         ValidConfig_BasicTechValidator.get_BasicTechValidator_WithSignatureFilter());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    // }
    //
    // /*
    //  * The respective test is SND_SBSC_1 + - Multiple times signed PDF Supported by DSS, one signature should be filtered
    //  */
    // @Test
    // @Disabled("TODO: repair test") // TODO
    // public void test_MultipleSignature() throws Exception {
    //
    //     // BusinessContent content = ValidConfig_BusinessContent.get_MultisignedFile_One_Invalid_WithoutAttachments();
    //     BusinessContent content =
    //         ValidConfig_BusinessContent.get_MultisignedFile_WithoutAttachments();
    //     TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
    //
    //     ECodexContainer container = containerService.create(content, issuer);
    //     ContainerToFilesystem.writeFiles("results/SND-SBSC-1-Plus-F_V1", container);
    //     // The eCodex container has been created
    //     Assertions.assertNotNull(container);
    //     // The ASiC document has been created and contains data
    //     Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
    //     Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
    //     // The PDF document has been created and contains data
    //     Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
    //     // The XML document has been created and contains data
    //     Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
    //     Assertions.assertNotNull(container.getToken());
    //     // Check Contains Business Data
    //     Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
    //     CheckResult checkResult = containerService.check(container);
    //
    //     Assertions.assertEquals(
    //         LegalTrustLevel.SUCCESSFUL,
    //         container.getToken().getLegalValidationResult().getTrustLevel()
    //     );
    //     Assertions.assertEquals(
    //         TechnicalTrustLevel.SUCCESSFUL,
    //         container.getToken().getTechnicalValidationResult().getTrustLevel()
    //     );
    //
    //     Assertions.assertTrue(container.getToken().getValidation() != null);
    //     Assertions.assertTrue(container.getToken().getValidation().getVerificationData() != null);
    //     Assertions.assertTrue(
    //         container.getToken().getValidation().getVerificationData().getSignatureData() != null);
    //
    //     List<Signature> signatures =
    //         container.getToken().getValidation().getVerificationData().getSignatureData();
    //     Assertions.assertTrue(signatures.size() == 2);
    //
    //     for (Signature signature : signatures) {
    //         Assertions.assertTrue(signature.getTechnicalResult() != null);
    //         Assertions.assertEquals(
    //             TechnicalTrustLevel.SUCCESSFUL, signature.getTechnicalResult().getTrustLevel());
    //         Assertions.assertTrue(signature.getTechnicalResult().getComment() != null);
    //     }
    //
    //     Assertions.assertTrue(checkResult.isSuccessful());
    //
    //     ContainerToFilesystem.writeFiles("results/SND-SBSC-1-Plus-F_V1", container);
    // }
}
