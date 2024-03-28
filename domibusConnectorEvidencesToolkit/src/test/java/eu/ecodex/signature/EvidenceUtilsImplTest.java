package eu.ecodex.signature;


import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * The class <code>EvidenceUtilsImplTest</code> contains tests for the class
 * <code>{@link EvidenceUtilsImpl}</code>.
 *
 * @author cheny01
 * @version $Revision: 1.0 $
 */
public class EvidenceUtilsImplTest {
    private static final String PATH_OUTPUT_FILES = "target/test/EvidenceUtilsImplTest/";
    private static final String SIGN_BYTE_ARRAY_FILE = "SignByteArrayTestoutputFile.xml";

    XMLSignatureFactory signFactory;
    Resource javaKeyStorePath = new ClassPathResource("keystore.jks");
    String javaKeyStorePassword = "test123";
    String alias = "new_Testcert";
    String keyPassword = "test123";
    String javaKeyStoreType = "JKS";

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
     * Run the EvidenceUtilsImpl(String,String,String,String) constructor test.
     *
     * @throws Exception
     */
    @Test
    void testEvidenceUtilsImpl() throws Exception {
        EvidenceUtilsImpl result =
                new EvidenceUtilsImpl(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        // add additional test code here
        assertNotNull(result);
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

    // /**
    // * Run the REMEvidenceType convertIntoEvidenceType(byte[]) method test.
    // *
    // * @throws Exception
    // *
    // *
    // */
    // @Test
    // public void testConvertIntoEvidenceType()
    // throws Exception {
    //
    // String xmlFile
    // ="src/test/resources/ConvertIntoEvidenceTypetestFileA.xml";
    // byte[] xmlData = getBytesFromFile(xmlFile);
    // EvidenceUtilsImpl fixture= new EvidenceUtilsImpl(javaKeyStorePath,
    // javaKeyStorePassword, alias, keyPassword);
    //
    // REMEvidenceType remEvidenceType = null;
    // remEvidenceType= fixture.convertIntoEvidenceType(xmlData);
    // //
    // System.out.print((remEvidenceType.getEvidenceIssuerDetails().getNamesPostalAddresses()).getNamePostalAddress()
    // .get(0).getEntityName().getName());
    // assertTrue("failure by runing the method convertIntoEvidenceType",
    // (remEvidenceType.getEvidenceIssuerDetails().getNamesPostalAddresses()).getNamePostalAddress().get(0)
    // .getEntityName().getName().toString().equalsIgnoreCase("[gatewayname]"));
    //
    // }

    // private method to get the keypair
    private KeyPair getKeyPairFromKeyStore(
            Resource store,
            String keyStoreType,
            String storePass,
            String alias,
            String keyPass) throws Exception {
        KeyStore ks;
        InputStream kfis;
        KeyPair keyPair = null;

        Key key = null;
        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        ks = KeyStore.getInstance(keyStoreType);
        //        final URL ksLocation = new URL(store);

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

    /**
     * Run the byte[] signByteArray(byte[]) method test.
     *
     * @throws Exception
     */
    @Test
    void testSignByteArray() throws Exception {

        // create signature with methode signByteArray
        InputStream xmlFileInputStream = new ClassPathResource("ConvertIntoEvidenceTypetestFileA.xml").getInputStream();
        byte[] xmlData = StreamUtils.copyToByteArray(xmlFileInputStream);
        EvidenceUtilsImpl fixture =
                new EvidenceUtilsImpl(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);

        byte[] signedxmlData = fixture.signByteArray(xmlData);

        File xmloutputfile = new File(PATH_OUTPUT_FILES + SIGN_BYTE_ARRAY_FILE);
        FileOutputStream fileoutXML = new FileOutputStream(xmloutputfile);
        fileoutXML.write(signedxmlData);
        fileoutXML.close();

        // validate Signature
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document document;
        document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signedxmlData));

        KeyPair keypair =
                getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);

        PublicKey publicKey = keypair.getPublic();

        // System.out.println(keypair.getPublic().toString());
        try {

            assertTrue(signatureValidate(document, publicKey), "Signature failed");
        } catch (MarshalException ex) {
            fail("Unmarshal failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Signature validation
    private boolean signatureValidate(Document doc, PublicKey publicKey) throws Exception {
        boolean signStatus = true;
        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        }
        Node signatureNode = nl.item(0);
        XMLSignatureFactory factory = getSignatureFactory();
        XMLSignature signature = factory.unmarshalXMLSignature(new DOMStructure(signatureNode));
        // Create ValidateContext
        DOMValidateContext valContext = new DOMValidateContext(publicKey, signatureNode);

        // Validate the XMLSignature
        signStatus = signStatus && signature.validate(valContext);
        // check the validation status of each Reference
        List<?> refs = signature.getSignedInfo().getReferences();
        for (int i = 0; i < refs.size(); i++) {
            Reference ref = (Reference) refs.get(i);
            // System.out.println("Reference[" + i + "] validity status: "
            // + ref.validate(valContext));
            signStatus = signStatus && ref.validate(valContext);
        }
        return signStatus;
    }

    private XMLSignatureFactory getSignatureFactory() throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        if (signFactory == null) signFactory = XMLSignatureFactory.getInstance("DOM");
        return signFactory;
    }

    /**
     * Run the boolean verifySignature(byte[]) method test.
     *
     * @throws Exception
     */
    @Test
    void testVerifySignature() throws Exception {
        EvidenceUtilsImpl fixture =
                new EvidenceUtilsImpl(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
        byte[] xmlData = new byte[]{};

        boolean result = fixture.verifySignature(xmlData);

        assertFalse(result);
    }

    @BeforeEach
    public void setUp() throws Exception {

        signFactory = XMLSignatureFactory.getInstance("DOM");
    }
}
