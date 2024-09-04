/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.singletests.configuration;

import org.junit.jupiter.api.Disabled;

/**
 * This class represents a test case for validating a valid configuration of a signature-based basic
 * technical validator. The test is related to the SUB-CONF-5 variant 1 scenario.
 *
 * @deprecated This test is currently disabled and needs to be repaired.
 */
// SUB-CONF-5
// TODO repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair tests")
public class ValidConfigSigBasedBasicTechValidatorTest {
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
    //         ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //     containerService.setLegalValidationService(
    //         ValidConfig_NationalLegalValidator.get_NationalLegalValidator_FullData());
    // }
    //
    // /**
    //  * The respective test is SUB-CONF-5 - Variant 1
    //  */
    // @Test
    // @Disabled("repair test!") // TODO
    // public void test_PDF_without_Attachments() throws Exception {
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
