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

import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_BasicLegalValidator;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_BasicTechValidator;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_BusinessContent;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_SignatureCheckers;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_SignatureParameters;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_TokenIssuer;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.util.DocumentStreamUtil;
import eu.ecodex.dss.util.ZipStreamUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

// SUB-CONF-21
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:PackageName"})
@Disabled("TODO: repair test")
class InvalidConfigBasicTechValidatorAuthCertificateTSLTest {
    /**
     * Within this test, if an invalid configuration of the authentication certificate TSL is
     * simulated. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
     * Business Document &nbsp;&nbsp;- A single Attachment &nbsp;&nbsp;- A complete "TokenIssuer"
     * object - No TSL has been configured. - TSL has not been initialized.
     */
    @Test
    void test_Missing_Initialization() throws Exception {
        var content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

        var techValService = ValidConfig_BasicTechValidator
            .get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        var containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );

        var container = containerService.create(content);

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
        Assertions.assertTrue(
            DocumentStreamUtil.hasData(container.getBusinessAttachments().getFirst()));

        var checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within
        // the TSL (and no TSL has been defined)
        Assertions.assertEquals(
            LegalTrustLevel.NOT_SUCCESSFUL,
            container.getToken().getLegalValidationResultTrustLevel()
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check
        // against the European TSL still takes place
        Assertions.assertEquals(
            TechnicalTrustLevel.SUCCESSFUL,
            container.getToken().getTechnicalValidationResultTrustLevel()
        );
    }

    /**
     * Within this test, if an invalid configuration of the authentication certificate TSL is
     * simulated. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
     * Business Document &nbsp;&nbsp;- A single Attachment &nbsp;&nbsp;- A complete "TokenIssuer"
     * object - No TSL has been configured. - TSL has been initialized.
     */
    @Test
    void test_Missing_TSL() throws Exception {
        var content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

        var techValService = ValidConfig_BasicTechValidator
            .get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        var containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );

        var container = containerService.create(content);

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
        Assertions.assertTrue(
            DocumentStreamUtil.hasData(container.getBusinessAttachments().getFirst()));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within
        // the TSL (and no TSL has been defined)
        Assertions.assertEquals(
            LegalTrustLevel.NOT_SUCCESSFUL,
            container.getToken().getLegalValidationResultTrustLevel()
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check
        // against the European TSL still takes place
        Assertions.assertEquals(
            TechnicalTrustLevel.SUCCESSFUL,
            container.getToken().getTechnicalValidationResultTrustLevel()
        );
    }

    /**
     * Within this test, if an invalid configuration of the authentication certificate TSL is
     * simulated. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
     * Business Document &nbsp;&nbsp;- A single Attachment &nbsp;&nbsp;- A complete "TokenIssuer"
     * object - A random XML has been configured as TSL - TSL has been initialized.
     */
    @Test
    void test_Random_XML_for_TSL() throws Exception {
        var content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

        var techValService = ValidConfig_BasicTechValidator
            .get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        var containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );

        var container = containerService.create(content);

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
        Assertions.assertTrue(
            DocumentStreamUtil.hasData(container.getBusinessAttachments().getFirst()));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within
        // the TSL (and no TSL has been defined)
        Assertions.assertEquals(
            LegalTrustLevel.NOT_SUCCESSFUL,
            container.getToken().getLegalValidationResultTrustLevel()
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check
        // against the European TSL still takes place
        Assertions.assertEquals(
            TechnicalTrustLevel.SUCCESSFUL,
            container.getToken().getTechnicalValidationResultTrustLevel()
        );
    }

    /**
     * Within this test, if an invalid configuration of the authentication certificate TSL is
     * simulated. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
     * Business Document &nbsp;&nbsp;- A single Attachment &nbsp;&nbsp;- A complete "TokenIssuer"
     * object - A random file has been configured as TSL - TSL has been initialized.
     */
    @Test
    void test_Random_file_for_TSL() throws Exception {
        var content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

        var techValService = ValidConfig_BasicTechValidator
            .get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        var containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );
        var container = containerService.create(content);

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
        Assertions.assertTrue(
            DocumentStreamUtil.hasData(container.getBusinessAttachments().getFirst()));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within
        // the TSL (and no TSL has been defined)
        Assertions.assertEquals(
            LegalTrustLevel.NOT_SUCCESSFUL,
            container.getToken().getLegalValidationResultTrustLevel()
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check
        // against the European TSL still takes place
        Assertions.assertEquals(
            TechnicalTrustLevel.SUCCESSFUL,
            container.getToken().getTechnicalValidationResultTrustLevel()
        );
    }

