package eu.ecodex.signature;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
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
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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

    /**
     * Sets up the test environment before running test cases.
     *
     * @throws IOException if an I/O exception occurs while deleting or creating directories
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
    void testEvidenceUtilsImpl() {
        EvidenceUtilsImpl result =
            new EvidenceUtilsImpl(
                javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
            );
        // add additional test code here
        assertNotNull(result);
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

    // private method to get the keypair
    private KeyPair getKeyPairFromKeyStore(
        Resource store, String keyStoreType, String storePass, String alias, String keyPass)
        throws Exception {
        KeyStore ks;
        InputStream kfis;

        ks = KeyStore.getInstance(keyStoreType);

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

    @Test
    void testSignByteArray() throws Exception {
        // create signature with methode signByteArray
        InputStream xmlFileInputStream =
            new ClassPathResource("ConvertIntoEvidenceTypetestFileA.xml").getInputStream();
        byte[] xmlData = StreamUtils.copyToByteArray(xmlFileInputStream);
        EvidenceUtilsImpl fixture =
            new EvidenceUtilsImpl(
                javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
            );

        byte[] signedXmlData = fixture.signByteArray(xmlData);

        File xmloutputfile = new File(PATH_OUTPUT_FILES + SIGN_BYTE_ARRAY_FILE);
        FileOutputStream fileOutXML = new FileOutputStream(xmloutputfile);
        fileOutXML.write(signedXmlData);
        fileOutXML.close();

        // validate Signature
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document document;
        document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signedXmlData));

        KeyPair keypair =
            getKeyPairFromKeyStore(
                javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
            );

        PublicKey publicKey = keypair.getPublic();

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
        var signatureNode = nl.item(0);
        var factory = getSignatureFactory();
        var signature = factory.unmarshalXMLSignature(new DOMStructure(signatureNode));
        // Create ValidateContext
        var valContext = new DOMValidateContext(publicKey, signatureNode);

        // Validate the XMLSignature
        signStatus = signStatus && signature.validate(valContext);
        // check the validation status of each Reference
        List<?> refs = signature.getSignedInfo().getReferences();
        for (Object o : refs) {
            Reference ref = (Reference) o;
            signStatus = signStatus && ref.validate(valContext);
        }
        return signStatus;
    }

    private XMLSignatureFactory getSignatureFactory() {
        if (signFactory == null) {
            signFactory = XMLSignatureFactory.getInstance("DOM");
        }
        return signFactory;
    }

    /**
     * Run the boolean verifySignature(byte[]) method test.
     */
    @Test
    void testVerifySignature() {
        EvidenceUtilsImpl fixture =
            new EvidenceUtilsImpl(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias,
                                  keyPassword
            );
        byte[] xmlData = new byte[] {};

        boolean result = fixture.verifySignature(xmlData);

        assertFalse(result);
    }

    @BeforeEach
    public void setUp() {
        signFactory = XMLSignatureFactory.getInstance("DOM");
    }
}
