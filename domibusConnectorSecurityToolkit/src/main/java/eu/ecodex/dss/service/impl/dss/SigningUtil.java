/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss/SigningUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.SignatureParameters;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.PAdESTimestampParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.XAdESTimestampParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import java.util.Date;
import lombok.experimental.UtilityClass;

/**
 * A utility class used for signing documents in specific flavours.
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@SuppressWarnings("squid:S1135")
@UtilityClass
class SigningUtil {
    /**
     * Signs a document with an ASiC_S_BES/DETACHED signature.
     *
     * @param signingParameters the signing parameters (algorithms, certificates and private key)
     * @param document          the to be signed document
     * @return a new document based on the input and signed
     */
    static DSSDocument signASiC(
        final SignatureParameters signingParameters, final DSSDocument document) {
        // TODO: replace with FACTORY!!
        final CertificateVerifier certificateVerifier = new CommonCertificateVerifier(true);
        final var params = new ASiCWithXAdESSignatureParameters();
        params.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        params.setSignaturePackaging(SignaturePackaging.DETACHED);
        params.aSiC().setContainerType(ASiCContainerType.ASiC_S);
        params.bLevel().setSigningDate(new Date());
        params.setCertificateChain(signingParameters.getPrivateKey().getCertificateChain());
        params.setSigningCertificate(signingParameters.getPrivateKey().getCertificate());

        final DocumentSignatureService<ASiCWithXAdESSignatureParameters, XAdESTimestampParameters>
            signatureService = new ASiCWithXAdESService(certificateVerifier);

        ToBeSigned bytesToSign = signatureService.getDataToSign(document, params);

        var signatureValue = signingParameters
            .getSignatureTokenConnection()
            .sign(
                bytesToSign,
                signingParameters.getDigestAlgorithm(),
                signingParameters.getPrivateKey()
            );

        return signatureService.signDocument(document, params, signatureValue);
    }

    /**
     * Signs a document with an PAdES_BES/ENVELOPED signature.
     *
     * @param signingParameters the signing parameters (algorithms, certificates and private key)
     * @param document          the to be signed document
     * @return a new document based on the input and signed
     */
    static DSSDocument signPAdES(
        final SignatureParameters signingParameters, final DSSDocument document) {
        // TODO: replace with FACTORY!!
        final CertificateVerifier certificateVerifier = new CommonCertificateVerifier(true);
        final DocumentSignatureService<PAdESSignatureParameters, PAdESTimestampParameters>
            signatureService = new PAdESService(certificateVerifier);

        final var encryptionAlgorithm = signingParameters.getEncryptionAlgorithm();
        final var digestAlgorithm = signingParameters.getDigestAlgorithm();

        final var params = new PAdESSignatureParameters();

        params.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        params.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        params.setEncryptionAlgorithm(encryptionAlgorithm);
        params.setDigestAlgorithm(digestAlgorithm);
        params.setCertificateChain(signingParameters.getCertificateChain());
        params.setSigningCertificate(signingParameters.getCertificate());

        ToBeSigned bytesToSign = signatureService.getDataToSign(document, params);

        SignatureValue signedData = signingParameters
            .getSignatureTokenConnection()
            .sign(
                bytesToSign,
                signingParameters.getDigestAlgorithm(),
                signingParameters.getPrivateKey()
            );

        return signatureService.signDocument(document, params, signedData);
    }

    /**
     * Signs a document with an XAdES_BES signature with the packaging in parameter.
     *
     * @param signingParameters  the signing parameters (algorithms, certificates and private key)
     * @param document           the to be signed document
     * @param signaturePackaging the packaging (ENVELOPED or DETACHED)
     * @return a new document based on the input and signed
     */
    static DSSDocument signXAdES(
        final SignatureParameters signingParameters, final DSSDocument document,
        final SignaturePackaging signaturePackaging) {

        // TODO: replace with FACTORY!!
        final var certificateVerifier = new CommonCertificateVerifier(true);
        final var signatureService = new XAdESService(certificateVerifier);

        final var params = new XAdESSignatureParameters();
        params.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        params.setSignaturePackaging(signaturePackaging);
        params.setEncryptionAlgorithm(signingParameters.getEncryptionAlgorithm());
        params.setDigestAlgorithm(signingParameters.getDigestAlgorithm());
        params.setCertificateChain(signingParameters.getCertificateChain());
        params.setSigningCertificate(signingParameters.getCertificate());
        params.bLevel().setSigningDate(new Date());

        var bytesToSign = signatureService.getDataToSign(document, params);
        var signatureTokenConnection = signingParameters.getSignatureTokenConnection();

        SignatureValue signedData = signatureTokenConnection.sign(
            bytesToSign,
            signingParameters.getDigestAlgorithm(),
            signingParameters.getPrivateKey()
        );

        return signatureService.signDocument(document, params, signedData);
    }
}
