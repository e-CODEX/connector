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

import eu.ecodex.connector.wp4.testenvironment.configurations.InvalidConfig_BusinessContent;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_BasicLegalValidator;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_BasicTechValidator;
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
import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// SUB-CONF-14
@SuppressWarnings("checkstyle:TypeName")
class InvalidConfigBusinessContentTest {
    /**
     * Variant 1 - Empty Business Content.
     */
    @Test
    void test_Empty_Business_Content() throws Exception {
        BusinessContent content = InvalidConfig_BusinessContent.get_EmptyContent();

        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();

        DSSECodexTechnicalValidationService techValService =
            ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        DSSECodexContainerService containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );

        try {
            ECodexContainer container = containerService.create(content);

            CheckResult checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail(
                    "The Business Content was invalid and a valid container has been created! "
                        + "The expected result either was an invalid container or an exception at "
                        + "the time of container creation!"
                );
            }
        } catch (ECodexException keyEx) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }

    /**
     * Variant 2 - Business Content with missing Business Document.
     */
    @Test
    void test_No_Business_Document() throws Exception {
        BusinessContent content = InvalidConfig_BusinessContent.get_MissingBusinessDocument();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();

        DSSECodexTechnicalValidationService techValService =
            ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        DSSECodexContainerService containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfig_By_SigParamFactory(),
            issuer,
            checkers
        );

        try {
            ECodexContainer container = containerService.create(content);

            CheckResult checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail(
                    "The Business Content was invalid and a valid container has been created! "
                        + "The expected result either was an invalid container or an exception at "
                        + "the time of container creation!"
                );
            }
        } catch (ECodexException keyEx) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }

    /**
     * Variant 3 - Null Business Content.
     */
    @Test
    void test_Null_Business_Content() throws Exception {
        BusinessContent content = InvalidConfig_BusinessContent.get_NullContent();

        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        DSSECodexContainerService containerService = new DSSECodexContainerService(
            ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig(),
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfig_By_SigParamFactory(),
            issuer,
            checkers
        );

        try {
            ECodexContainer container = containerService.create(content);

            CheckResult checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail(
                    "The Business Content was invalid and a valid container has been created! "
                        + "The expected result either was an invalid container or an exception at "
                        + "the time of container creation!"
                );
            }
        } catch (ECodexException keyEx) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }
}
