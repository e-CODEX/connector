/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.configurations;

import eu.ecodex.dss.model.SignatureParameters;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import java.io.InputStream;
import java.security.KeyStore;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * This class can be used to receive valid configurations of Connector Certificates.
 *
 * <p>The respective test case is SUB_CONF_01
 */
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_SignatureParameters {
    private static final Resource JKS_KEYSTORE_PATH =
        new ClassPathResource("/keystores/signature_store.jks");
    private static final KeyStore.PasswordProtection JKS_KEYSTORE_PASSWORD =
        new KeyStore.PasswordProtection("teststore".toCharArray());
    private static final String JKS_KEY_NAME = "sign_key";
    private static final KeyStore.PasswordProtection JKS_KEY_PASSWORD =
        new KeyStore.PasswordProtection("teststore".toCharArray());
    private static final Resource PKCS12_KEYSTORE_PATH =
        new ClassPathResource("keystores/signature_store.p12");
    private static final KeyStore.PasswordProtection PKCS12_KEYSTORE_PASSWORD =
        new KeyStore.PasswordProtection("teststore".toCharArray());
    private static final String PKCS12_KEY_NAME = "sign_key";
    private static final KeyStore.PasswordProtection PKCS12_KEY_PASSWORD =
        new KeyStore.PasswordProtection("teststore".toCharArray());

    /**
     * Returns a "SignatureParameters" object containing - An accessible private key - The
     * respective certificate - The respective certificate chain - The digest algorithm being set to
     * SHA1 - The encryption algorithm being set to RSA.
     *
     * <p>The information has been set using standard java classes
     *
     * <p>The respective test case is SUB_CONF_01 - Variant 1
     */
    public static SignatureParameters getJKSConfiguration() {
        InputStream kfis = null;

        try {
            var sigParam = new SignatureParameters();
            KeyStoreSignatureTokenConnection keyStoreSignatureTokenConnection =
                new KeyStoreSignatureTokenConnection(
                    JKS_KEYSTORE_PATH.getInputStream(),
                    "JKS",
                    JKS_KEYSTORE_PASSWORD
                );
            sigParam.setSignatureTokenConnection(keyStoreSignatureTokenConnection);

            var key = keyStoreSignatureTokenConnection.getKey(JKS_KEY_NAME, JKS_KEY_PASSWORD);
            sigParam.setPrivateKey(key);
            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA1);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the signature parameters - Variant 1:"
            );
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(kfis);
        }
    }

    /**
     * Returns a "SignatureParameters" object containing - An accessible private key - The
     * respective certificate - The respective certificate chain - The digest algorithm being set to
     * SHA1 - The encryption algorithm being set to RSA.
     *
     * <p>The information has been set using standard java classes
     *
     * <p>The respective test case is SUB_CONF_01 - Variant 2
     */
    public static SignatureParameters getPKCS12Configuration() {
        InputStream kfis = null;
        try {
            var sigParam = new SignatureParameters();
            var keyStoreSignatureTokenConnection = new KeyStoreSignatureTokenConnection(
                PKCS12_KEYSTORE_PATH.getInputStream(),
                "PKCS12",
                PKCS12_KEYSTORE_PASSWORD
            );
            sigParam.setSignatureTokenConnection(keyStoreSignatureTokenConnection);

            var key = keyStoreSignatureTokenConnection.getKey(PKCS12_KEY_NAME, PKCS12_KEY_PASSWORD);
            sigParam.setPrivateKey(key);
            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA1);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the signature parameters - Variant 1:"
            );
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(kfis);
        }
    }

    /**
     * Returns a "SignatureParameters" object containing created by a "SignatureParameterFactory"
     * object - An accessible private key - The respective certificate - The respective certificate
     * chain - The digest algorithm being set to SHA1 - The encryption algorithm being set to RSA.
     *
     * <p>The information has been set using standard java classes
     *
     * <p>The respective test case is SUB_CONF_01 - Variant 3
     */
    @Deprecated
    public static SignatureParameters getJKSConfig_By_SigParamFactory() {
        return new SignatureParameters();
    }

    /**
     * Returns a "SignatureParameters" object containing created by a "SignatureParameterFactory"
     * object - An accessible private key - The respective certificate - The respective certificate
     * chain - The digest algorithm being set to SHA1 - The encryption algorithm being set to RSA.
     *
     * <p>The information has been set using standard java classes
     *
     * <p>The respective test case is SUB_CONF_01 - Variant 4
     */
    @Deprecated
    public static SignatureParameters getPKCS12Config_By_SigParamFactory() {
        return new SignatureParameters();
    }

    /**
     * Returns a SignatureParameters object with the configuration for creating a signature using
     * the SHA256 digest algorithm.
     *
     * @return a SignatureParameters object configured with a signature token connection, private
     *      key, digest algorithm, and encryption algorithm
     */
    public static SignatureParameters getJKSConfiguration_SHA256() {
        try {
            var sigParam = new SignatureParameters();
            var keyStoreSignatureTokenConnection = new KeyStoreSignatureTokenConnection(
                JKS_KEYSTORE_PATH.getInputStream(),
                "JKS",
                JKS_KEYSTORE_PASSWORD
            );
            sigParam.setSignatureTokenConnection(keyStoreSignatureTokenConnection);

            var key = keyStoreSignatureTokenConnection.getKey(JKS_KEY_NAME, JKS_KEY_PASSWORD);
            sigParam.setPrivateKey(key);
            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA256);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the signature parameters - Variant 1:"
            );
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a {@code SignatureParameters} object with the configuration for creating a signature
     * using the SHA512 digest algorithm.
     *
     * <p>The returned object includes:</p>
     * <ul>
     *   <li>A {@code SignatureTokenConnection} object that represents the connection to the
     *   signature token holder (e.g., KeyStore, SmartCard)</li>
     *   <li>A private key of the signer</li>
     *   <li>The digest algorithm set to SHA512</li>
     *   <li>The encryption algorithm set to RSA</li>
     * </ul>
     *
     * <p>If an exception occurs during the configuration process, an error message is printed
     * and null is returned.
     *
     * @return a {@code SignatureParameters} object configured with a signature token connection,
     *      private key, digest algorithm, and encryption algorithm
     */
    public static SignatureParameters getJKSConfiguration_SHA512() {
        try {
            SignatureParameters sigParam = new SignatureParameters();

            var keyStoreSignatureTokenConnection = new KeyStoreSignatureTokenConnection(
                PKCS12_KEYSTORE_PATH.getInputStream(),
                "PKCS12",
                PKCS12_KEYSTORE_PASSWORD
            );
            sigParam.setSignatureTokenConnection(keyStoreSignatureTokenConnection);

            var key = keyStoreSignatureTokenConnection.getKey(PKCS12_KEY_NAME, PKCS12_KEY_PASSWORD);
            sigParam.setPrivateKey(key);
            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA512);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {

            System.err.println(
                "Exception within the configuration of the signature parameters - Variant 1:"
            );
            e.printStackTrace();
            return null;
        }
    }
}
