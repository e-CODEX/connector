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
 * A test class for testing the functionality of the SND_AMIX_1_Test class.
 */
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair / Complete the  tests")
public class Test_SND_AMIX_1_Test {
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
    //         ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    // }
    //
    // /**
    //  * Within this test, a working configuration for the combined implementations of a
    //  * signature-based system (National Implementation of Tech. Validator and Basic Legal Validator)
    //  * is tested The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A
    //  * unsigned PDF Business Document - A complete "TokenIssuer" object
    //  * <p>
    //  * The respective test is SND_AMIX_1 - Variant 1 - PDF Business Document - No Attachments
    //  */
    // @Test
    // public void test_NoAttachment() throws Exception {
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //     TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //     ECodexContainer container = containerService.create(content, issuer);
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
    //
    //     Assertions.assertEquals(
    //         LegalTrustLevel.SUCCESSFUL,
    //         container.getToken().getLegalValidationResult().getTrustLevel()
    //     );
    //     Assertions.assertEquals(
    //         TechnicalTrustLevel.SUCCESSFUL,
    //         container.getToken().getTechnicalValidationResult().getTrustLevel()
    //     );
    //     Assertions.assertEquals(
    //         AdvancedSystemType.AUTHENTICATION_BASED,
    //         container.getToken().getAdvancedElectronicSystem()
    //     );
    //
    //     CheckResult checkResult = containerService.check(container);
    //
    //     Assertions.assertTrue(checkResult.isSuccessful());
    //
    //     ContainerToFilesystem.writeFiles("results/SND-AMIX-1_V1", container);
    // }
    //
    // /**
    //  * Within this test, a working configuration for the combined implementations of a
    //  * signature-based system (National Implementation of Tech. Validator and Basic Legal Validator)
    //  * is tested The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A
    //  * unsigned PDF Business Document &nbsp;&nbsp;- An attached Document - A complete "TokenIssuer"
    //  * object
    //  * <p>
    //  * The respective test is SND_AMIX_1 - Variant 2 - PDF Business Document - No Attachments
    //  */
    // @Test
    // public void test_WithAttachment() throws Exception {
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithAttachments();
    //     TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //     ECodexContainer container = containerService.create(content, issuer);
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
    //     Assertions.assertEquals(
    //         LegalTrustLevel.SUCCESSFUL,
    //         container.getToken().getLegalValidationResult().getTrustLevel()
    //     );
    //     Assertions.assertEquals(
    //         TechnicalTrustLevel.SUCCESSFUL,
    //         container.getToken().getTechnicalValidationResult().getTrustLevel()
    //     );
    //     Assertions.assertEquals(
    //         AdvancedSystemType.AUTHENTICATION_BASED,
    //         container.getToken().getAdvancedElectronicSystem()
    //     );
    //
    //     CheckResult checkResult = containerService.check(container);
    //
    //     Assertions.assertTrue(checkResult.isSuccessful());
    //
    //     ContainerToFilesystem.writeFiles("results/SND-AMIX-1_V2", container);
    // }
}
