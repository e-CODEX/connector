/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.singletests;

import org.junit.jupiter.api.Disabled;

/**
 * Contains tests not being able to create a valid ASiC-S container using an authentication-based
 * system with signatures The reason for not being able to do so is invalid configuration of the
 * container service.
 */
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair / Complete the  tests")
public class Test_SND_ABWS_2_Test {
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
    //         InvalidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_WithInvalidAuthCertConfig());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    // }
    //
    // /*
    //  * Variant 1 - Invalid configuration for TSL
    //  */
    // @Test
    // @Disabled("TODO: repair test!")
    // public void test_InvalidConfigurationForTSL() throws Exception {
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
    //
    //     Assertions.assertTrue(checkResult.isSuccessful());
    //
    //     // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within the TSL (and no TSL has been defined)
    //     Assertions.assertTrue(container.getToken().getLegalValidationResultTrustLevel()
    //                                    .equals(LegalTrustLevel.NOT_SUCCESSFUL));
    //
    //     // Result of the technical validation of the token must be SUCCESSFUL the usual check against the European TSL still takes place
    //     Assertions.assertTrue(container.getToken().getTechnicalValidationResultTrustLevel()
    //                                    .equals(TechnicalTrustLevel.SUCCESSFUL));
    //
    //     ContainerToFilesystem.writeFiles("results/SND-ABWS-2_V1", container);
    // }
}
