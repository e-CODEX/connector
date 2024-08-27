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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import javax.xml.XMLConstants;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class provides implementation for utility methods related to evidence handling.
 */
public class EvidenceUtilsImpl extends EvidenceUtils {
    private static final Logger LOGGER = LogManager.getLogger(EvidenceUtilsImpl.class);

    public EvidenceUtilsImpl(
        Resource javaKeyStorePath, String javaKeyStoreType,
        String javaKeyStorePassword, String alias, String keyPassword) {
        super(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
    }

    @Override
    public byte[] signByteArray(byte[] xmlData) {
        LOG.info("Java API Signer used");
        byte[] signedByteArray = null;

        // Create a DOM XMLSignatureFactory that will be used to generate the
        // enveloped signature

        try {
            var fac = XMLSignatureFactory.getInstance("DOM");

            // Load KeyPair from Java Key Store
            var kp = getKeyPairFromKeyStore(javaKeyStorePath,
                                            javaKeyStorePassword, alias, keyPassword
            );

            // Create a KeyValue containing the PublicKey that was generated
            var keyInfoFactory = fac.getKeyInfoFactory();
            KeyValue keyValue;

            keyValue = keyInfoFactory.newKeyValue(kp.getPublic());

            // Instantiate the document to be signed
            var builderFactory = DocumentBuilderFactory.newInstance();
            try {
                builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            builderFactory.setNamespaceAware(true);
            Document doc;
            doc = builderFactory.newDocumentBuilder().parse(
                new ByteArrayInputStream(xmlData));

            // Create a DOMSignContext and specify the PrivateKey and
            // location of the resulting XMLSignature's parent element
            var dsc = new DOMSignContext(
                kp.getPrivate(),
                doc.getDocumentElement()
            );

            // Create a Reference to the enveloped document (in this case we are
            // signing the whole document, so a URI of "" signifies that) and
            // also specify the SHA1 digest algorithm and the ENVELOPED
            // Transform.
            var ref = fac.newReference(
                "",
                fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(
                    fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null
                    )), null, null
            );

            // Create the SignedInfo
            var si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(
                    CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                    (C14NMethodParameterSpec) null
                ),
                fac.newSignatureMethod(
                    SignatureMethod.RSA_SHA1, null
                ),
                Collections.singletonList(ref)
            );

            // Create a KeyInfo and add the KeyValue to it
            var keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(keyValue));
            // Create the XMLSignature (but don't sign it yet)
            var signature = fac.newXMLSignature(si, keyInfo);

            // Marshal, generate (and sign) the enveloped signature
            signature.sign(dsc);

            var bos = new ByteArrayOutputStream();

            var transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            var transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(bos));

            signedByteArray = bos.toByteArray();
        } catch (KeyException | SAXException | IOException | ParserConfigurationException
                 | TransformerException | MarshalException | XMLSignatureException
                 | NoSuchAlgorithmException | InvalidAlgorithmParameterException e1) {
            LOGGER.error("Cannot signByteArray due", e1);
        }

        return signedByteArray;
    }

    @SuppressWarnings("squid:S1135")
    @Override
    public boolean verifySignature(byte[] xmlData) {
        // TODO Auto-generated method stub
        return false;
    }
}
