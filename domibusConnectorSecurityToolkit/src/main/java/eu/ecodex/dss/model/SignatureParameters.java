/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/SignatureParameters.java $
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
import lombok.Getter;

/**
 * Holds attributes to create a signature. This is a simple POJO - no checks are done.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@SuppressWarnings("squid:S1135")
@Getter
public class SignatureParameters {
    /**
     * The connection to the signatureToken holder KeyStore, SmartCard, ...
     *  TODO: maybe this should be a factory, because SignatureTokenConnection is closeable...
     */
    private SignatureTokenConnection signatureTokenConnection;
    /**
     * The private key of the signer.
     */
    private DSSPrivateKeyEntry privateKey;
    /**
     * The algorithm used for signing; business-wise mandatory (but not in this pojo).
     */
    private EncryptionAlgorithm encryptionAlgorithm;
    /**
     * The algorithm used for the digest; business-wise mandatory (but not in this pojo).
     */
    private DigestAlgorithm digestAlgorithm;

    /**
     * Returns the certificate; used for getting the identity of the signer; business-wise mandatory
     * (but not in this pojo).
     *
     * @return the value
     * @deprecated This method is deprecated and may be removed in a future release. Use
     *      getPrivateKey().getCertificate() instead.
     */
    @Deprecated
    public CertificateToken getCertificate() {
        return privateKey.getCertificate();
    }

    /**
     * Sets the private key of the signer.
     *
     * @param privateKey the value
     * @return this class' instance for chaining
     */
    public SignatureParameters setPrivateKey(final DSSPrivateKeyEntry privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    /**
     * The chain of certificates from the signer up to his root.
     *
     * @return the certificate chain as a List of CertificateToken objects
     * @deprecated Use getPrivateKey().getCertificateChain() instead.
     */
    @Deprecated // use getPrivateKey().getCertificateChain() instead!
    public List<CertificateToken> getCertificateChain() {
        return Arrays.asList(getPrivateKey().getCertificateChain());
    }

    /**
     * The algorithm used for signing; business-wise mandatory (but not in this pojo).
     *
     * @param encryptionAlgorithm the value
     * @return this class' instance for chaining
     */
    public SignatureParameters setEncryptionAlgorithm(
        final EncryptionAlgorithm encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
        return this;
    }

    /**
     * The algorithm used for the digest; business-wise mandatory (but not in this pojo).
     *
     * @param digestAlgorithm the value
     * @return this class' instance for chaining
     */
    public SignatureParameters setDigestAlgorithm(final DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
        return this;
    }

    public SignatureParameters setSignatureTokenConnection(
        SignatureTokenConnection signatureTokenConnection) {
        this.signatureTokenConnection = signatureTokenConnection;
        return this;
    }
}
