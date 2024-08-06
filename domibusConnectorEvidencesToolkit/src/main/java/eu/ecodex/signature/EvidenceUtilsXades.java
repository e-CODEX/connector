/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.ecodex.signature;

import static eu.domibus.connector.tools.logging.LoggingUtils.logPassword;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.XAdESTimestampParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

/**
 * Utility class for XAdES-based evidence operations.
 */
@SuppressWarnings("squid:S1135")
public class EvidenceUtilsXades extends EvidenceUtils {
    private static final Logger LOGGER = LogManager.getLogger(EvidenceUtilsXades.class);

    public EvidenceUtilsXades(
        Resource javaKeyStorePath, String javaKeyStoreType, String javaKeyStorePassword,
        String alias, String keyPassword) {
        super(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
    }

    @Override
    public byte[] signByteArray(byte[] xmlData) {

        byte[] signedData = null;
        try {
            signedData = createAndVerifySignature(xmlData);
        } catch (Exception e) {
            LOGGER.error("Cannot createAndVerifySignature", e);
        }

        return signedData;
    }

    private byte[] createAndVerifySignature(byte[] xmlData) throws Exception {
        LOG.info("Xades Signer started");

        var keyInfos = getKeyInfosFromKeyStore(
            javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
        );
        if (keyInfos == null) {
            throw new RuntimeException(String.format(
                "Was not able to load keyInfo from javaKeyStorePath=[%s], javaKeyStoreType=[%s], "
                    + "javaKeyStorePassword=[%s], alias=[%s], keyPassword=[%s]",
                javaKeyStorePath, javaKeyStoreType, logPassword(LOGGER, javaKeyStorePassword
                ),
                alias, logPassword(LOGGER, keyPassword)
            ));
        }

        java.security.cert.X509Certificate cert = keyInfos.getCert();

        var sigParam = new XAdESSignatureParameters();
        sigParam.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        sigParam.setSignaturePackaging(SignaturePackaging.ENVELOPED);

        var tkn = new CertificateToken(cert);
        sigParam.setSigningCertificate(tkn);

        final List<CertificateToken> x509Certs = new ArrayList<>();
        final List<X509Certificate> certs = keyInfos.getCertChain();
        for (final Certificate certificate : certs) {
            if (certificate instanceof X509Certificate x509Certificate) {
                // ChainCertificate chainCert =
                // new ChainCertificate(new CertificateToken((X509Certificate) certificate));
                x509Certs.add(new CertificateToken(x509Certificate));
            } else {
                LOG.warn(
                    "the alias {} has a certificate chain item that does not represent an "
                        + "X509Certificate; it is ignored");
            }
        }
        sigParam.setCertificateChain(x509Certs);

        sigParam.setDigestAlgorithm(DigestAlgorithm.SHA1);
        sigParam.setEncryptionAlgorithm(sigParam.getEncryptionAlgorithm());

        // Signature creation
        // Create and configure the signature creation service
        var service = new XAdESService(new CommonCertificateVerifier(true));
        var docum = new InMemoryDocument(xmlData);

        ToBeSigned bytesToSign = service.getDataToSign(docum, sigParam);

        final String jceSignatureAlgorithm = sigParam.getSignatureAlgorithm().getJCEId();

        // final byte[] signature =
        // DSSUtils.encrypt(jceSignatureAlgorithm, pke.getPrivateKey(), bytesToSign);

        // InMemoryDocument signedDocument =
        // (InMemoryDocument) service.signDocument(docum, sigParam, signature);
        final var signature = Signature.getInstance(jceSignatureAlgorithm);
        var privKey = keyInfos.getPrivKey();
        java.security.cert.Certificate[] certArray = {cert};
        var privKeyEntry = new PrivateKeyEntry(privKey, certArray);
        // KSPrivateKeyEntry pke = new KSPrivateKeyEntry("", privKeyEntry);
        signature.initSign(privKeyEntry.getPrivateKey());
        signature.update(bytesToSign.getBytes());
        final byte[] signatureValue = signature.sign();
        final var signedData = new SignatureValue(sigParam.getSignatureAlgorithm(), signatureValue);

        DocumentSignatureService<XAdESSignatureParameters, XAdESTimestampParameters>
            signatureService = new XAdESService(getCompleteCertificateVerifier());
        DSSDocument toBeSigned = new InMemoryDocument(xmlData);
        final DSSDocument signedDocument =
            signatureService.signDocument(toBeSigned, sigParam, signedData);

        // Verification
        var val = SignedDocumentValidator.fromDocument(signedDocument);
        var certVeri = new CommonCertificateVerifier(true);
        val.setCertificateVerifier(certVeri);

        Reports test = val.validateDocument();
        boolean sigValid = test.getDiagnosticData()
                               .getSignatureById(test.getDiagnosticData().getFirstSignatureId())
                               .isSignatureIntact();
        boolean sigIntact = test.getDiagnosticData()
                                .getSignatureById(test.getDiagnosticData().getFirstSignatureId())
                                .isSignatureIntact();

        LOG.info("Signature applied to document. Validationresult: Signature Valid: "
                     + sigValid
                     + " / Signature Intact: "
                     + sigIntact);

        return getBytes(signedDocument.openStream());
    }

    @Override
    public boolean verifySignature(byte[] xmlData) {
        var signedDocument = new InMemoryDocument(xmlData);

        SignedDocumentValidator val;

        val = SignedDocumentValidator.fromDocument(signedDocument);
        var certVeri = new CommonCertificateVerifier(true);
        val.setCertificateVerifier(certVeri);
        Reports test = val.validateDocument();

        boolean sigValid = test.getDiagnosticData()
                               .getSignatureById(test.getDiagnosticData().getFirstSignatureId())
                               .isSignatureIntact();
        boolean sigIntact = test.getDiagnosticData()
                                .getSignatureById(test.getDiagnosticData().getFirstSignatureId())
                                .isSignatureIntact();

        LOG.info("Signature applied to document. Validationresult: Signature Valid: "
                     + sigValid
                     + " / Signature Intact: "
                     + sigIntact);

        return sigValid && sigIntact;
    }

    private static synchronized byte[] getBytes(InputStream is) throws IOException {

        int len;
        var size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            // Do not remove below line
            len = is.read(buf, 0, size);
        } else {
            var bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, len);
            }
            buf = bos.toByteArray();
        }
        return buf;
    }

    // TODO: refactor: use key store service
    protected static synchronized KeyInfos getKeyInfosFromKeyStore(
        Resource store, String keyStoreType, String storePass, String alias, String keyPass) {
        LOG.debug("Loading KeyPair from Java KeyStore(" + store + ")");
        KeyStore ks;
        InputStream kfis = null;
        var keyInfos = new KeyInfos();

        Key key;
        PrivateKey privateKey;
        try {
            ks = KeyStore.getInstance(keyStoreType);

            kfis = store.getInputStream();
            ks.load(kfis, (storePass == null) ? null : storePass.toCharArray());

            if (ks.containsAlias(alias)) {
                key = ks.getKey(alias, keyPass.toCharArray());
                if (key instanceof PrivateKey) {
                    X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

                    keyInfos.setCert(cert);
                    privateKey = (PrivateKey) key;
                    keyInfos.setPrivKey(privateKey);
                } else {
                    throw new IllegalArgumentException(
                        String.format("The provided alias [%s] in store [%s] is not a private key",
                                      alias, store
                        ));
                }

                try {
                    keyInfos.setCertChain(ks.getCertificateChain(alias));
                } catch (ClassCastException e) {
                    LOG.error(e.getMessage());
                }
            } else {
                throw new RuntimeException(
                    String.format(
                        "The provided store [%s] does not contain an alias [%s]", store, alias
                    ));
            }
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
                 | CertificateException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(kfis);
        }

        return keyInfos;
    }

    protected CertificateVerifier getCompleteCertificateVerifier() {
        // cv.setDataLoader(getFileCacheDataLoader());
        // cv.setCrlSource(onlineCrlSource());
        // cv.setOcspSource(onlineOcspSource());
        // cv.setTrustedCertSource(getTrustedCertificateSource());
        return new CommonCertificateVerifier(true);
    }

    private DataLoader getFileCacheDataLoader() {
        var cacheDataLoader = new FileCacheDataLoader();
        var dataLoader = new CommonsDataLoader();
        // dataLoader.setProxyConfig(getProxyConfig());
        cacheDataLoader.setDataLoader(dataLoader);
        // cacheDataLoader.setFileCacheDirectory(new File("target"));
        cacheDataLoader.setCacheExpirationTime(3600000L);
        return cacheDataLoader;
    }
}
