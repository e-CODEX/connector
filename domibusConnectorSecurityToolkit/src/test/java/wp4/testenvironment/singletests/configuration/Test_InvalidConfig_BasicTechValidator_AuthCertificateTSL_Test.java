package wp4.testenvironment.singletests.configuration;


import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureCheckers;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import eu.ecodex.dss.util.DocumentStreamUtil;
import eu.ecodex.dss.util.ZipStreamUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import wp4.testenvironment.configurations.*;


// SUB-CONF-21
@Disabled("TODO: repair test") // TODO
class Test_InvalidConfig_BasicTechValidator_AuthCertificateTSL_Test {
    /**
     * Within this test, a invalid configuration of the authentication certificate TSL is simulated.
     * The test contains:
     * - A complete "BusinessContent" object with
     * &nbsp;&nbsp;- A PDF Business Document
     * &nbsp;&nbsp;- A single Attachment
     * &nbsp;&nbsp;- A complete "TokenIssuer" object
     * - No TSL has been configured.
     * - TSL has not been initialized.
     */
    @Test
    void test_Missing_Initialization() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

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

        ECodexContainer container = containerService.create(content);

        // The eCodex container has been created
        Assertions.assertNotNull(container);
        // The ASiC document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
        // The PDF document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
        // The XML document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
        Assertions.assertNotNull(container.getToken());
        // Check Contains Business Data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
        // The Attachments are in place
        Assertions.assertNotNull(container.getBusinessAttachments());
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within the TSL (and no TSL
        // has been defined)
        Assertions.assertEquals(
                container.getToken().getLegalValidationResultTrustLevel(),
                LegalTrustLevel.NOT_SUCCESSFUL
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check against the European
        // TSL still takes place
        Assertions.assertEquals(
                container.getToken().getTechnicalValidationResultTrustLevel(),
                TechnicalTrustLevel.SUCCESSFUL
        );
    }

    /**
     * Within this test, a invalid configuration of the authentication certificate TSL is simulated.
     * The test contains:
     * - A complete "BusinessContent" object with
     * &nbsp;&nbsp;- A PDF Business Document
     * &nbsp;&nbsp;- A single Attachment
     * &nbsp;&nbsp;- A complete "TokenIssuer" object
     * - No TSL has been configured.
     * - TSL has been initialized.
     */
    @Test
    void test_Missing_TSL() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

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

        ECodexContainer container = containerService.create(content);

        // The eCodex container has been created
        Assertions.assertNotNull(container);
        // The ASiC document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
        // The PDF document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
        // The XML document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
        Assertions.assertNotNull(container.getToken());
        // Check Contains Business Data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
        // The Attachments are in place
        Assertions.assertNotNull(container.getBusinessAttachments());
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within the TSL (and no TSL
        // has been defined)
        Assertions.assertEquals(
                container.getToken().getLegalValidationResultTrustLevel(),
                LegalTrustLevel.NOT_SUCCESSFUL
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check against the European
        // TSL still takes place
        Assertions.assertEquals(
                container.getToken().getTechnicalValidationResultTrustLevel(),
                TechnicalTrustLevel.SUCCESSFUL
        );
    }

    /**
     * Within this test, a invalid configuration of the authentication certificate TSL is simulated.
     * The test contains:
     * - A complete "BusinessContent" object with
     * &nbsp;&nbsp;- A PDF Business Document
     * &nbsp;&nbsp;- A single Attachment
     * &nbsp;&nbsp;- A complete "TokenIssuer" object
     * - A random XML has been configured as TSL
     * - TSL has been initialized.
     */
    @Test
    void test_Random_XML_for_TSL() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

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

        ECodexContainer container = containerService.create(content);

        // The eCodex container has been created
        Assertions.assertNotNull(container);
        // The ASiC document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
        // The PDF document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
        // The XML document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
        Assertions.assertNotNull(container.getToken());
        // Check Contains Business Data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
        // The Attachments are in place
        Assertions.assertNotNull(container.getBusinessAttachments());
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within the TSL (and no TSL
        // has been defined)
        Assertions.assertEquals(
                container.getToken().getLegalValidationResultTrustLevel(),
                LegalTrustLevel.NOT_SUCCESSFUL
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check against the European
        // TSL still takes place
        Assertions.assertEquals(
                container.getToken().getTechnicalValidationResultTrustLevel(),
                TechnicalTrustLevel.SUCCESSFUL
        );
    }

    /**
     * Within this test, a invalid configuration of the authentication certificate TSL is simulated.
     * The test contains:
     * - A complete "BusinessContent" object with
     * &nbsp;&nbsp;- A PDF Business Document
     * &nbsp;&nbsp;- A single Attachment
     * &nbsp;&nbsp;- A complete "TokenIssuer" object
     * - A random file has been configured as TSL
     * - TSL has been initialized.
     */
    @Test
    void test_Random_file_for_TSL() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

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
        ECodexContainer container = containerService.create(content);

        // The eCodex container has been created
        Assertions.assertNotNull(container);
        // The ASiC document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
        // The PDF document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
        // The XML document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
        Assertions.assertNotNull(container.getToken());
        // Check Contains Business Data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
        // The Attachments are in place
        Assertions.assertNotNull(container.getBusinessAttachments());
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within the TSL (and no TSL
        // has been defined)
        Assertions.assertEquals(
                container.getToken().getLegalValidationResultTrustLevel(),
                LegalTrustLevel.NOT_SUCCESSFUL
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check against the European
        // TSL still takes place
        Assertions.assertEquals(
                container.getToken().getTechnicalValidationResultTrustLevel(),
                TechnicalTrustLevel.SUCCESSFUL
        );
    }

    /**
     * Within this test, a invalid configuration of the authentication certificate TSL is simulated.
     * The test contains:
     * - A complete "BusinessContent" object with
     * &nbsp;&nbsp;- A PDF Business Document
     * &nbsp;&nbsp;- A single Attachment
     * &nbsp;&nbsp;- A complete "TokenIssuer" object
     * - The file meant to be the TSL does not exist
     * - TSL has been initialized.
     */
    @Test
    void test_Invalid_path_to_TSL() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

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
        ECodexContainer container = containerService.create(content);

        // The eCodex container has been created
        Assertions.assertNotNull(container);
        // The ASiC document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
        // The PDF document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
        // The XML document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
        Assertions.assertNotNull(container.getToken());
        // Check Contains Business Data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
        // The Attachments are in place
        Assertions.assertNotNull(container.getBusinessAttachments());
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within the TSL (and no TSL
        // has been defined)
        Assertions.assertEquals(
                container.getToken().getLegalValidationResultTrustLevel(),
                LegalTrustLevel.NOT_SUCCESSFUL
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check against the European
        // TSL still takes place
        Assertions.assertEquals(
                container.getToken().getTechnicalValidationResultTrustLevel(),
                TechnicalTrustLevel.SUCCESSFUL
        );
    }

    /**
     * Within this test, a invalid configuration of the authentication certificate TSL is simulated.
     * The test contains:
     * - A complete "BusinessContent" object with
     * &nbsp;&nbsp;- A PDF Business Document
     * &nbsp;&nbsp;- A single Attachment
     * &nbsp;&nbsp;- A complete "TokenIssuer" object
     * - A LOTL pointing at a random XML has been configured as TSL
     * - LOTL has been marked as LOTL.
     * - TSL has been initialized.
     */
    @Test
    void test_Random_XML_as_LOTL() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

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
        ECodexContainer container = containerService.create(content);

        // The eCodex container has been created
        Assertions.assertNotNull(container);
        // The ASiC document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
        // The PDF document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
        // The XML document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
        Assertions.assertNotNull(container.getToken());
        // Check Contains Business Data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
        // The Attachments are in place
        Assertions.assertNotNull(container.getBusinessAttachments());
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within the TSL (and no TSL
        // has been defined)
        Assertions.assertEquals(
                container.getToken().getLegalValidationResultTrustLevel(),
                LegalTrustLevel.NOT_SUCCESSFUL
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check against the European
        // TSL still takes place
        Assertions.assertEquals(
                container.getToken().getTechnicalValidationResultTrustLevel(),
                TechnicalTrustLevel.SUCCESSFUL
        );
    }

    /**
     * Within this test, a invalid configuration of the authentication certificate TSL is simulated.
     * The test contains:
     * - A complete "BusinessContent" object with
     * &nbsp;&nbsp;- A PDF Business Document
     * &nbsp;&nbsp;- A single Attachment
     * &nbsp;&nbsp;- A complete "TokenIssuer" object
     * - A LOTL pointing at a random file has been configured as TSL
     * - LOTL has been marked as LOTL.
     * - TSL has been initialized.
     */
    @Test
    void test_Random_file_as_LOTL() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

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
        ECodexContainer container = containerService.create(content);

        // The eCodex container has been created
        Assertions.assertNotNull(container);
        // The ASiC document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
        // The PDF document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
        // The XML document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
        Assertions.assertNotNull(container.getToken());
        // Check Contains Business Data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
        // The Attachments are in place
        Assertions.assertNotNull(container.getBusinessAttachments());
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within the TSL (and no TSL
        // has been defined)
        Assertions.assertEquals(
                container.getToken().getLegalValidationResultTrustLevel(),
                LegalTrustLevel.NOT_SUCCESSFUL
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check against the European
        // TSL still takes place
        Assertions.assertEquals(
                container.getToken().getTechnicalValidationResultTrustLevel(),
                TechnicalTrustLevel.SUCCESSFUL
        );
    }

    /**
     * Within this test, a invalid configuration of the authentication certificate TSL is simulated.
     * The test contains:
     * - A complete "BusinessContent" object with
     * &nbsp;&nbsp;- A PDF Business Document
     * &nbsp;&nbsp;- A single Attachment
     * &nbsp;&nbsp;- A complete "TokenIssuer" object
     * - A valid LOTL has been configured
     * - LOTL has not been marked as LOTL.
     * - TSL has been initialized.
     */
    @Test
    void test_LOTL_is_marked_as_TSL() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

        DSSECodexTechnicalValidationService techValService =
                ValidConfig_BasicTechValidator.get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        //    	techValService.setAuthenticationCertificateTSL(ValidConfig_BasicTechValidator_AuthCertificateTSL
        //    	.get_ByteArray_with_LOTL());
        //    	techValService.initAuthenticationCertificateVerification();

        SignatureCheckers checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        DSSECodexContainerService containerService = new DSSECodexContainerService(
                techValService,
                ValidConfig_BasicLegalValidator.get_LegalValidator(),
                ValidConfig_SignatureParameters.getJKSConfiguration(),
                issuer,
                checkers
        );
        ECodexContainer container = containerService.create(content);

        // The eCodex container has been created
        Assertions.assertNotNull(container);
        // The ASiC document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getAsicDocument()));
        Assertions.assertTrue(ZipStreamUtil.isZipFile(container.getAsicDocument()));
        // The PDF document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenPDF()));
        // The XML document has been created and contains data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getTokenXML()));
        Assertions.assertNotNull(container.getToken());
        // Check Contains Business Data
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessDocument()));
        // The Attachments are in place
        Assertions.assertNotNull(container.getBusinessAttachments());
        Assertions.assertTrue(DocumentStreamUtil.hasData(container.getBusinessAttachments().get(0)));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within the TSL (and no TSL
        // has been defined)
        Assertions.assertEquals(
                container.getToken().getLegalValidationResultTrustLevel(),
                LegalTrustLevel.NOT_SUCCESSFUL
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check against the European
        // TSL still takes place
        Assertions.assertEquals(
                container.getToken().getTechnicalValidationResultTrustLevel(),
                TechnicalTrustLevel.SUCCESSFUL
        );
    }
}
