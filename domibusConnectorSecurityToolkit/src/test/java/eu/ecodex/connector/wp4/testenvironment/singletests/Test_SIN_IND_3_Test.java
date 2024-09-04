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
 * Add Signature to Container.
 */
// TODO Repair the tests
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:LineLength", "squid:S1135"})
@Disabled("Repair tests")
public class Test_SIN_IND_3_Test {
    // private static String Path_ValidContainer_ASIC =
    //     "src/test/resources/container/ValidContainer.asics";
    // private static String Path_ValidContainer_XML =
    //     "src/test/resources/container/ValidContainer.xml";
    // static DSSECodexContainerService containerService;
    // static ECodexContainer container;
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
    //
    //     InputStream aSiCStream = getInputStream(Path_ValidContainer_ASIC);
    //     InputStream xMLStream = getInputStream(Path_ValidContainer_XML);
    //
    //     try {
    //         container = containerService.receive(aSiCStream, xMLStream);
    //     } catch (ECodexException e) {
    //         System.err.println("Exception while reading the test-container for Test_SIN_IND_2");
    //         e.printStackTrace();
    //     } finally {
    //         IOUtils.closeQuietly(aSiCStream);
    //         IOUtils.closeQuietly(xMLStream);
    //     }
    // }
    //
    // /*
    //  * Variant 1 (Checks taken from official Lib-Tests)
    //  */
    // @Test
    // @Disabled("TODO: repair test") // TODO
    // public void test_Add_Signature() throws Exception {
    //
    //     // The eCodex container has been created
    //     Assertions.assertNotNull(container);
    //
    //     ECodexContainer container2 = containerService.addSignature(container);
    //
    //     Assertions.assertNotNull(container2);
    //     Assertions.assertNotNull(container2.getAsicDocument());
    //
    //     // check for a new instance
    //     Assertions.assertTrue(container != container2);
    //
    //     // check for the _same_ attributes
    //     Assertions.assertTrue(container.getBusinessDocument() == container2.getBusinessDocument());
    //     Assertions.assertTrue(container.getTokenPDF() == container2.getTokenPDF());
    //     Assertions.assertTrue(container.getTokenXML() == container2.getTokenXML());
    //     Assertions.assertTrue(container.getToken() == container2.getToken());
    //
    //     // check that the asic docs are not the same
    //     Assertions.assertTrue(container.getAsicDocument() != container2.getAsicDocument());
    //     // even not on binary level
    //     Assertions.assertTrue(
    //         !binaryEquals(container.getAsicDocument(), container2.getAsicDocument()));
    //
    //     // extract the signatures and check that they differ
    //     final DSSDocument signatures1 = ZipStreamUtil.extract(container.getAsicDocument(),
    //                                                           ContainerFileDefinitions.SIGNATURES_REF
    //     );
    //     Assertions.assertNotNull(signatures1);
    //     final DSSDocument signatures2 = ZipStreamUtil.extract(container2.getAsicDocument(),
    //                                                           ContainerFileDefinitions.SIGNATURES_REF
    //     );
    //     Assertions.assertNotNull(signatures2);
    //
    //     Assertions.assertTrue(!binaryEquals(signatures1, signatures2));
    //
    //     ContainerToFilesystem.writeFiles("results/SIN_IND_3", container2);
    //
    //     CheckResult checkResult = containerService.check(container2);
    //
    //     Assertions.assertTrue(checkResult.isSuccessful());
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
    //         System.err.println("Exception within the files used for test Test_SIN_IND_3");
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
    //
    // static boolean binaryEquals(final DSSDocument doc1, final DSSDocument doc2) {
    //     try {
    //         final InputStream in1 = doc1.openStream();
    //         final InputStream in2 = doc2.openStream();
    //         final ByteArrayOutputStream bos1 = new ByteArrayOutputStream(16 * 1024);
    //         final ByteArrayOutputStream bos2 = new ByteArrayOutputStream(16 * 1024);
    //         if (in1 != null) {
    //             IOUtils.copy(in1, bos1);
    //         }
    //         if (in2 != null) {
    //             IOUtils.copy(in2, bos2);
    //         }
    //         final byte[] bytes1 = bos1.toByteArray();
    //         final byte[] bytes2 = bos2.toByteArray();
    //         return Arrays.equals(bytes1, bytes2);
    //     } catch (IOException e) {
    //         throw new RuntimeException(e);
    //     }
    // }
}
