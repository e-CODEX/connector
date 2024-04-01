/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss/SigningUtil
 * .java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.SignatureParameters;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.PAdESTimestampParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.XAdESTimestampParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;

import java.util.Date;


/**
 * a utility class used for signing documents in specific flavours
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
class SigningUtil {
    /**
     * utility constructor
     */
    private SigningUtil() {
    }

    /**
     * signs a document with an ASiC_S_BES/DETACHED signature
     *
     * @param signingParameters the signing parameters (algorithms, certificates and private key)
     * @param document          the to be signed document
     * @return a new document based on the input and signed
     * @throws java.security.NoSuchAlgorithmException from the underlying classes
     */
    static DSSDocument signASiC(
            final SignatureParameters signingParameters,
            final DSSDocument document) throws Exception {
        final CertificateVerifier certificateVerifier =
                new CommonCertificateVerifier(true); // TODO: replace with FACTORY!!
        final ASiCWithXAdESSignatureParameters params = new ASiCWithXAdESSignatureParameters();
        params.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        params.setSignaturePackaging(SignaturePackaging.DETACHED);
        params.aSiC().setContainerType(ASiCContainerType.ASiC_S);
        params.bLevel().setSigningDate(new Date());
        params.setCertificateChain(signingParameters.getPrivateKey().getCertificateChain());
        params.setSigningCertificate(signingParameters.getPrivateKey().getCertificate());

        final DocumentSignatureService<ASiCWithXAdESSignatureParameters, XAdESTimestampParameters> signatureService =
                new ASiCWithXAdESService(certificateVerifier);

        ToBeSigned bytesToSign = signatureService.getDataToSign(document, params);

        SignatureValue signatureValue = signingParameters.getSignatureTokenConnection().sign(
                bytesToSign,
                signingParameters.getDigestAlgorithm(),
                signingParameters.getPrivateKey()
        );

        final DSSDocument signedDocument = signatureService.signDocument(document, params, signatureValue);

        return signedDocument;
    }

    /**
     * signs a document with an PAdES_BES/ENVELOPED signature
     *
     * @param signingParameters the signing parameters (algorithms, certificates and private key)
     * @param document          the to be signed document
     * @return a new document based on the input and signed
     * @throws java.security.NoSuchAlgorithmException from the underlying classes
     */
    static DSSDocument signPAdES(
            final SignatureParameters signingParameters,
            final DSSDocument document) throws Exception {

        final CertificateVerifier certificateVerifier =
                new CommonCertificateVerifier(true); // TODO: replace with FACTORY!!
        final DocumentSignatureService<PAdESSignatureParameters, PAdESTimestampParameters> signatureService =
                new PAdESService(certificateVerifier);

        final EncryptionAlgorithm encryptionAlgorithm = signingParameters.getEncryptionAlgorithm();
        final DigestAlgorithm digestAlgorithm = signingParameters.getDigestAlgorithm();

        final PAdESSignatureParameters params = new PAdESSignatureParameters();

        params.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        params.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        params.setEncryptionAlgorithm(encryptionAlgorithm);
        params.setDigestAlgorithm(digestAlgorithm);
        params.setCertificateChain(signingParameters.getCertificateChain());
        params.setSigningCertificate(signingParameters.getCertificate());

        ToBeSigned bytesToSign = signatureService.getDataToSign(document, params);

        SignatureValue signedData = signingParameters
                .getSignatureTokenConnection()
                .sign(bytesToSign, signingParameters.getDigestAlgorithm(), signingParameters.getPrivateKey());

        final DSSDocument signedDocument = signatureService.signDocument(document, params, signedData);

        return signedDocument;
    }

    /**
     * signs a document with an XAdES_BES signature with the packaging in parameter
     *
     * @param signingParameters  the signing parameters (algorithms, certificates and private key)
     * @param document           the to be signed document
     * @param signaturePackaging the packaging (ENVELOPED or DETACHED)
     * @return a new document based on the input and signed
     * @throws java.security.NoSuchAlgorithmException from the underlying classes
     */
    static DSSDocument signXAdES(
            final SignatureParameters signingParameters,
            final DSSDocument document,
            final SignaturePackaging signaturePackaging) throws Exception {

        final CertificateVerifier certificateVerifier =
                new CommonCertificateVerifier(true); // TODO: replace with FACTORY!!
        final XAdESService signatureService = new XAdESService(certificateVerifier);

        final XAdESSignatureParameters params = new XAdESSignatureParameters();
        params.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        params.setSignaturePackaging(signaturePackaging);
        params.setEncryptionAlgorithm(signingParameters.getEncryptionAlgorithm());
        params.setDigestAlgorithm(signingParameters.getDigestAlgorithm());
        params.setCertificateChain(signingParameters.getCertificateChain());
        params.setSigningCertificate(signingParameters.getCertificate());
        params.bLevel().setSigningDate(new Date());
        // params.setEmbedXML(true);

        ToBeSigned bytesToSign = signatureService.getDataToSign(document, params);

        SignatureTokenConnection signatureTokenConnection = signingParameters.getSignatureTokenConnection();

        SignatureValue signedData = signatureTokenConnection
                .sign(bytesToSign, signingParameters.getDigestAlgorithm(), signingParameters.getPrivateKey());

        final DSSDocument signedDocument = signatureService.signDocument(document, params, signedData);

        return signedDocument;
    }
}
