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
 * This class represents a test case for the "InvalidConfig_TokenIssuer_Test" functionality.
 */
// SUB-CONF-16
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair tests")
public class InvalidConfigTokenIssuerTest {
    // /*
    //  * Variant 1 - Authentication Based & Signature Based - No AdvancedElectronicSystem
    //  */
    // @Test
    // public void test_No_System() throws Exception {
    //
    //     final DSSECodexContainerService containerService = new DSSECodexContainerService(
    //         technicalValidationService,
    //         legalValidationService,
    //         signingParameters,
    //         certificateVerifier, connectorCertificatesSource, processExecutor,
    //         asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker
    //     );
    //
    //     containerService.setContainerSignatureParameters(
    //         ValidConfig_SignatureParameters.getJKSConfig_By_SigParamFactory());
    //     containerService.setTechnicalValidationService(
    //         ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //     TokenIssuer issuer = InvalidConfig_TokenIssuer.get_NoAdvancedElectronicSystem();
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The Token Issuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException keyEx) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
    //
    // /*
    //  * Variant 2 - Authentication Based - No Country
    //  */
    // @Test
    // public void test_Auth_No_Country() throws Exception {
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
    //         ValidConfig_SignatureParameters.getJKSConfig_By_SigParamFactory());
    //     containerService.setTechnicalValidationService(
    //         ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //     TokenIssuer issuer = InvalidConfig_TokenIssuer.get_AuthBased_NoCountry();
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The Token Issuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException keyEx) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
    //
    // /*
    //  * Variant 3 - Authentication Based - No Service Provider
    //  */
    // @Test
    // public void test_Auth_No_Provider() throws Exception {
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
    //         ValidConfig_SignatureParameters.getJKSConfig_By_SigParamFactory());
    //     containerService.setTechnicalValidationService(
    //         ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //     TokenIssuer issuer = InvalidConfig_TokenIssuer.get_AuthBased_NoServiceProvider();
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The Token Issuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException keyEx) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
    //
    // /*
    //  * Variant 4 - Authentication Based - Not-ISO-3166 Country
    //  */
    // @Test
    // public void test_Auth_No_ISO_Country() throws Exception {
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
    //         ValidConfig_SignatureParameters.getJKSConfig_By_SigParamFactory());
    //     containerService.setTechnicalValidationService(
    //         ValidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //     TokenIssuer issuer = InvalidConfig_TokenIssuer.get_AuthBased_NoISOCountry();
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The Token Issuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException keyEx) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
    //
    // /*
    //  * Variant 5 - Signature Based - No Country
    //  */
    // @Test
    // public void test_Sig_No_Country() throws Exception {
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
    //         ValidConfig_SignatureParameters.getJKSConfig_By_SigParamFactory());
    //     containerService.setTechnicalValidationService(
    //         ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //     TokenIssuer issuer = InvalidConfig_TokenIssuer.get_SigBased_NoCountry();
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The Token Issuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException keyEx) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
    //
    // /*
    //  * Variant 6 - Signature Based - No Service Provider
    //  */
    // @Test
    // public void test_Sig_No_Provider() throws Exception {
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
    //         ValidConfig_SignatureParameters.getJKSConfig_By_SigParamFactory());
    //     containerService.setTechnicalValidationService(
    //         ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //     TokenIssuer issuer = InvalidConfig_TokenIssuer.get_SigBased_NoServiceProvider();
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The Token Issuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException keyEx) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
    //
    // /*
    //  * Variant 7 - Signature Based - Not-ISO-3166 Country
    //  */
    // @Test
    // public void test_Sig_No_ISO_Country() throws Exception {
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
    //         ValidConfig_SignatureParameters.getJKSConfig_By_SigParamFactory());
    //     containerService.setTechnicalValidationService(
    //         ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
    //     containerService.setLegalValidationService(
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    //
    //     BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
    //     TokenIssuer issuer = InvalidConfig_TokenIssuer.get_SigBased_NoISOCountry();
    //
    //     try {
    //         ECodexContainer container = containerService.create(content, issuer);
    //
    //         CheckResult checkResult = containerService.check(container);
    //
    //         if (checkResult.isSuccessful()) {
    //             Assertions.fail(
    //                 "The Token Issuer was invalid and a valid container has been created! " +
    //                     "The expected result either was an invalid container or an exception at the time of container creation!");
    //         }
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException keyEx) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     }
    // }
}
