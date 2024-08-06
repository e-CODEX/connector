package eu.ecodex.evidences;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.ecodex.evidences.types.ECodexMessageDetails;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail.PostalAdress;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail.Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.etsi.uri._02640.v2.EventReasonType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The class <code>ECodexEvidenceBuilderTest</code> contains tests for the class
 * <code>{@link ECodexEvidenceBuilder}</code>.
 *
 * @author cheny01
 * @version $Revision: 1.0 $
 */
public class ECodexEvidenceBuilderTest {
    private static final String PATH_OUTPUT_FILES = "target/test/ECodexEvidenceBuilderTest/";
    private static final String DELIVERY_UNKNOWN_ADDRESS_FILE =
        "outputfileDeliveryNonDeliveryToRecipientUnknownAdress.xml";
    private static final String DELIVERY_NO_REASON_FILE =
        "outputfileDeliveryNonDeliveryToRecipientNoreason.xml";
    private static final String RETRIEVAL_UNKNOWN_ADDRESS_FILE =
        "outputfileRetrievalNonRetrievalByRecipientUnkownadress.xml";
    private static final String RETRIEVAL_NO_REASON_FILE =
        "outputfileRetrievalNonRetrievalByRecipientNoreason.xml";
    private static final String SUBMISSION_NO_REJECTION =
        "outputfileSubmissionAcceptanceNORejection.xml";
    private static final String SUBMISSION_YES_REJECTION =
        "outputfileSubmissionAcceptanceYESRejection.xml";
    Resource javaKeyStorePath = new ClassPathResource("keystore.jks");
    String javaKeyStorePassword = "test123";
    String alias = "new_testcert";
    String keyPassword = "test123";
    String javaKeyStoreType = "JKS";
    XMLSignatureFactory sigFactory = null;
    DocumentBuilderFactory dbf;

