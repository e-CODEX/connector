package wp4.testenvironment.configurations;

import eu.ecodex.dss.model.SignatureParameters;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.KeyStore;


/**
 * This class can be used to receive valid configurations of Connector Certificates.
 * <p>
 * The respective test case is SUB_CONF_01
 */
public class ValidConfig_SignatureParameters {
    private static final Resource JKS_KEYSTORE_PATH = new ClassPathResource("/keystores/signature_store.jks");
    private static final KeyStore.PasswordProtection JKS_KEYSTORE_PASSWORD =
            new KeyStore.PasswordProtection("teststore".toCharArray());
    private static final String JKS_KEY_NAME = "sign_key";
    private static final KeyStore.PasswordProtection JKS_KEY_PASSWORD =
            new KeyStore.PasswordProtection("teststore".toCharArray());

    private static final Resource PKCS12_KEYSTORE_PATH = new ClassPathResource("keystores/signature_store.p12");
    private static final KeyStore.PasswordProtection PKCS12_KEYSTORE_PASSWORD =
            new KeyStore.PasswordProtection("teststore".toCharArray());
    private static final String PKCS12_KEY_NAME = "sign_key";
    private static final KeyStore.PasswordProtection PKCS12_KEY_PASSWORD =
            new KeyStore.PasswordProtection("teststore".toCharArray());