    /**
     * Within this test, if an invalid configuration of the authentication certificate TSL is
     * simulated. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
     * Business Document &nbsp;&nbsp;- A single Attachment &nbsp;&nbsp;- A complete "TokenIssuer"
     * object - The file meant to be the TSL does not exist - TSL has been initialized.
     */
    @Test
    void test_Invalid_path_to_TSL() throws Exception {
        var content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

        var techValService = ValidConfig_BasicTechValidator
            .get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        var containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );
        var container = containerService.create(content);

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
        Assertions.assertTrue(
            DocumentStreamUtil.hasData(container.getBusinessAttachments().getFirst()));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within
        // the TSL (and no TSL has been defined)
        Assertions.assertEquals(
            LegalTrustLevel.NOT_SUCCESSFUL,
            container.getToken().getLegalValidationResultTrustLevel()
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check
        // against the European TSL still takes place
        Assertions.assertEquals(
            TechnicalTrustLevel.SUCCESSFUL,
            container.getToken().getTechnicalValidationResultTrustLevel()
        );
    }

    /**
     * Within this test, if an invalid configuration of the authentication certificate TSL is
     * simulated. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
     * Business Document &nbsp;&nbsp;- A single Attachment &nbsp;&nbsp;- A complete "TokenIssuer"
     * object - A LOTL pointing at a random XML has been configured as TSL - LOTL has been marked as
     * LOTL. - TSL has been initialized.
     */
    @Test
    void test_Random_XML_as_LOTL() throws Exception {
        var content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

        var techValService = ValidConfig_BasicTechValidator
            .get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        var containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );
        var container = containerService.create(content);

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
        Assertions.assertTrue(
            DocumentStreamUtil.hasData(container.getBusinessAttachments().getFirst()));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within
        // the TSL (and no TSL has been defined)
        Assertions.assertEquals(
            LegalTrustLevel.NOT_SUCCESSFUL,
            container.getToken().getLegalValidationResultTrustLevel()
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check
        // against the European TSL still takes place
        Assertions.assertEquals(
            TechnicalTrustLevel.SUCCESSFUL,
            container.getToken().getTechnicalValidationResultTrustLevel()
        );
    }

    /**
     * Within this test, if an invalid configuration of the authentication certificate TSL is
     * simulated. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
     * Business Document &nbsp;&nbsp;- A single Attachment &nbsp;&nbsp;- A complete "TokenIssuer"
     * object - A LOTL pointing at a random file has been configured as TSL - LOTL has been marked
     * as LOTL. - TSL has been initialized.
     */
    @Test
    void test_Random_file_as_LOTL() throws Exception {
        var content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

        var techValService = ValidConfig_BasicTechValidator
            .get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        var containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );
        var container = containerService.create(content);

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
        Assertions.assertTrue(
            DocumentStreamUtil.hasData(container.getBusinessAttachments().getFirst()));

        CheckResult checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present within
        // the TSL (and no TSL has been defined)
        Assertions.assertEquals(
            LegalTrustLevel.NOT_SUCCESSFUL,
            container.getToken().getLegalValidationResultTrustLevel()
        );

        // Result of the technical validation of the token must be SUCCESSFUL the usual check
        // against the European TSL still takes place
        Assertions.assertEquals(
            TechnicalTrustLevel.SUCCESSFUL,
            container.getToken().getTechnicalValidationResultTrustLevel()
        );
    }

    /**
     * Within this test, if an invalid configuration of the authentication certificate TSL is
     * simulated. The test contains: - A complete "BusinessContent" object with &nbsp;&nbsp;- A PDF
     * Business Document &nbsp;&nbsp;- A single Attachment &nbsp;&nbsp;- A complete "TokenIssuer"
     * object - A valid LOTL has been configured - LOTL has not been marked as LOTL. - TSL has been
     * initialized.
     */
    @Test
    void test_LOTL_is_marked_as_TSL() throws Exception {
        var content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();
        var issuer = ValidConfig_TokenIssuer.get_FullAuthenticationBased();

        var techValService = ValidConfig_BasicTechValidator
            .get_BasicTechValidator_NoProxy_NoAuthCertConfig();

        var checkers = ValidConfig_SignatureCheckers.getSignatureCheckers();

        var containerService = new DSSECodexContainerService(
            techValService,
            ValidConfig_BasicLegalValidator.get_LegalValidator(),
            ValidConfig_SignatureParameters.getJKSConfiguration(),
            issuer,
            checkers
        );
        var container = containerService.create(content);

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
        Assertions.assertTrue(
            DocumentStreamUtil.hasData(container.getBusinessAttachments().getFirst()));

        var checkResult = containerService.check(container);

        Assertions.assertTrue(checkResult.isSuccessful());

        // Result of the token must be NOT_SUCCESSFUL as the certificate has to be present
        // within the TSL (and no TSL has been defined)
        Assertions.assertEquals(
            LegalTrustLevel.NOT_SUCCESSFUL,
            container.getToken().getLegalValidationResultTrustLevel()
        );

        Assertions.assertEquals(
            TechnicalTrustLevel.SUCCESSFUL,
            container.getToken().getTechnicalValidationResultTrustLevel()
        );
    }
}
