/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.service;

import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.common.service.DCKeyStoreService.CannotLoadKeyStoreException;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * This class represents a service for loading and extracting information from a keystore.
 */
@Service("webKeystoreService")
@SuppressWarnings("squid:S1135")
public class WebKeystoreService {
    private final DCKeyStoreService dcKeyStoreService;

    public WebKeystoreService(DCKeyStoreService dcKeyStoreService) {
        this.dcKeyStoreService = dcKeyStoreService;
    }

    /**
     * Represents information about a certificate, including its alias, subject, issuer, validity
     * period, algorithm, and type.
     */
    @Data
    public class CertificateInfo {
        private String alias;
        private String subject;
        private String issuer;
        private Date notBefore;
        private Date notAfter;
        private String algorithm;
        private String type;

        /**
         * Represents information about a certificate, including its alias, subject, issuer,
         * validity period, algorithm, and type.
         *
         * @param alias     the alias of the certificate
         * @param subject   the subject of the certificate
         * @param issuer    the issuer of the certificate
         * @param notBefore the start date of the certificate's validity period
         * @param notAfter  the end date of the certificate's validity period
         * @param algorithm the signing algorithm used for the certificate
         * @param type      the type of the certificate (e.g., keypair, public, private)
         */
        public CertificateInfo(
            String alias, String subject, String issuer, Date notBefore, Date notAfter,
            String algorithm, String type) {
            super();
            this.alias = alias;
            this.setSubject(subject);
            this.setIssuer(issuer);
            this.notBefore = notBefore;
            this.notAfter = notAfter;
            this.setAlgorithm(algorithm);
            this.setType(type);
        }
    }

    public KeyStore loadKeyStore(StoreConfigurationProperties storeConfigurationProperties) {
        return dcKeyStoreService.loadKeyStore(storeConfigurationProperties);
    }

    /**
     * Loads keystore by, the assumed type is JKS.
     *
     * @param path     path
     * @param password password
     * @return the loaded keystore
     * @deprecated The method {@link #loadKeyStore(StoreConfigurationProperties)} should be used
     *      instead
     */
    @Deprecated
    public KeyStore loadKeyStore(String path, String password) {
        var storeConfigurationProperties = new StoreConfigurationProperties();
        storeConfigurationProperties.setPath(path);
        storeConfigurationProperties.setPassword(password);
        return dcKeyStoreService.loadKeyStore(storeConfigurationProperties);
    }

    /**
     * Loads a KeyStore from the given input stream with the specified password.
     *
     * @param is       The input stream of the KeyStore.
     * @param password The password to access the KeyStore. If null, an empty string is used.
     * @return The loaded KeyStore.
     * @throws CannotLoadKeyStoreException If there is an error loading the KeyStore.
     */
    public KeyStore loadKeyStore(InputStream is, String password) {
        if (password == null) {
            password = "";
        }

        char[] pwdArray = password.toCharArray();

        try {
            var keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, pwdArray);
            return keyStore;
        } catch (NoSuchAlgorithmException | CertificateException | IOException
                 | KeyStoreException e) {
            throw new CannotLoadKeyStoreException("Cannot load key store!", e);
        }
    }

    /**
     * Loads store certificates information from a keystore file.
     *
     * @param path     The path of the keystore file.
     * @param password The password to access the keystore.
     * @return A list of CertificateInfo objects representing the certificates in the keystore.
     * @deprecated This method is deprecated and should not be used. Use the method
     *      {@link #loadStoreCertificatesInformation(StoreConfigurationProperties)} instead, which
     *      allows loading the keystore from a configuration object.
     */
    @Deprecated
    public List<CertificateInfo> loadStoreCertificatesInformation(String path, String password) {
        var keyStore = loadKeyStore(path, password);
        return loadStoreCertificatesInformation(keyStore);
    }

    public List<CertificateInfo> loadStoreCertificatesInformation(
        StoreConfigurationProperties storeConfigurationProperties) {
        var keyStore = loadKeyStore(storeConfigurationProperties);
        return loadStoreCertificatesInformation(keyStore);
    }

    public List<CertificateInfo> loadStoreCertificatesInformation(InputStream is, String password) {
        var keyStore = loadKeyStore(is, password);
        return loadStoreCertificatesInformation(keyStore);
    }

    private List<CertificateInfo> loadStoreCertificatesInformation(KeyStore keyStore) {
        List<CertificateInfo> certsInfo = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                var certificate = keyStore.getCertificate(alias);
                String subject = null;
                String issuer = null;
                Date notBefore = null;
                Date notAfter = null;
                String algorithm = null;
                var type = "undefined";
                if (certificate instanceof X509Certificate x509Certificate) {
                    subject = x509Certificate.getSubjectX500Principal().getName();
                    issuer = x509Certificate.getIssuerX500Principal().getName();
                    notBefore = x509Certificate.getNotBefore();
                    notAfter = x509Certificate.getNotAfter();
                    algorithm = x509Certificate.getSigAlgName();
                    if (keyStore.isKeyEntry(alias) && keyStore.isCertificateEntry(alias)) {
                        type = "keypair";
                    } else if (keyStore.isCertificateEntry(alias)) {
                        type = "public";
                    } else if (keyStore.isKeyEntry(alias)) {
                        type = "private";
                    }
                }
                certsInfo.add(
                    new CertificateInfo(
                        alias, subject, issuer, notBefore, notAfter, algorithm, type
                    ));
            }
            return certsInfo;
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }
}
