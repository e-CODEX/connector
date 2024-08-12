/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.singletests;

import org.junit.jupiter.api.Disabled;

/**
 * Contains tests being able to create a valid ASiC-S container using an authentication-based
 * system.
 */
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair / Complete the  tests")
public class Test_SND_AUTH_1_Test {
    // static DSSECodexContainerService containerService;
    //
    // /**
    //  * Initializes all test cases with the same, working configuration. Test case specific
    //  * configurations are done within each test case itself.
    //  */
    // @BeforeAll
    // static public void init() {
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
    //         ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
    //     containerService.setLegalValidationService(
    //         ValidConfig_NationalLegalValidator.get_NationalLegalValidator_FullData());
    // }
    //
    // /**
    //  * Within this test, a working configuration for an authentication-based system is meant to be
    //  * tested. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
    //  * Business Document &nbsp;&nbsp;- No Attachments - A complete "TokenIssuer" object
    //  * <p>
    //  * The respective test is SND_AUTH_1 - Variant 1 - PDF Business Document - No Attachments
    //  */
    // @Test
    // public void test_PDF_without_Attachments() throws Exception {
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //     TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //     ECodexContainer container = containerService.create(content, issuer);
    //
    //     containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
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
    //     Assertions.assertTrue(checkResult.isSuccessful());
    //
    //     ContainerToFilesystem.writeFiles("results/SND-AUTH-1_V1", container);
    // }
    //
    // /**
    //  * Within this test, a working configuration for an authentication-based system is meant to be
    //  * tested. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
    //  * Business Document &nbsp;&nbsp;- A single Attachment - A complete "TokenIssuer" object
    //  * <p>
    //  * The respective test is SND_AUTH_1 - Variant 2 - PDF Business Document - With Attachments
    //  */
    // @Test
    // public void test_PDF_with_Attachments() throws Exception {
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithAttachments();
    //     TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //     ECodexContainer container = containerService.create(content, issuer);
    //
    //     containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
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
    //     // The Attachments are in place
    //     Assertions.assertNotNull(container.getBusinessAttachments());
    //     Assertions.assertTrue(
    //         DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));
    //
    //     CheckResult checkResult = containerService.check(container);
    //     Assertions.assertTrue(checkResult.isSuccessful());
    //
    //     ContainerToFilesystem.writeFiles("results/SND-AUTH-1_V2", container);
    // }
}
