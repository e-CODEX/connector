/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.configurations;

import eu.ecodex.dss.model.SignatureParameters;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import java.io.FileInputStream;
import java.security.KeyStore;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * This class provides methods to generate different variants of signature parameters. The variants
 * include different combinations of private key, certificate, certificate chain, digest algorithm,
 * and encryption algorithm.
 *
 * <p>The class also provides several utility methods to configure the signature parameters.
 * Each variant is represented by a static method that returns a SignatureParameters object with the
 * specified configuration. If an exception occurs during the configuration process, an error
 * message is printed to the standard error output and null is returned.
 *
 * <p>Note that some of the configurations are marked as deprecated, indicating that they are no
 * longer supported or possible to configure.
 *
 * <p>The class contains several private static final fields that are used in the configuration
 * process: - JKS_KEYSTORE_PATH: The path to the JKS keystore file. - JKS_KEYSTORE_PASSWORD: The
 * password for the JKS keystore. - JKS_KEY_NAME: The name of the key in the JKS keystore. -
 * JKS_KEY_PASSWORD: The password for the key in the JKS keystore.
 *
 * @deprecated This class is deprecated and should not be used anymore.
 */
// SUB-CONF-02
@SuppressWarnings("checkstyle:TypeName")
public class InvalidConfig_SignatureParameters {
    private static final Resource JKS_KEYSTORE_PATH =
        new ClassPathResource("keystores/signature_store.jks");
    private static final KeyStore.PasswordProtection JKS_KEYSTORE_PASSWORD =
        new KeyStore.PasswordProtection("teststore".toCharArray());
    private static final String JKS_KEY_NAME = "sign_key";
    private static final KeyStore.PasswordProtection JKS_KEY_PASSWORD =
        new KeyStore.PasswordProtection("teststore".toCharArray());

    /**
     * Returns a SignatureParameters object with no private key set.
     *
     * <p>The returned object represents the parameters required to create a digital signature.
     * The private key is not set in this method.
     *
     * @return a SignatureParameters object without a private key
     */
    // SUB-CONF-02 Variant 1
    // No Private Key
    public static SignatureParameters get_SignatureParameters_NoPrivateKey() {
        FileInputStream fileInputStream = null;

        try {
            var sigParam = new SignatureParameters();
            sigParam.setPrivateKey(null);
            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA1);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {

            System.err.println(
                "Exception within the configuration of the signature parameters - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    /**
     * This method is deprecated and no longer possible to configure. It returns a
     * SignatureParameters object with no certificate set. The returned object represents the
     * parameters required to create a digital signature. The private key, digest algorithm, and
     * encryption algorithm are set in this method.
     *
     * @return a SignatureParameters object without a certificate
     */
    // SUB-CONF-02 Variant 2
    // No Certificate
    @Deprecated // this config is not possible anymore...
    public static SignatureParameters get_SignatureParameters_NoCertificate() {
        FileInputStream fileInputStream = null;
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

            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA1);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the signature parameters - Variant 2:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    /**
     * Retrieves a SignatureParameters object representing the parameters required to create a
     * digital signature, with no certificate chain. This method is deprecated and no longer
     * possible to configure. The private key, digest algorithm, and encryption algorithm are set in
     * this method.
     *
     * @return a SignatureParameters object without a certificate chain
     * @deprecated This configuration is no longer supported
     */
    // SUB-CONF-02 Variant 3
    // No Certificate Chain
    @Deprecated // this config is not possible anymore
    public static SignatureParameters get_SignatureParameters_NoCertificateChain() {
        FileInputStream fileInputStream = null;
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

            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA1);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {

            System.err.println(
                "Exception within the configuration of the signature parameters - Variant 3:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    /**
     * Retrieves a SignatureParameters object representing the parameters required to create a
     * digital signature, with no digest algorithm provided. This method is used to configure
     * Variant 4 of the SUB-CONF-02 configuration.
     *
     * @return a SignatureParameters object without a digest algorithm
     */
    // SUB-CONF-02 Variant 4
    // No Digest Algorithm
    public static SignatureParameters get_SignatureParameters_NoDigestAlgorithm() {
        FileInputStream fileInputStream = null;
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

            sigParam.setDigestAlgorithm(null);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {

            System.err.println(
                "Exception within the configuration of the signature parameters - Variant 4:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    /**
     * Retrieves a SignatureParameters object representing the parameters required to create a
     * digital signature, with no encryption algorithm provided.
     *
     * @return a SignatureParameters object without an encryption algorithm
     */
    // SUB-CONF-02 Variant 5
    // No Signature Algorithm
    public static SignatureParameters get_SignatureParameters_NoEncryptionAlgorithm() {
        FileInputStream fileInputStream = null;
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

            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA1);
            sigParam.setEncryptionAlgorithm(null);

            return sigParam;
        } catch (Exception e) {

            System.err.println(
                "Exception within the configuration of the signature parameters - Variant 5:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }
}
