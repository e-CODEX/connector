/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.singletests.configuration;

import org.junit.jupiter.api.Disabled;

/**
 * The Test_ValidConfig_BasicLegalValidator_Test class represents a test class for validating the
 * basic legal configurations.
 */
// SUB-CONF-10
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair tests")
public class ValidConfigBasicLegalValidatorTest {
    // /**
    //  * The respective test is SUB-CONF-9 - Variant 1
    //  */
    // @Test
    // public void test_FullData() throws Exception {
    //
    //     final DSSECodexContainerService containerService =
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
    //         ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //     TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
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
    // }
}
