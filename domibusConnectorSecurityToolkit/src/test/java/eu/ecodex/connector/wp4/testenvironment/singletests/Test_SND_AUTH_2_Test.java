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
 * Contains tests either not being able to create a valid ASiC-S container or being able to create
 * an invalid ASiC-S container using an authentication-based system.
 */
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair / Complete the  tests")
public class Test_SND_AUTH_2_Test {
    // /**
    //  * Within this test, a working configuration for an authentication-based system is meant to be
    //  * tested with an invalid businessContent object.
    //  * <p>
    //  * The respective test is SND_AUTH_2 - Variant 1 - Invalid businessContent object
    //  */
    // @Test
    // public void test_InvalidBusinessContent() throws Exception {
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
    //         ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
    //     containerService.setLegalValidationService(
    //         ValidConfig_NationalLegalValidator.get_NationalLegalValidator_FullData());
    //
    //     BusinessContent content = InvalidConfig_BusinessContent.get_EmptyContent();
    //     TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //     containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //
    //         CheckResult checkResult = containerService.check(container);
    //         ContainerToFilesystem.writeFiles("results/SND-AUTH-2_V1", container);
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The businesscontent was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertEquals(
    //             e.getMessage(), "the parameter 'business content' is not valid");
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
    //
    // /**
    //  * Within this test, a working configuration for an authentication-based system is meant to be
    //  * tested with an invalid tokenIssuer object.
    //  * <p>
    //  * The respective test is SND_AUTH_2 - Variant 2 - Invalid TokenIssuer object
    //  */
    // @Test
    // public void test_InvalidTokenIssuer() throws Exception {
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
    //         ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
    //     containerService.setLegalValidationService(
    //         ValidConfig_NationalLegalValidator.get_NationalLegalValidator_FullData());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //     TokenIssuer issuer = InvalidConfig_TokenIssuer.get_NoAdvancedElectronicSystem();
    //
    //     containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //         ContainerToFilesystem.writeFiles("results/SND-AUTH-2_V2", container);
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The TokenIssuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertEquals(e.getMessage(), "the parameter 'token issuer' is not valid");
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
    //
    // /**
    //  * Within this test, a wrong configuration (invalid legal validator) for an authentication-based
    //  * system is meant to be tested.
    //  * <p>
    //  * The respective test is SND_AUTH_2 - Variant 3 - Invalid Legal Validation Result
    //  */
    // @Test
    // public void test_InvalidLegalValidationResult() throws Exception {
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
    //         ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
    //     containerService.setLegalValidationService(
    //         InvalidConfig_NationalLegalValidator.get_NationalLegalValidator_NullResult());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //     TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //     containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //         ContainerToFilesystem.writeFiles("results/SND-AUTH-2_V3", container);
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The TokenIssuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException e) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
    //
    // /**
    //  * Within this test, a wrong configuration (invalid technical validator) for an
    //  * authentication-based system is meant to be tested.
    //  * <p>
    //  * The respective test is SND_AUTH_2 - Variant 4 - Invalid Technical Validation Result
    //  */
    // @Test
    // public void test_InvalidTechnicalValidationResult() throws Exception {
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
    //         InvalidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator_NullResult());
    //     containerService.setLegalValidationService(
    //         ValidConfig_NationalLegalValidator.get_NationalLegalValidator_FullData());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
    //     TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
    //
    //     containerService.setCertificateVerifier(new CommonCertificateVerifier());
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //         ContainerToFilesystem.writeFiles("results/SND-AUTH-2_V4", container);
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The TokenIssuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException e) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
}
