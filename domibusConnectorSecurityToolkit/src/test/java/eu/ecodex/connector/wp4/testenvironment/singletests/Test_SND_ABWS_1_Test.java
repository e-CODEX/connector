/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.singletests;

import org.junit.jupiter.api.Disabled;

/**
 * Contains tests being able to create a valid ASiC-S container using an authentication-based system
 * with signatures.
 */
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair / Complete the  tests")
public class Test_SND_ABWS_1_Test {
    // static DSSECodexContainerService containerService;
    //
    // /**
    //  * Initializes all test cases with the same, working configuration. Test case specific
    //  * configurations are done within each test case itself.
    //  *
    //  * @throws IOException
    //  */
    // @BeforeAll
    // static public void init() throws IOException {
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
    //         ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_WithAuthCertConfig());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    // }
    //
    // /**
    //  * Within this test, a working configuration for an authentication-based system with signature
    //  * is meant to be tested. The test contains: - A complete "BusinessContent" object with
    //  * &nbsp;&nbsp;- A PDF Business Document &nbsp;&nbsp;- A single Attachment - A complete
    //  * "TokenIssuer" object
    //  */
    // @Test
    // public void test_WithoutSignature() throws Exception {
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
    //
    //     Assertions.assertTrue(checkResult.isSuccessful());
    //
    //     // Result of the token must be SUCCESSFUL as the certificate has to be present within the TSL
    //     Assertions.assertTrue(container.getToken().getLegalValidationResultTrustLevel()
    //                                    .equals(LegalTrustLevel.NOT_SUCCESSFUL));
    //
    //     ContainerToFilesystem.writeFiles("results/SND-ABWS-1_V1", container);
    // }
    //
    // /**
    //  * Within this test, a working configuration for an authentication-based system with signature
    //  * is meant to be tested. The test contains: - A complete "BusinessContent" object with
    //  * &nbsp;&nbsp;- A signed PDF Business Document &nbsp;&nbsp;- A single Attachment - A complete
    //  * "TokenIssuer" object
    //  */
    // @Test
    // public void test_WithSignature() throws Exception {
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
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
    //     // Result of the token must be SUCCESSFUL as the certificate has to be present within the TSL
    //     Assertions.assertTrue(container.getToken().getLegalValidationResultTrustLevel()
    //                                    .equals(LegalTrustLevel.SUCCESSFUL));
    //
    //     ContainerToFilesystem.writeFiles("results/SND-ABWS-1_V2", container);
    // }
}
