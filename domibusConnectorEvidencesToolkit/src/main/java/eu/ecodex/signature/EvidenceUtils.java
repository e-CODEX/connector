package eu.ecodex.signature;

import eu.spocseu.edeliverygw.JaxbContextHolder;
import org.apache.log4j.Logger;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public abstract class EvidenceUtils {
    protected static Logger LOG = Logger.getLogger(EvidenceUtilsImpl.class);
    protected final String javaKeyStorePassword, alias, keyPassword;
    protected final String javaKeyStoreType;
    protected Resource javaKeyStorePath;

    public EvidenceUtils(
            Resource javaKeyStorePath,
            String storeType,
            String javaKeyStorePassword,
            String alias,
            String keyPassword) {
        this.javaKeyStoreType = storeType;
        this.javaKeyStorePath = javaKeyStorePath;
        this.javaKeyStorePassword = javaKeyStorePassword;
        this.alias = alias;
        this.keyPassword = keyPassword;
    }

    protected synchronized static KeyPair getKeyPairFromKeyStore(
            Resource store, String storePass, String alias, String keyPass) {
        LOG.debug("Loading KeyPair from Java KeyStore(" + store + ")");
        KeyStore ks;
        InputStream kfis;
        KeyPair keyPair = null;

        Key key = null;
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        try {
            ks = KeyStore.getInstance("JKS");

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
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException |
                 IOException e) {
            LOG.error("Cannot get keypair from keystore", e); // TODO: runtime exception?
        }

        return keyPair;
    }

    public abstract byte[] signByteArray(byte[] xmlData);

    public abstract boolean verifySignature(byte[] xmlData);

    public REMEvidenceType convertIntoEvidenceType(byte[] xmlData) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        REMEvidenceType convertedEvidence = null;
        Document doc;

        LOG.debug("Convert byte-array into Evidence");
        try {
            doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmlData));

            convertedEvidence = convertIntoREMEvidenceType(doc).getValue();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            LOG.error("Failed to convert convertIntoREMEvidenceType", e); // TODO: runtime exception?
        }

        return convertedEvidence;
    }

    private JAXBElement<REMEvidenceType> convertIntoREMEvidenceType(Document domDocument) {
        JAXBElement<REMEvidenceType> jaxbObj = null;

        try {
            jaxbObj = JaxbContextHolder.getSpocsJaxBContext().createUnmarshaller()
                                       .unmarshal(domDocument, REMEvidenceType.class);
        } catch (JAXBException e) {
            LOG.error(e); // TODO: runtime exception?
        }

        return jaxbObj;
    }
}