    /**
     * Returns a "SignatureParameters" object containing
     * - An accessible private key
     * - The respective certificate
     * - The respective certificate chain
     * - The digest algorithm being set to SHA1
     * - The encryption algorithm being set to RSA
     * <p>
     * The information has been set using standard java classes
     * <p>
     * The respective test case is SUB_CONF_01 - Variant 1
     */
    public static SignatureParameters getJKSConfiguration() {
        InputStream kfis = null;

        try {
            SignatureParameters sigParam = new SignatureParameters();

            KeyStoreSignatureTokenConnection keyStoreSignatureTokenConnection = new KeyStoreSignatureTokenConnection(
                    JKS_KEYSTORE_PATH.getInputStream(),
                    "JKS",
                    JKS_KEYSTORE_PASSWORD
            );
            sigParam.setSignatureTokenConnection(keyStoreSignatureTokenConnection);

            DSSPrivateKeyEntry key = keyStoreSignatureTokenConnection.getKey(JKS_KEY_NAME, JKS_KEY_PASSWORD);
            sigParam.setPrivateKey(key);

            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA1);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {

            System.err.println("Exception within the configuration of the signature parameters - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(kfis);
        }
    }

    /**
     * Returns a "SignatureParameters" object containing
     * - An accessible private key
     * - The respective certificate
     * - The respective certificate chain
     * - The digest algorithm being set to SHA1
     * - The encryption algorithm being set to RSA
     * <p>
     * The information has been set using standard java classes
     * <p>
     * The respective test case is SUB_CONF_01 - Variant 2
     */
    public static SignatureParameters getPKCS12Configuration() {
        InputStream kfis = null;

        try {
            SignatureParameters sigParam = new SignatureParameters();

            KeyStoreSignatureTokenConnection keyStoreSignatureTokenConnection = new KeyStoreSignatureTokenConnection(
                    PKCS12_KEYSTORE_PATH.getInputStream(),
                    "PKCS12",
                    PKCS12_KEYSTORE_PASSWORD
            );
            sigParam.setSignatureTokenConnection(keyStoreSignatureTokenConnection);

            DSSPrivateKeyEntry key = keyStoreSignatureTokenConnection.getKey(PKCS12_KEY_NAME, PKCS12_KEY_PASSWORD);
            sigParam.setPrivateKey(key);

            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA1);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {

            System.err.println("Exception within the configuration of the signature parameters - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(kfis);
        }
    }

    /**
     * Returns a "SignatureParameters" object containing created by a "SignatureParameterFactory" object
     * - An accessible private key
     * - The respective certificate
     * - The respective certificate chain
     * - The digest algorithm being set to SHA1
     * - The encryption algorithm being set to RSA
     * <p>
     * The information has been set using standard java classes
     * <p>
     * The respective test case is SUB_CONF_01 - Variant 3
     *
     * @throws Exception
     */
    @Deprecated
    public static SignatureParameters getJKSConfig_By_SigParamFactory() throws Exception {
        SignatureParameters sigParam = new SignatureParameters();

        //		CertificateStoreInfo certStore = new CertificateStoreInfo();
        //		certStore.setLocation(JKS_KEYSTORE_PATH);
        //		certStore.setPassword(JKS_KEY_PASSWORD_STRING);

        //		sigParam = SignatureParametersFactory.create(certStore, JKS_KEY_NAME, JKS_KEY_PASSWORD_STRING,
        //		EncryptionAlgorithm.RSA, DigestAlgorithm.SHA1);

        return sigParam;
    }

    /**
     * Returns a "SignatureParameters" object containing created by a "SignatureParameterFactory" object
     * - An accessible private key
     * - The respective certificate
     * - The respective certificate chain
     * - The digest algorithm being set to SHA1
     * - The encryption algorithm being set to RSA
     * <p>
     * The information has been set using standard java classes
     * <p>
     * The respective test case is SUB_CONF_01 - Variant 4
     *
     * @throws Exception
     */
    @Deprecated
    public static SignatureParameters getPKCS12Config_By_SigParamFactory() throws Exception {
        SignatureParameters sigParam = new SignatureParameters();

        //		CertificateStoreInfo certStore = new CertificateStoreInfo();
        //		certStore.setLocation(PKCS12_KEYSTORE_PATH);
        //		certStore.setPassword(PKCS12_KEYSTORE_PASSWORD);
        //
        //		sigParam = SignatureParametersFactory.create(certStore, PKCS12_KEY_NAME, PKCS12_KEY_PASSWORD,
        //		EncryptionAlgorithm.RSA, DigestAlgorithm.SHA1);

        return sigParam;
    }

    public static SignatureParameters getJKSConfiguration_SHA256() {
        try {
            SignatureParameters sigParam = new SignatureParameters();

            KeyStoreSignatureTokenConnection keyStoreSignatureTokenConnection = new KeyStoreSignatureTokenConnection(
                    JKS_KEYSTORE_PATH.getInputStream(),
                    "JKS",
                    JKS_KEYSTORE_PASSWORD
            );
            sigParam.setSignatureTokenConnection(keyStoreSignatureTokenConnection);

            DSSPrivateKeyEntry key = keyStoreSignatureTokenConnection.getKey(JKS_KEY_NAME, JKS_KEY_PASSWORD);
            sigParam.setPrivateKey(key);

            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA256);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {

            System.err.println("Exception within the configuration of the signature parameters - Variant 1:");
            e.printStackTrace();
            return null;
        }
    }

    public static SignatureParameters getJKSConfiguration_SHA512() {
        try {
            SignatureParameters sigParam = new SignatureParameters();

            KeyStoreSignatureTokenConnection keyStoreSignatureTokenConnection = new KeyStoreSignatureTokenConnection(
                    PKCS12_KEYSTORE_PATH.getInputStream(),
                    "PKCS12",
                    PKCS12_KEYSTORE_PASSWORD
            );
            sigParam.setSignatureTokenConnection(keyStoreSignatureTokenConnection);

            DSSPrivateKeyEntry key = keyStoreSignatureTokenConnection.getKey(PKCS12_KEY_NAME, PKCS12_KEY_PASSWORD);
            sigParam.setPrivateKey(key);

            sigParam.setDigestAlgorithm(DigestAlgorithm.SHA512);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {

            System.err.println("Exception within the configuration of the signature parameters - Variant 1:");
            e.printStackTrace();
            return null;
        }
    }
}