    /**
     * Set up the test environment for the test class.
     *
     * @throws IOException if an I/O error occurs during file operation
     */
    @BeforeAll
    public static void setUpTestEnv() throws IOException {
        File testDir = Paths.get(PATH_OUTPUT_FILES).toFile();
        try {
            FileUtils.forceDelete(testDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.forceMkdir(testDir);
    }

    @Test
    void testECodexEvidenceBuilder() {
        ECodexEvidenceBuilder result =
            new ECodexEvidenceBuilder(
                javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
            );
        assertNotNull(result);
    }

    // create EDliveryDetails
    private EDeliveryDetails createEntityDetailsObject() {

        PostalAdress address = new PostalAdress();
        address.setCountry("testCountry");
        address.setLocality("testLocality");
        address.setPostalCode("testPostalcode");
        address.setStreetAddress("testStreetaddress");

        Server server = new Server();
        server.setDefaultCitizenQAAlevel(1);
        server.setGatewayAddress("testGatewayaddress");
        server.setGatewayDomain("testGatewaydomain");
        server.setGatewayName("testGatewayname");

        EDeliveryDetail detail = new EDeliveryDetail();

        detail.setPostalAdress(address);
        detail.setServer(server);

        return new EDeliveryDetails(detail);
    }

    // create MessageDetails
    private ECodexMessageDetails createMessageDetailsObject() {

        ECodexMessageDetails messageDetails = new ECodexMessageDetails();
        messageDetails.setEbmsMessageId("testEbms3MsgId");
        messageDetails.setHashAlgorithm("sha1");
        messageDetails.setHashValue(new byte[] {0x000A, 0x000A});
        messageDetails.setNationalMessageId("testNationalMsgId");
        messageDetails.setRecipientAddress("testRecipientAddress");
        messageDetails.setSenderAddress("testSenderAddress");

        return messageDetails;
    }

    @Test
    void testCreateDeliveryNonDeliveryToRecipient() throws Exception {
        byte[] previousEvidence;
        // klara
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified");
        eventReason.setDetails("Originator not known");

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        previousEvidence = createREMEvidenceType1();

        boolean isDelivery = true;
        var ecodexEvidenceBuilder = new ECodexEvidenceBuilder(
            javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
        );
        byte[] signedXmlData = ecodexEvidenceBuilder.createDeliveryNonDeliveryToRecipient(
            isDelivery,
            eventReason,
            evidenceIssuerDetails,
            previousEvidence
        );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + DELIVERY_UNKNOWN_ADDRESS_FILE);
        FileOutputStream fileOutXML = new FileOutputStream(xmloutputfile);
        fileOutXML.write(signedXmlData);
        fileOutXML.close();

        KeyPair keypair = getKeyPairFromKeyStore(
            javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
        );
        PublicKey publicKey;
        publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedXmlData, publicKey), "Signature failed");
    }

    @Test
    void testCreateDeliveryNonDeliveryToRecipient_1() throws Exception {

        byte[] signedxmlData;
        byte[] previousEvidence;

        ECodexEvidenceBuilder ecodexEvidenceBuilder =
            new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword,
                                      alias, keyPassword
            );

        boolean isDelivery = true;

        EventReasonType eventReason = null;

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        previousEvidence = createREMEvidenceType1();

        signedxmlData =
            ecodexEvidenceBuilder.createDeliveryNonDeliveryToRecipient(
                isDelivery,
                eventReason,
                evidenceIssuerDetails,
                previousEvidence
            );

        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + DELIVERY_NO_REASON_FILE);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedxmlData);
        fileoutXML.close();

        KeyPair keypair =
            getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias,
                                   keyPassword
            );
        PublicKey publicKey;
        publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedxmlData, publicKey), "Signature failed");
    }

    // create a REMEvidenceType "SubmissionAcceptanceRejection"
    private byte[] createREMEvidenceType1() {
        byte[] evidenceAsByteArray;
        ECodexEvidenceBuilder builder =
            new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword,
                                      alias, keyPassword
            );

        // klara
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#Other");
        eventReason.setDetails(null);
        evidenceAsByteArray = builder.createSubmissionAcceptanceRejection(
            true, eventReason,
            createEntityDetailsObject(),
            createMessageDetailsObject()
        );

        return evidenceAsByteArray;
    }

    @Test
    void testCreateRetrievalNonRetrievalByRecipient() throws Exception {
        byte[] signedXmlData;
        byte[] previousEvidence;
        ECodexEvidenceBuilder ecodexEvidenceBuilder =
            new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword,
                                      alias, keyPassword
            );
        boolean isDelivery = true;

        // klara
        EventReasonType eventReason = null;

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        previousEvidence = createREMEvidenceType2();

        signedXmlData =
            ecodexEvidenceBuilder.createRetrievalNonRetrievalByRecipient(
                isDelivery,
                eventReason,
                evidenceIssuerDetails,
                previousEvidence
            );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + RETRIEVAL_NO_REASON_FILE);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedXmlData);
        fileoutXML.close();

        KeyPair keypair =
            getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias,
                                   keyPassword
            );
        PublicKey publicKey;
        publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedXmlData, publicKey), "Signature failed");
    }

    // create a REMEvidenceType "DeliveryNonDeliveryToRecipient"

    private byte[] createREMEvidenceType2() {
        byte[] evidenceAsByteArray;
        var builder = new ECodexEvidenceBuilder(
            javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
        );

        // klara
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#Other");
        eventReason.setDetails(null);

        evidenceAsByteArray = builder.createSubmissionAcceptanceRejection(
            true,
            eventReason,
            createEntityDetailsObject(),
            createMessageDetailsObject()
        );

        byte[] evidenceAsByteArray1;
        evidenceAsByteArray1 = builder.createDeliveryNonDeliveryToRecipient(
            true,
            eventReason,
            createEntityDetailsObject(),
            evidenceAsByteArray
        );

        return evidenceAsByteArray1;
    }

    @Test
    void testCreateRetrievalNonRetrievalByRecipient_1() throws Exception {
        byte[] previousEvidence;

        // klara
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified");
        eventReason.setDetails("Originator not known");

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        previousEvidence = createREMEvidenceType2();

        var ecodexEvidenceBuilder = new ECodexEvidenceBuilder(
            javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
        );
        boolean isDelivery = true;
        byte[] signedXmlData = ecodexEvidenceBuilder.createRetrievalNonRetrievalByRecipient(
            isDelivery,
            eventReason,
            evidenceIssuerDetails,
            previousEvidence
        );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + RETRIEVAL_UNKNOWN_ADDRESS_FILE);
        FileOutputStream fileOutXML = new FileOutputStream(xmloutputfile);
        fileOutXML.write(signedXmlData);
        fileOutXML.close();

        KeyPair keypair =
            getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias,
                                   keyPassword
            );
        var publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedXmlData, publicKey), "Signature failed");
    }

    @Test
    void testCreateSubmissionAcceptanceRejection_shouldAcceptanceTrue() throws Exception {
        byte[] signedXmlData;
        ECodexEvidenceBuilder ecodexEvidenceBuilder =
            new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword,
                                      alias, keyPassword
            );
        boolean isAcceptance = true;

        // klara
        EventReasonType eventReason = null;

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        ECodexMessageDetails messageDetails = createMessageDetailsObject();

        // run methode createSubmissionAcceptanceRejection
        signedXmlData =
            ecodexEvidenceBuilder.createSubmissionAcceptanceRejection(isAcceptance, eventReason,
                                                                      evidenceIssuerDetails,
                                                                      messageDetails
            );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + SUBMISSION_NO_REJECTION);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedXmlData);
        fileoutXML.close();

        KeyPair keypair =
            getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias,
                                   keyPassword
            );
        PublicKey publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedXmlData, publicKey), "Signature failed");
    }

    @Test
    void testCreateSubmissionAcceptanceRejection_shouldAcceptanceFalse() throws Exception {
        byte[] signedxmlData;
        ECodexEvidenceBuilder ecodexEvidenceBuilder =
            new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword,
                                      alias, keyPassword
            );
        boolean isAcceptance = false;

        // klara
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified");
        eventReason.setDetails("Originator not known");

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        ECodexMessageDetails messageDetails = createMessageDetailsObject();

        // run methode createSubmissionAcceptanceRejection
        signedxmlData =
            ecodexEvidenceBuilder.createSubmissionAcceptanceRejection(isAcceptance, eventReason,
                                                                      evidenceIssuerDetails,
                                                                      messageDetails
            );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + SUBMISSION_YES_REJECTION);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedxmlData);
        fileoutXML.close();
        // to test: if file A.xml is changed.
        KeyPair keypair =
            getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias,
                                   keyPassword
            );
        PublicKey publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedxmlData, publicKey), "Signature failed");
    }

    // Signature validation
    private boolean signatureValidate(byte[] signedXmlData, PublicKey publicKey) {
        InMemoryDocument document = new InMemoryDocument(signedXmlData);

        SignedDocumentValidator val = SignedDocumentValidator.fromDocument(document);
        CommonCertificateVerifier certVeri = new CommonCertificateVerifier();
        val.setCertificateVerifier(certVeri);

        Reports test = val.validateDocument();
        boolean sigValid = test.getDiagnosticData()
                               .getSignatureById(test.getDiagnosticData().getFirstSignatureId())
                               .isSignatureIntact();
        boolean sigIntact = test.getDiagnosticData()
                                .getSignatureById(test.getDiagnosticData().getFirstSignatureId())
                                .isSignatureIntact();

        return sigValid && sigIntact;

        // 2016-03-03 klara:
        // Switched to DSS Validation due to missing time to fix problem with
        // the dereferencing in the java.crypto signature validation.
    }

    private XMLSignatureFactory getSignatureFactory() {
        if (sigFactory == null) {
            sigFactory = XMLSignatureFactory.getInstance("DOM");
        }
        return sigFactory;
    }

    // private method to get the keypair
    private KeyPair getKeyPairFromKeyStore(
        Resource store, String storeType, String storePass, String alias, String keyPass)
        throws Exception {
        KeyStore ks;
        InputStream kfis;

        ks = KeyStore.getInstance(storeType);

        kfis = store.getInputStream();
        ks.load(kfis, (storePass == null) ? null : storePass.toCharArray());

        KeyPair keyPair;
        Key key;
        PublicKey publicKey;
        PrivateKey privateKey;

        if (ks.containsAlias(alias)) {
            key = ks.getKey(alias, keyPass.toCharArray());
            if (key instanceof PrivateKey) {
                X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
                publicKey = cert.getPublicKey();
                privateKey = (PrivateKey) key;
                keyPair = new KeyPair(publicKey, privateKey);
            } else {
                keyPair = null;
            }
        } else {
            keyPair = null;
        }
        return keyPair;
    }

    // create array bytes from xmlFile
    private byte[] getBytesFromFile(String xmlFilePath) throws Exception {

        byte[] bytes;
        File file = new File(xmlFilePath);
        InputStream is;
        is = new FileInputStream(file);
        // Get the size of the file
        long length = file.length();
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            System.err.println("Too large file");
        }
        // Create the byte array to hold the data
        bytes = new byte[(int) length];
        // Read in the bytes
        int offset = 0;
        int numRead;
        while (offset < bytes.length
            && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            System.err.println("Could not completely read file " + file.getName());
        }
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * Perform pre-test initialization.
     */
    @BeforeEach
    public void setUp() {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
    }
}
