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
 * This class is a test class for testing functionality related to REC_IND_1_Test.
 */
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair tests")
public class Test_REC_IND_1_Test {
    // private static Resource Path_ValidContainer_ASIC =
    //     new ClassPathResource("/container/ValidContainer.asics");
    // private static Resource Path_AlteredContainer_ASIC =
    //     new ClassPathResource("/container/AlteredContainer.asics");
    // private static Resource Path_ValidContainer_XML =
    //     new ClassPathResource("/container/ValidContainer.xml");
    // private static Resource Path_Random_XML = new ClassPathResource("/container/Random.xml");
    // private static Resource Path_RandomContainer_XML =
    //     new ClassPathResource("/container/RandomContainer.xml");
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
    //         ValidConfig_BasicLegalValidator.get_LegalValidator());
    //     containerService.setCertificateVerifier(ValidConfig_CertificateVerifier.get_WithProxy());
    // }
    //
    // /*
    //  * The respective test is REC-IND-1 - Variant 1 - Valid Container and respective XML
    //  */
    // @Test
    // @Disabled("TODO: repair test") // TODO
    // public void test_Valid_with_Respective_XML() throws Exception {
    //
    //     InputStream aSiCStream = Path_ValidContainer_ASIC.getInputStream();
    //     InputStream xMLStream = Path_ValidContainer_XML.getInputStream();
    //
    //     ECodexContainer container = containerService.receive(aSiCStream, xMLStream);
    //
    //     aSiCStream.close();
    //     xMLStream.close();
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
    //     // The Attachments are in place
    //     Assertions.assertNotNull(container.getBusinessAttachments());
    //
    //     Assertions.assertEquals(
    //         LegalTrustLevel.SUCCESSFUL,
    //         container.getToken().getLegalValidationResult().getTrustLevel()
    //     );
    //     Assertions.assertEquals(
    //         TechnicalTrustLevel.SUCCESSFUL,
    //         container.getToken().getTechnicalValidationResult().getTrustLevel()
    //     );
    //     Assertions.assertEquals(
    //         AdvancedSystemType.SIGNATURE_BASED, container.getToken().getAdvancedElectronicSystem());
    //
    //     CheckResult checkResult = containerService.check(container);
    //
    //     // List<CheckProblem> problems = checkResult.getProblems();
    //     // for (CheckProblem checkProblem : problems) {
    //     //     System.out.println(checkProblem.getMessage());
    //     // }
    //
    //     Assertions.assertTrue(checkResult.isSuccessful());
    //
    //     ContainerToFilesystem.writeFiles("results/REC-IND-1_V1", container);
    // }
    //
    // /*
    //  * The respective test is REC-IND-1 - Variant 2 - Valid Container with random XML
    //  */
    // @Test
    // public void test_Valid_with_Random_XML() throws Exception {
    //
    //     InputStream aSiCStream = Path_ValidContainer_ASIC.getInputStream();
    //     InputStream xMLStream = Path_Random_XML.getInputStream();
    //
    //     try {
    //         @SuppressWarnings("unused")
    //         ECodexContainer container = containerService.receive(aSiCStream, xMLStream);
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException e) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     } finally {
    //         IOUtils.closeQuietly(aSiCStream);
    //         IOUtils.closeQuietly(xMLStream);
    //     }
    // }
    //
    // /*
    //  * The respective test is REC-IND-1 - Variant 3 - Invalid Container with valid XML
    //  */
    // @Test
    // public void test_Invalid_with_Valid_XML() throws Exception {
    //
    //     InputStream aSiCStream = Path_Random_XML.getInputStream();
    //     InputStream xMLStream = Path_ValidContainer_XML.getInputStream();
    //
    //     try {
    //         @SuppressWarnings("unused")
    //         ECodexContainer container = containerService.receive(aSiCStream, xMLStream);
    //     } catch (eu.ecodex.dss.service.ECodexBusinessException e) {
    //         Assertions.assertTrue(true);
    //     } catch (eu.ecodex.dss.service.ECodexException e) {
    //         Assertions.assertTrue(true);
    //     } catch (Exception ex) {
    //         Assertions.fail("An unexpected exception has been thrown: " + ex.getMessage(), ex);
    //     } finally {
    //         IOUtils.closeQuietly(aSiCStream);
    //         IOUtils.closeQuietly(xMLStream);
    //     }
    // }
    //
    // /*
    //  * The respective test is REC-IND-1 - Variant 4 - Altered Container with valid XML
    //  */
    // @Test
    // public void test_Altered_with_Valid_XML() throws Exception {
    //
    //     InputStream aSiCStream = Path_AlteredContainer_ASIC.getInputStream();
    //     InputStream xMLStream = Path_ValidContainer_XML.getInputStream();
    //
    //     ECodexContainer container = containerService.receive(aSiCStream, xMLStream);
    //
    //     aSiCStream.close();
    //     xMLStream.close();
    //
    //     // The eCodex container has been created
    //     Assertions.assertNotNull(container);
    //
    //     CheckResult checkResult = containerService.check(container);
    //
    //     Assertions.assertTrue(checkResult.isFatal());
    // }
    //
    // /*
    //  * The respective test is REC-IND-1 - Variant 5 - Valid Container and valid XML of another container
    //  */
    // @Test
    // public void test_Invalid_with_Random_XML_Token() throws Exception {
    //
    //     InputStream aSiCStream = Path_ValidContainer_ASIC.getInputStream();
    //     InputStream xMLStream = Path_RandomContainer_XML.getInputStream();
    //
    //     ECodexContainer container = containerService.receive(aSiCStream, xMLStream);
    //
    //     aSiCStream.close();
    //     xMLStream.close();
    //
    //     // The eCodex container has been created
    //     Assertions.assertNotNull(container);
    //
    //     CheckResult checkResult = containerService.check(container);
    //
    //     Assertions.assertTrue(checkResult.isFatal());
    // }
    //
    // private static InputStream getInputStream(String path) {
    //
    //     FileInputStream fis = null;
    //     File f = null;
    //
    //     try {
    //         f = new File(path);
    //
    //         fis = new FileInputStream(f);
    //
    //         return fis;
    //     } catch (Exception e) {
    //         System.err.println("Exception within the files used for test REC-IND-1");
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
}
