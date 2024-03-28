package eu.ecodex.signature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;


public class EvidenceUtilsImpl extends EvidenceUtils {
    private static final Logger LOGGER = LogManager.getLogger(EvidenceUtilsImpl.class);

    public EvidenceUtilsImpl(
            Resource javaKeyStorePath,
            String javaKeyStoreType,
            String javaKeyStorePassword,
            String alias,
            String keyPassword) {
        super(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
    }

    @Override
    public byte[] signByteArray(byte[] xmlData) {
        LOG.info("Java API Signer used");
        byte[] signedByteArray = null;

        // Create a DOM XMLSignatureFactory that will be used to generate the
        // enveloped signature

        try {
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            // Create a Reference to the enveloped document (in this case we are
            // signing the whole document, so a URI of "" signifies that) and
            // also specify the SHA1 digest algorithm and the ENVELOPED
            // Transform.
            Reference ref = fac.newReference(
                    "",
                    fac.newDigestMethod(DigestMethod.SHA1, null),
                    Collections.singletonList(fac.newTransform(
                            Transform.ENVELOPED,
                            (TransformParameterSpec) null
                    )),
                    null,
                    null
            );

            // Create the SignedInfo
            SignedInfo si = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(
                            CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                            (C14NMethodParameterSpec) null
                    ),
                    fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    Collections.singletonList(ref)
            );

            // Load KeyPair from Java Key Store
            KeyPair kp = getKeyPairFromKeyStore(javaKeyStorePath, javaKeyStorePassword, alias, keyPassword);

            // Create a KeyValue containing the PublicKey that was generated
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            KeyValue kv;

            kv = kif.newKeyValue(kp.getPublic());
            // Create a KeyInfo and add the KeyValue to it
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

            // Instantiate the document to be signed
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc;
            doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmlData));

            // Create a DOMSignContext and specify the PrivateKey and
            // location of the resulting XMLSignature's parent element
            DOMSignContext dsc = new DOMSignContext(kp.getPrivate(), doc.getDocumentElement());

            // Create the XMLSignature (but don't sign it yet)
            XMLSignature signature = fac.newXMLSignature(si, ki);

            // Marshal, generate (and sign) the enveloped signature
            signature.sign(dsc);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(bos));

            signedByteArray = bos.toByteArray();
        } catch (KeyException | SAXException | IOException | ParserConfigurationException | TransformerException |
                 MarshalException | XMLSignatureException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException e1) {
            LOGGER.error("Cannot signByteArray due", e1);
        }

        return signedByteArray;
    }

    @Override
    public boolean verifySignature(byte[] xmlData) {
        // TODO Auto-generated method stub
        return false;
    }
}
