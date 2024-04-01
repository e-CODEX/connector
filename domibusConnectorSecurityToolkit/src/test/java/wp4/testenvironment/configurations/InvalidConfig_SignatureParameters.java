package wp4.testenvironment.configurations;

import eu.ecodex.dss.model.SignatureParameters;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.security.KeyStore;


// SUB-CONF-02
public class InvalidConfig_SignatureParameters {
    private static final Resource JKS_KEYSTORE_PATH = new ClassPathResource("keystores/signature_store.jks");
    private static final KeyStore.PasswordProtection JKS_KEYSTORE_PASSWORD =
            new KeyStore.PasswordProtection("teststore".toCharArray());
    private static final String JKS_KEY_NAME = "sign_key";
    private static final KeyStore.PasswordProtection JKS_KEY_PASSWORD =
            new KeyStore.PasswordProtection("teststore".toCharArray());

    // SUB-CONF-02 Variant 1
    // No Private Key
    public static SignatureParameters get_SignatureParameters_NoPrivateKey() {
        FileInputStream kfis = null;
        try {
            SignatureParameters sigParam = new SignatureParameters();

            //			KeyStore ks = KeyStore.getInstance("JKS");
            //			kfis = new FileInputStream(JKS_KEYSTORE_PATH);
            //			ks.load(kfis, JKS_KEYSTORE_PASSWORD.toCharArray());

            sigParam.setPrivateKey(null);

            //			X509Certificate cert = (X509Certificate) ks.getCertificate(JKS_KEY_NAME);
            //			CertificateToken tkn = new CertificateToken(cert);
            //
            //			sigParam.setCertificate(tkn);

            //			Certificate[] certs = ks.getCertificateChain(JKS_KEY_NAME);

            //			final List<CertificateToken> x509Certs = new ArrayList<CertificateToken>();
            //
            //			for (final Certificate certificate : certs) {
            //				if (certificate instanceof X509Certificate) {
            //					CertificateToken chainCert = new CertificateToken((X509Certificate) certificate);
            //			    	x509Certs.add(chainCert);
            //				}
            //			}
            //
            //			sigParam.setCertificateChain(x509Certs);

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

    // SUB-CONF-02 Variant 2
    // No Certificate
    @Deprecated // this config is not possible anymore...
    public static SignatureParameters get_SignatureParameters_NoCertificate() {
        FileInputStream kfis = null;

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

            System.err.println("Exception within the configuration of the signature parameters - Variant 2:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(kfis);
        }
    }

    // SUB-CONF-02 Variant 3
    // No Certificate Chain
    @Deprecated // this config is not possible anymore
    public static SignatureParameters get_SignatureParameters_NoCertificateChain() {
        FileInputStream kfis = null;

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

            System.err.println("Exception within the configuration of the signature parameters - Variant 3:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(kfis);
        }
    }

    // SUB-CONF-02 Variant 4
    // No Digest Algorithm
    public static SignatureParameters get_SignatureParameters_NoDigestAlgorithm() {
        FileInputStream kfis = null;

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

            sigParam.setDigestAlgorithm(null);
            sigParam.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);

            return sigParam;
        } catch (Exception e) {

            System.err.println("Exception within the configuration of the signature parameters - Variant 4:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(kfis);
        }
    }

    // SUB-CONF-02 Variant 5
    // No Signature Algorithm
    public static SignatureParameters get_SignatureParameters_NoEncryptionAlgorithm() {
        FileInputStream kfis = null;

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
            sigParam.setEncryptionAlgorithm(null);

            return sigParam;
        } catch (Exception e) {

            System.err.println("Exception within the configuration of the signature parameters - Variant 5:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(kfis);
        }
    }
}
