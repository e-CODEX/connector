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

import eu.ecodex.connector.wp4.testenvironment.configurations.InvalidConfig_SigBasedNationalTechValidator;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_BusinessContent;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_NationalLegalValidator;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_SignatureCheckers;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_SignatureParameters;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_TokenIssuer;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureCheckers;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This class represents a test class for the {@code SigBasedNationalTechValidator} in case of
 * invalid configuration. It contains tests for different variants of invalid results.
 */
// SUB-CONF-7
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength"})
class InvalidConfigSigBasedNationalTechValidatorTest {
    // /**
    //  * Initializes all test cases with the same, working configuration.
    //  * Test case specific configurations are done within each test case itself.
    //  */
    // @BeforeAll
    // static public void init() {
    //      containerService = new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
    //
    //      containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters.getJKSConfiguration());
    //      containerService.setTechnicalValidationService(ValidConfig_SigBasedNationalTechValidator.get_SigBasedNationalTechValidator());
    //      containerService.setLegalValidationService(ValidConfig_NationalLegalValidator.get_NationalLegalValidator_FullData());
    // }

    /**
     * The respective test is SUB-CONF-7 - Variant 1 No Result.
     */
    @Test
    void test_NoResult() {
        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();

        final var containerService = new DSSECodexContainerService(
            InvalidConfig_SigBasedNationalTechValidator
                .get_SigBasedNationalTechValidator_NullResult(),
            ValidConfig_NationalLegalValidator.get_NationalLegalValidator_NoDisclaimer(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );

        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();

        try {
            ECodexContainer container = containerService.create(content);

            CheckResult checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail(
                    "The technical validator was invalid and a valid container has been created! "
                        + "The expected result either was an invalid container or an exception at "
                        + "the time of container creation!"
                );
            }
        } catch (ECodexException el) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }

    /**
     * The respective test is SUB-CONF-7 - Variant 2 Empty Result.
     */
    @Test
    void test_EmptyResult() {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();

        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();

        final DSSECodexContainerService containerService =
            new DSSECodexContainerService(
                InvalidConfig_SigBasedNationalTechValidator
                    .get_SigBasedNationalTechValidator_EmptyResult(),
                ValidConfig_NationalLegalValidator.get_NationalLegalValidator_NoDisclaimer(),
                ValidConfig_SignatureParameters.getJKSConfiguration(),
                issuer,
                checkers
            );

        try {
            ECodexContainer container = containerService.create(content);

            CheckResult checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail(
                    "The technical validator was invalid and a valid container has been created! "
                        + "The expected result either was an invalid container or an exception at "
                        + "the time of container creation!"
                );
            }
        } catch (ECodexException el) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }

    /**
     * The respective test is SUB-CONF-7 - Variant 3 Invalid Result.
     */
    @Test
    void test_InvalidResult() {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();

        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        final DSSECodexContainerService containerService =
            new DSSECodexContainerService(
                InvalidConfig_SigBasedNationalTechValidator
                    .get_SigBasedNationalTechValidator_InvalidResult(),
                ValidConfig_NationalLegalValidator.get_NationalLegalValidator_NoDisclaimer(),
                ValidConfig_SignatureParameters.getJKSConfiguration(),
                issuer,
                checkers
            );

        try {
            ECodexContainer container = containerService.create(content);

            CheckResult checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail(
                    "The technical validator was invalid and a valid container has been created! "
                        + "The expected result either was an invalid container or an exception "
                        + "at the time of container creation!"
                );
            }
        } catch (ECodexException el) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }
}
