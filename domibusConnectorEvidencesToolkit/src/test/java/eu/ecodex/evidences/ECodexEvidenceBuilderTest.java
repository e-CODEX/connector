package eu.ecodex.evidences;

import eu.ecodex.evidences.exception.ECodexEvidenceBuilderException;
import eu.ecodex.evidences.types.ECodexMessageDetails;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail.PostalAdress;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail.Server;
import org.apache.commons.io.FileUtils;
import org.etsi.uri._02640.v2.EventReasonType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;

import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
    private static final String DELIVERY_NO_REASON_FILE = "outputfileDeliveryNonDeliveryToRecipientNoreason.xml";

    private static final String RETRIEVAL_UNKNOWN_ADDRESS_FILE =
            "outputfileRetrievalNonRetrievalByRecipientUnkownadress.xml";
    private static final String RETRIEVAL_NO_REASON_FILE = "outputfileRetrievalNonRetrievalByRecipientNoreason.xml";

    private static final String SUBMISSION_NO_REJECTION = "outputfileSubmissionAcceptanceNORejection.xml";
    private static final String SUBMISSION_YES_REJECTION = "outputfileSubmissionAcceptanceYESRejection.xml";

    Resource javaKeyStorePath = new ClassPathResource("keystore.jks");
    String javaKeyStorePassword = "test123";
    String alias = "new_testcert";
    String keyPassword = "test123";
    String javaKeyStoreType = "JKS";
    XMLSignatureFactory sigFactory = null;
    DocumentBuilderFactory dbf;

    @BeforeAll
    public static void setUpTestEnv() throws IOException {
        File testDir = Paths.get(PATH_OUTPUT_FILES).toFile();
        try {
            FileUtils.forceDelete(testDir);
        } catch (IOException e) {
        }
        FileUtils.forceMkdir(testDir);
    }

    /**
     * Run the ECodexEvidenceBuilder(String,String,String,String) constructor
     * test.
     *
     * @throws Exception
     */
    @Test
    void testECodexEvidenceBuilder() throws Exception {
        ECodexEvidenceBuilder result =
                new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
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
        messageDetails.setHashValue(new byte[]{0x000A, 0x000A});
        messageDetails.setNationalMessageId("testNationalMsgId");
        messageDetails.setRecipientAddress("testRecipientAddress");
        messageDetails.setSenderAddress("testSenderAddress");

        return messageDetails;
    }

    /**
     * Run the byte[]
     * createDeliveryNonDeliveryToRecipient(boolean,REMErrorEvent,
     * EDeliveryDetails,REMEvidenceType) method test.
     * <p>
     * Case: Eventreason = UNKNOWN_ORIGINATOR_ADDRESS;
     *
     * @throws Exception
     */
    @Test
    void testCreateDeliveryNonDeliveryToRecipient() throws Exception {
        byte[] signedxmlData;
        byte[] previousEvidence;
        PublicKey publicKey;
        ECodexEvidenceBuilder ecodexEvidenceBuilder =
                new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        boolean isDelivery = true;

        // klara
        //	REMErrorEvent eventReason = REMErrorEvent.UNKNOWN_ORIGINATOR_ADDRESS;
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified");
        eventReason.setDetails("Originator not known");

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        previousEvidence = createREMEvidenceType1();

        signedxmlData = ecodexEvidenceBuilder.createDeliveryNonDeliveryToRecipient(
                isDelivery,
                eventReason,
                evidenceIssuerDetails,
                previousEvidence
        );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + DELIVERY_UNKNOWN_ADDRESS_FILE);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedxmlData);
        fileoutXML.close();

        Document document;
        document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signedxmlData));

        KeyPair keypair =
                getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedxmlData, publicKey), "Signature failed");
    }

    /**
     * Run the byte[]
     * createDeliveryNonDeliveryToRecipient(boolean,REMErrorEvent,
     * EDeliveryDetails,REMEvidenceType) method test.
     * <p>
     * Case: Eventreason = null
     *
     * @throws Exception
     */
    @Test
    void testCreateDeliveryNonDeliveryToRecipient_1() throws Exception {

        byte[] signedxmlData;
        byte[] previousEvidence;

        PublicKey publicKey;

        ECodexEvidenceBuilder ecodexEvidenceBuilder =
                new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);

        boolean isDelivery = true;

        //	REMErrorEvent eventReason = null;
        EventReasonType eventReason = null;

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        previousEvidence = createREMEvidenceType1();

        signedxmlData = ecodexEvidenceBuilder.createDeliveryNonDeliveryToRecipient(
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

        Document document;
        document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signedxmlData));

        KeyPair keypair =
                getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedxmlData, publicKey), "Signature failed");
    }

    // create a REMEvidenceType "SubmissionAcceptanceRejection"
    private byte[] createREMEvidenceType1() throws ECodexEvidenceBuilderException {
        // REMEvidenceType evidenceType;

        byte[] evidenceAsByteArray;
        ECodexEvidenceBuilder builder =
                new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);

        // klara
        //	evidenceAsByteArray = builder.createSubmissionAcceptanceRejection(true, REMErrorEvent.OTHER,
        //	createEntityDetailsObject(), createMessageDetailsObject());
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#Other");
        eventReason.setDetails(null);
        evidenceAsByteArray = builder.createSubmissionAcceptanceRejection(
                true,
                eventReason,
                createEntityDetailsObject(),
                createMessageDetailsObject()
        );

        // EvidenceUtils utils = new EvidenceUtilsImpl(javaKeyStorePath,
        // javaKeyStorePassword, alias, keyPassword);
        // evidenceType = utils.convertIntoEvidenceType(evidenceAsByteArray);

        return evidenceAsByteArray;
    }

    /**
     * Run the byte[]
     * createRetrievalNonRetrievalByRecipient(boolean,REMErrorEvent
     * ,EDeliveryDetails,REMEvidenceType) method test.
     * <p>
     * Case: Eventreason = null
     *
     * @throws Exception
     */
    @Test
    void testCreateRetrievalNonRetrievalByRecipient() throws Exception {
        byte[] signedxmlData;
        byte[] previousEvidence;
        PublicKey publicKey;
        ECodexEvidenceBuilder ecodexEvidenceBuilder =
                new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        boolean isDelivery = true;

        // klara
        //	REMErrorEvent eventReason = null;
        EventReasonType eventReason = null;

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        previousEvidence = createREMEvidenceType2();

        signedxmlData = ecodexEvidenceBuilder.createRetrievalNonRetrievalByRecipient(
                isDelivery,
                eventReason,
                evidenceIssuerDetails,
                previousEvidence
        );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + RETRIEVAL_NO_REASON_FILE);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedxmlData);
        fileoutXML.close();

        Document document;
        document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signedxmlData));

        KeyPair keypair =
                getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedxmlData, publicKey), "Signature failed");
    }

    // create a REMEvidenceType "DeliveryNonDeliveryToRecipient"

    private byte[] createREMEvidenceType2() throws ECodexEvidenceBuilderException {
        // REMEvidenceType evidenceType;
        byte[] evidenceAsByteArray;
        byte[] evidenceAsByteArray1;
        ECodexEvidenceBuilder builder =
                new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);

        // klara
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#Other");
        eventReason.setDetails(null);

        // evidenceAsByteArray = builder.createSubmissionAcceptanceRejection(true, REMErrorEvent.OTHER,
        // createEntityDetailsObject(), createMessageDetailsObject());
        evidenceAsByteArray = builder.createSubmissionAcceptanceRejection(
                true,
                eventReason,
                createEntityDetailsObject(),
                createMessageDetailsObject()
        );
        // EvidenceUtils utils = new EvidenceUtilsImpl(javaKeyStorePath,
        // javaKeyStorePassword, alias, keyPassword);

        //	evidenceAsByteArray1 = builder.createDeliveryNonDeliveryToRecipient(true, REMErrorEvent.OTHER,
        //	createEntityDetailsObject(), evidenceAsByteArray);
        evidenceAsByteArray1 = builder.createDeliveryNonDeliveryToRecipient(
                true,
                eventReason,
                createEntityDetailsObject(),
                evidenceAsByteArray
        );

        // evidenceType = utils.convertIntoEvidenceType(evidenceAsByteArray1);

        return evidenceAsByteArray1;
    }

    /**
     * Run the byte[]
     * createRetrievalNonRetrievalByRecipient(boolean,REMErrorEvent
     * ,EDeliveryDetails,REMEvidenceType) method test.
     * <p>
     * Case: Eventreason = UNKNOWN_ORIGINATOR_ADDRESS;
     *
     * @throws Exception
     */
    @Test
    void testCreateRetrievalNonRetrievalByRecipient_1() throws Exception {
        byte[] signedxmlData;
        byte[] previousEvidence;
        PublicKey publicKey;
        ECodexEvidenceBuilder ecodexEvidenceBuilder =
                new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        boolean isDelivery = true;

        // klara
        //	REMErrorEvent eventReason = REMErrorEvent.UNKNOWN_ORIGINATOR_ADDRESS;
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified");
        eventReason.setDetails("Originator not known");

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        previousEvidence = createREMEvidenceType2();

        signedxmlData = ecodexEvidenceBuilder.createRetrievalNonRetrievalByRecipient(
                isDelivery,
                eventReason,
                evidenceIssuerDetails,
                previousEvidence
        );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + RETRIEVAL_UNKNOWN_ADDRESS_FILE);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedxmlData);
        fileoutXML.close();

        Document document;
        document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signedxmlData));

        KeyPair keypair =
                getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedxmlData, publicKey), "Signature failed");
    }

    /**
     * Run the byte[] createSubmissionAcceptanceRejection(boolean,REMErrorEvent,
     * EDeliveryDetails,ECodexMessageDetails) method test.
     * <p>
     * Case: isAcceptance=true
     *
     * @throws Exception
     */
    @Test
    void testCreateSubmissionAcceptanceRejection_shouldAcceptanceTrue() throws Exception {
        byte[] signedxmlData;
        PublicKey publicKey;
        ECodexEvidenceBuilder ecodexEvidenceBuilder =
                new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        boolean isAcceptance = true;

        // klara
        //	REMErrorEvent eventReason = null;
        EventReasonType eventReason = null;

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        ECodexMessageDetails messageDetails = createMessageDetailsObject();

        // run methode createSubmissionAcceptanceRejection
        signedxmlData = ecodexEvidenceBuilder.createSubmissionAcceptanceRejection(
                isAcceptance,
                eventReason,
                evidenceIssuerDetails,
                messageDetails
        );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + SUBMISSION_NO_REJECTION);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedxmlData);
        fileoutXML.close();

        Document document;
        document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signedxmlData));

        KeyPair keypair =
                getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        publicKey = keypair.getPublic();

        assertTrue(signatureValidate(signedxmlData, publicKey), "Signature failed");
    }

    /**
     * Run the byte[] createSubmissionAcceptanceRejection(boolean,REMErrorEvent,
     * EDeliveryDetails,ECodexMessageDetails) method test.
     * <p>
     * Case: isAcceptance=false
     *
     * @throws Exception
     */
    @Test
    void testCreateSubmissionAcceptanceRejection_shouldAcceptanceFalse() throws Exception {
        byte[] signedxmlData;
        PublicKey publicKey;
        ECodexEvidenceBuilder ecodexEvidenceBuilder =
                new ECodexEvidenceBuilder(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        boolean isAcceptance = false;

        // klara
        //	REMErrorEvent eventReason = REMErrorEvent.UNKNOWN_ORIGINATOR_ADDRESS;
        EventReasonType eventReason = new EventReasonType();
        eventReason.setCode("http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified");
        eventReason.setDetails("Originator not known");

        EDeliveryDetails evidenceIssuerDetails = createEntityDetailsObject();
        ECodexMessageDetails messageDetails = createMessageDetailsObject();

        // run methode createSubmissionAcceptanceRejection
        signedxmlData = ecodexEvidenceBuilder.createSubmissionAcceptanceRejection(
                isAcceptance,
                eventReason,
                evidenceIssuerDetails,
                messageDetails
        );
        // output the signed Xmlfile
        File xmloutputfile = new File(PATH_OUTPUT_FILES + SUBMISSION_YES_REJECTION);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedxmlData);
        fileoutXML.close();
        //	// to test: if file A.xml is changed.
        //	signedxmlData = getBytesFromFile("src/test/resources/signatureTestbysourceInfochange.xml");
        Document document;
        document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signedxmlData));
        // KeyPair keypair= generateNewRandomKeyPair();
        KeyPair keypair =
                getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        publicKey = keypair.getPublic();

        // System.out.println(publicKey.toString());

        assertTrue(signatureValidate(signedxmlData, publicKey), "Signature failed");
    }

    // Signature validation
    private boolean signatureValidate(byte[] signedxmlData, PublicKey publicKey) throws Exception {

        InMemoryDocument docum = new InMemoryDocument(signedxmlData);

        SignedDocumentValidator val = SignedDocumentValidator.fromDocument(docum);
        CommonCertificateVerifier certVeri = new CommonCertificateVerifier();
        val.setCertificateVerifier(certVeri);

        Reports test = val.validateDocument();
        boolean sigValid = test.getDiagnosticData().getSignatureById(test.getDiagnosticData().getFirstSignatureId())
                               .isSignatureIntact();
        boolean sigIntact = test.getDiagnosticData().getSignatureById(test.getDiagnosticData().getFirstSignatureId())
                                .isSignatureIntact();

        return sigValid && sigIntact;

        // 2016-03-03 klara:
        // Switched to DSS Validation due to missing time to fix problem with
        // the dereferencing in the java.crypto signature validation.

        //    	boolean signStatus = true;
        //
        //    	NodeList signedPropsNL = doc.getElementsByTagName("SignedProperties");
        //    	if (signedPropsNL.getLength() != 0) {
        //    		Node signedProps = signedPropsNL.item(0);
        //    		((Element) signedProps).setIdAttribute("Id", true);
        //    	}
        //
        //    	NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        //
        //    	if (nl.getLength() == 0) {
        //    		throw new Exception("Cannot find Signature element");
        //    	}
        //
        //    	Node signatureNode = nl.item(0);
        //    	XMLSignatureFactory factory = getSignatureFactory();
        //
        //    	// to test: CASE 1
        //    	// XMLSignature signature = factory
        //    	// .unmarshalXMLSignature(new DOMStructure(signatureNode));
        //
        //    	// Create ValidateContext
        //    	DOMValidateContext valContext = new DOMValidateContext(publicKey, signatureNode);
        //
        //    	// to test: CASE 2
        //    	XMLSignature signature = factory.unmarshalXMLSignature(valContext);
        //
        //    	// Validate the XMLSignature
        //    	signStatus = signStatus && signature.validate(valContext);
        //
        //    	// check the validation status of each Reference
        //    	List<?> refs = signature.getSignedInfo().getReferences();
        //
        //    	for (int i = 0; i < refs.size(); i++) {
        //    		Reference ref = (Reference) refs.get(i);
        //
        //    		// System.out.println("Reference[" + i + "] validity status: "
        //    		// + ref.validate(valContext));
        //    		signStatus = signStatus && ref.validate(valContext);
        //    	}
        //
        //    	return signStatus;
    }

    private XMLSignatureFactory getSignatureFactory() throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        if (sigFactory == null) sigFactory = XMLSignatureFactory.getInstance("DOM");
        return sigFactory;
    }

    // private method to get the keypair
    private KeyPair getKeyPairFromKeyStore(
            Resource store,
            String storeType,
            String storePass,
            String alias,
            String keyPass) throws Exception {
        KeyStore ks;
        InputStream kfis;
        KeyPair keyPair = null;

        Key key = null;
        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        ks = KeyStore.getInstance(storeType);

        // final URL ksLocation = new URL(store);

        kfis = store.getInputStream();
        ks.load(kfis, (storePass == null) ? null : storePass.toCharArray());

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
        byte[] bytes = null;
        File file = new File(xmlFilePath);
        InputStream is;
        is = new FileInputStream(file);
        // Get the size of the file
        long length = file.length();
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // throw new Exception("Too large file");
            System.err.println("Too large file");
        }
        // Create the byte array to hold the data
        bytes = new byte[(int) length];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
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

    // create random KeyPair with algorithm "RSA"
    // private static KeyPair generateNewRandomKeyPair()
    // throws NoSuchAlgorithmException {
    // final int KEY_SIZE = 1024;
    // final String ALGORITHM = "RSA";
    // final String RANDOM_ALG = "SHA1PRNG";
    // KeyPairGenerator kg = null;
    // SecureRandom random = null;
    // kg = KeyPairGenerator.getInstance(ALGORITHM);
    // random = SecureRandom.getInstance(RANDOM_ALG);
    // kg.initialize(KEY_SIZE, random);
    // return kg.generateKeyPair();
    // }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception if the initialization fails for some reason
     */
    @BeforeEach
    public void setUp() throws Exception {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
    }
}
