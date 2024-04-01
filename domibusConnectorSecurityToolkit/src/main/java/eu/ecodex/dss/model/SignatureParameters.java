/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/SignatureParameters
 * .java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.model;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;

import java.util.Arrays;
import java.util.List;


/**
 * Holds attributes to create a signature.
 * This is a simple POJO - no checks are done.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class SignatureParameters {
    /**
     * The connection to the signatureToken holder
     * KeyStore, SmartCard, ...
     *  TODO: maybe this should be a factory, because SignatureTokenConnection is closeable...
     */
    private SignatureTokenConnection signatureTokenConnection;
    /**
     * the private key of the signer
     */
    private DSSPrivateKeyEntry privateKey;
    /**
     * the algorithm used for signing; business-wise mandatory (but not in this pojo)
     */
    private EncryptionAlgorithm encryptionAlgorithm;
    /**
     * the algorithm used for the digest; business-wise mandatory (but not in this pojo)
     */
    private DigestAlgorithm digestAlgorithm;

    /**
     * returns the certificate; used for getting the identity of the signer; business-wise mandatory (but not in this
     * pojo)
     *
     * @return the value
     */
    @Deprecated
    public CertificateToken getCertificate() {
        return privateKey.getCertificate();
    }

    /**
     * sets the certificate; used for getting the identity of the signer; business-wise mandatory (but not in this
     * pojo)
     *
     * @param certificate the value
     * @return this class' instance for chaining
     */
    //	public SignatureParameters setCertificate(final CertificateToken certificate) {
    //		this.certificate = certificate;
    //		return this;
    //	}

    /**
     * returns the private key of the signer;
     *
     * @return the value
     */
    public DSSPrivateKeyEntry getPrivateKey() {
        return privateKey;
    }

    /**
     * sets the private key of the signer;
     *
     * @param privateKey the value
     * @return this class' instance for chaining
     */
    public SignatureParameters setPrivateKey(final DSSPrivateKeyEntry privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    /**
     * the chain of certificates from the signer up to his root;
     * use getPrivateKey().getCertificateChain() instead!
     *
     * @return the value
     */
    @Deprecated // use getPrivateKey().getCertificateChain() instead!
    public List<CertificateToken> getCertificateChain() {
        return Arrays.asList(getPrivateKey().getCertificateChain());
    }

    /**
     * the algorithm used for signing; business-wise mandatory (but not in this pojo)
     *
     * @return the value
     */
    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    /**
     * the algorithm used for signing; business-wise mandatory (but not in this pojo)
     *
     * @param encryptionAlgorithm the value
     * @return this class' instance for chaining
     */
    public SignatureParameters setEncryptionAlgorithm(final EncryptionAlgorithm encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
        return this;
    }

    /**
     * the algorithm used for the digest; business-wise mandatory (but not in this pojo)
     *
     * @return the value
     */
    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * the algorithm used for the digest; business-wise mandatory (but not in this pojo)
     *
     * @param digestAlgorithm the value
     * @return this class' instance for chaining
     */
    public SignatureParameters setDigestAlgorithm(final DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
        return this;
    }

    public SignatureTokenConnection getSignatureTokenConnection() {
        return signatureTokenConnection;
    }

    public SignatureParameters setSignatureTokenConnection(SignatureTokenConnection signatureTokenConnection) {
        this.signatureTokenConnection = signatureTokenConnection;
        return this;
    }
}
