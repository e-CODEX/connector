package wp4.testenvironment.singletests.configuration;


import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureCheckers;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wp4.testenvironment.configurations.*;


// SUB-CONF-6
class Test_InvalidConfig_AuthBasedNationalTechValidator_Test {
    /**
     * The respective test is SUB-CONF-6 - Variant 1 No Result
     */
    @Test
    void test_NoResult() throws Exception {
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        final DSSECodexContainerService containerService =
                new DSSECodexContainerService(
                        InvalidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator_NullResult(),
                        ValidConfig_NationalLegalValidator.get_NationalLegalValidator_NoDisclaimer(),
                        ValidConfig_SignatureParameters.getJKSConfiguration(),
                        issuer,
                        checkers
                );

        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();

        try {
            ECodexContainer container = containerService.create(content);

            CheckResult checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail("The technical validator was invalid and a valid container has been created! " +
                                        "The expected result either was an invalid container or an exception at the " +
                                        "time of container creation!");
            }
        } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
            Assertions.assertTrue(true);
        } catch (ECodexException el) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }

    /**
     * The respective test is SUB-CONF-6 - Variant 2 Empty Result
     */
    @Test
    void test_EmptyResult() throws Exception {
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        final DSSECodexContainerService containerService =
                new DSSECodexContainerService(
                        InvalidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator_EmptyResult(),
                        ValidConfig_NationalLegalValidator.get_NationalLegalValidator_NoDisclaimer(),
                        ValidConfig_SignatureParameters.getJKSConfiguration(),
                        issuer,
                        checkers
                );

        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();

        try {
            ECodexContainer container = containerService.create(content);

            CheckResult checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail("The technical validator was invalid and a valid container has been created! " +
                                        "The expected result either was an invalid container or an exception at the " +
                                        "time of container creation!");
            }
        } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
            Assertions.assertTrue(true);
        } catch (ECodexException el) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }

    /**
     * The respective test is SUB-CONF-6 - Variant 3 Invalid Result
     */
    @Test
    void test_InvalidResult() throws Exception {
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();
        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        final DSSECodexContainerService containerService = new DSSECodexContainerService(
                InvalidConfig_AuthBasedNationalTechValidator.get_AuthBasedNationalTechValidator_InvalidResult(),
                ValidConfig_NationalLegalValidator.get_NationalLegalValidator_NoDisclaimer(),
                ValidConfig_SignatureParameters.getJKSConfiguration(),
                issuer,
                checkers
        );

        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();

        try {
            ECodexContainer container = containerService.create(content);

            CheckResult checkResult = containerService.check(container);

            if (checkResult.isSuccessful()) {
                Assertions.fail("The technical validator was invalid and a valid container has been created! " +
                                        "The expected result either was an invalid container or an exception at the " +
                                        "time of container creation!");
            }
        } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
            Assertions.assertTrue(true);
        } catch (ECodexException el) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }
}
