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

import eu.ecodex.connector.wp4.testenvironment.configurations.InvalidConfig_AuthBasedNationalTechValidator;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_BusinessContent;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_NationalLegalValidator;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_SignatureCheckers;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_SignatureParameters;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_TokenIssuer;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// SUB-CONF-6
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:PackageName"})
class InvalidConfigAuthBasedNationalTechValidatorTest {
    /**
     * The respective test is SUB-CONF-6 - Variant 1 No Result.
     */
    @Test
    void test_NoResult() {
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        final var containerService = new DSSECodexContainerService(
            InvalidConfig_AuthBasedNationalTechValidator
                .get_AuthBasedNationalTechValidator_NullResult(),
            ValidConfig_NationalLegalValidator.get_NationalLegalValidator_NoDisclaimer(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );

        var content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();

        try {
            var container = containerService.create(content);
            var checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail(
                    "The technical validator was invalid and a valid container has been created! "
                        + "The expected result either was an invalid container or an exception at "
                        + "the time of container creation!");
            }
        } catch (ECodexException el) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }

    /**
     * The respective test is SUB-CONF-6 - Variant 2 Empty Result.
     */
    @Test
    void test_EmptyResult() {
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        final var containerService = new DSSECodexContainerService(
            InvalidConfig_AuthBasedNationalTechValidator
                .get_AuthBasedNationalTechValidator_EmptyResult(),
            ValidConfig_NationalLegalValidator.get_NationalLegalValidator_NoDisclaimer(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );
        var content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
        try {
            var container = containerService.create(content);
            var checkResult = containerService.check(container);
            if (checkResult.isSuccessful()) {
                Assertions.fail(
                    "The technical validator was invalid and a valid container has been created! "
                        + "The expected result either was an invalid container or an exception at "
                        + "the time of container creation!");
            }
        } catch (ECodexException el) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }

    /**
     * The respective test is SUB-CONF-6 - Variant 3 Invalid Result.
     */
    @Test
    void test_InvalidResult() {
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        final var containerService = new DSSECodexContainerService(
            InvalidConfig_AuthBasedNationalTechValidator
                .get_AuthBasedNationalTechValidator_InvalidResult(),
            ValidConfig_NationalLegalValidator.get_NationalLegalValidator_NoDisclaimer(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );

        var content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();

        try {
            var container = containerService.create(content);
            var checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail(
                    "The technical validator was invalid and a valid container has been created! "
                        + "The expected result either was an invalid container or an exception at "
                        + "the time of container creation!");
            }
        } catch (ECodexException el) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }
}
