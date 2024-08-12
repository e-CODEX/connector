/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.singletests;

import org.junit.jupiter.api.Disabled;

/**
 * The Test_SIN_IND_1_Test class is a container used for checking purposes.
 */
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair tests")
public class Test_SIN_IND_1_Test {
    // private static String Path_ValidContainer_ASIC = "/container/ValidContainer.asics";
    // private static String Path_ValidContainer_XML = "/container/ValidContainer.xml";
    // private static String Path_AlteredContainer_ASIC = "/container/AlteredContainer.asics";
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
    //  * Variant 1 Check Valid Container
    //  */
    // @Test
    // @Disabled("TODO: repair test")
    // public void test_Valid_with_Respective_XML() throws Exception {
    //
    //     InputStream aSiCStream = getInputStream(Path_ValidContainer_ASIC);
    //     InputStream xMLStream = getInputStream(Path_ValidContainer_XML);
    //
    //     ECodexContainer container = containerService.receive(aSiCStream, xMLStream);
    //
    //     aSiCStream.close();
    //     xMLStream.close();
    //
    //     CheckResult checkResult = containerService.check(container);
    //
    //     assertThat(checkResult.getProblems()).isEmpty();
    //
    //     Assertions.assertTrue(checkResult.isSuccessful());
    //     Assertions.assertTrue(!checkResult.isFatal());
    //     Assertions.assertTrue(!checkResult.isProblematic());
    // }
    //
    // /*
    //  * Variant 2 Check Invalid Container
    //  */
    // @Test
    // public void test_Invalid_with_Respective_XML() throws Exception {
    //
    //     InputStream aSiCStream = getInputStream(Path_AlteredContainer_ASIC);
    //     InputStream xMLStream = getInputStream(Path_ValidContainer_XML);
    //
    //     ECodexContainer container = containerService.receive(aSiCStream, xMLStream);
    //
    //     aSiCStream.close();
    //     xMLStream.close();
    //
    //     CheckResult checkResult = containerService.check(container);
    //
    //     Assertions.assertTrue(!checkResult.getProblems().isEmpty());
    //
    //     Assertions.assertTrue(!checkResult.isSuccessful());
    //     Assertions.assertTrue(checkResult.isFatal());
    //     Assertions.assertTrue(checkResult.isProblematic());
    //
    //     CheckProblem curProblem = checkResult.getProblems().get(0);
    //
    //     Assertions.assertNotSame(curProblem.getMessage(), null);
    //     Assertions.assertNotSame(curProblem.getMessage(), "");
    //
    //     Assertions.assertTrue(curProblem.isFatal());
    // }
    //
    // private static InputStream getInputStream(String path) {
    //
    //     InputStream fis = null;
    //     Resource f = null;
    //
    //     try {
    //         f = new ClassPathResource(path);
    //
    //         if (!f.exists()) {
    //             throw new IllegalArgumentException("Resource " + path + " does not exist!");
    //         }
    //
    //         fis = f.getInputStream();
    //         assertThat(fis).isNotNull();
    //
    //         return fis;
    //     } catch (Exception e) {
    //         System.err.println("Exception within the files used for test Test_SIN_IND_1");
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
}
