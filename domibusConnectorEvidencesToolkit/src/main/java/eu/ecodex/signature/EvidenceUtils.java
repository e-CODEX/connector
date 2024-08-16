/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.signature;

import eu.spocseu.edeliverygw.JaxbContextHolder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This abstract class provides utility methods for working with evidence.
 */
@SuppressWarnings("squid:S1135")
public abstract class EvidenceUtils {
    protected static Logger LOG = Logger.getLogger(EvidenceUtilsImpl.class);
    protected Resource javaKeyStorePath;
    protected final String javaKeyStorePassword;
    protected final String alias;
    protected final String keyPassword;
    protected final String javaKeyStoreType;

    /**
     * EvidenceUtils is a utility class for handling evidence related operations.
     *
     * @param javaKeyStorePath     The path to the Java keystore.
     * @param storeType            The type of the Java keystore.
     * @param javaKeyStorePassword The password for the Java keystore.
     * @param alias                The alias for the key in the keystore.
     * @param keyPassword          The password for the key in the keystore.
     */
    protected EvidenceUtils(
        Resource javaKeyStorePath, String storeType, String javaKeyStorePassword, String alias,
        String keyPassword) {
        this.javaKeyStoreType = storeType;
        this.javaKeyStorePath = javaKeyStorePath;
        this.javaKeyStorePassword = javaKeyStorePassword;
        this.alias = alias;
        this.keyPassword = keyPassword;
    }

    public abstract byte[] signByteArray(byte[] xmlData);

    public abstract boolean verifySignature(byte[] xmlData);

    protected static synchronized KeyPair getKeyPairFromKeyStore(
        Resource store, String storePass, String alias, String keyPass) {
        LOG.debug("Loading KeyPair from Java KeyStore(" + store + ")");
        KeyStore ks;
        InputStream kfis;
        KeyPair keyPair = null;

        Key key;
        PublicKey publicKey;
        try {
            ks = KeyStore.getInstance("JKS");

            kfis = store.getInputStream();
            ks.load(kfis, (storePass == null) ? null : storePass.toCharArray());

            if (ks.containsAlias(alias)) {
                key = ks.getKey(alias, keyPass.toCharArray());
                if (key instanceof PrivateKey privateKey) {
                    X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
                    publicKey = cert.getPublicKey();
                    keyPair = new KeyPair(publicKey, privateKey);
                }
            }
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
                 | CertificateException | IOException e) {
            LOG.error("Cannot get keypair from keystore", e); // TODO: runtime exception?
        }

        return keyPair;
    }

    /**
     * Converts a byte array into a REMEvidenceType object.
     *
     * @param xmlData The byte array containing the XML data to convert.
     * @return The converted REMEvidenceType object.
     */
    public REMEvidenceType convertIntoEvidenceType(byte[] xmlData) {
        var builderFactory = DocumentBuilderFactory.newInstance();
        try {
            builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        builderFactory.setNamespaceAware(true);
        REMEvidenceType convertedEvidence = null;
        Document doc;

        LOG.debug("Convert byte-array into Evidence");
        try {
            doc = builderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(xmlData));

            convertedEvidence = convertIntoREMEvidenceType(doc).getValue();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            LOG.error(
                "Failed to convert convertIntoREMEvidenceType", e); // TODO: runtime exception?
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
