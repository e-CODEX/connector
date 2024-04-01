package wp4.testenvironment.singletests.configuration;


import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureCheckers;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wp4.testenvironment.configurations.*;


// SUB-CONF-14
class Test_InvalidConfig_BusinessContent_Test {

    /*
     * Variant 1 - Empty Business Content
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
                Assertions.fail("The Business Content was invalid and a valid container has been created! " +
                                        "The expected result either was an invalid container or an exception at the " +
                                        "time of container creation!");
            }
        } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
            Assertions.assertTrue(true);
        } catch (eu.ecodex.dss.service.ECodexException keyEx) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }
    /*
     * Variant 2 - Business Content with missing Business Document
     */
    @Test
    void test_No_Business_Document() throws Exception {
        BusinessContent content = InvalidConfig_BusinessContent.get_MissingBusinessDocument();

        //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
        //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
        //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
        //    	pdfTokenSignatureChecker);
        //
        //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
        //    	.getJKSConfig_By_SigParamFactory());
        //    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator
        //    	.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
        //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
        //    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());

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
                Assertions.fail("The Business Content was invalid and a valid container has been created! " +
                                        "The expected result either was an invalid container or an exception at the " +
                                        "time of container creation!");
            }
        } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
            Assertions.assertTrue(true);
        } catch (eu.ecodex.dss.service.ECodexException keyEx) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }

    /*
     * Variant 3 - Null Business Content
     */
    @Test
    public void test_Null_Business_Content() throws Exception {

        BusinessContent content = InvalidConfig_BusinessContent.get_NullContent();

        //    	final DSSECodexContainerService containerService = new DSSECodexContainerService
        //    	(technicalValidationService, legalValidationService, signingParameters, certificateVerifier,
        //    	connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker,
        //    	pdfTokenSignatureChecker);

        //    	containerService.setContainerSignatureParameters(ValidConfig_SignatureParameters
        //    	.getJKSConfig_By_SigParamFactory());
        //    	containerService.setTechnicalValidationService(ValidConfig_BasicTechValidator
        //    	.get_BasicTechValidator_NoProxy_NoAuthCertConfig());
        //    	containerService.setLegalValidationService(ValidConfig_BasicLegalValidator.get_LegalValidator());
        //    	containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());

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
                Assertions.fail("The Business Content was invalid and a valid container has been created! " +
                                        "The expected result either was an invalid container or an exception at the " +
                                        "time of container creation!");
            }
        } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
            Assertions.assertTrue(true);
        } catch (eu.ecodex.dss.service.ECodexException keyEx) {
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
        }
    }
}
