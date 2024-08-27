/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/SignatureParametersFactory.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import eu.ecodex.dss.model.CertificateStoreInfo;
import eu.ecodex.dss.model.SignatureParameters;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import java.security.KeyStore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

/**
 * Provides convenience-methods for creating a {@link SignatureParameters} instance.
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class SignatureParametersFactory {
    private static final LogDelegate LOG = new LogDelegate(SignatureParametersFactory.class);

    /**
     * Utility constructor.
     */
    private SignatureParametersFactory() {
    }

    /**
     * convenience method to be used if the algorithms are set externally.
     *
     * @param certStoreInfo access information for the keystore
     * @param certAlias     the alias of the certificate that should be used
     * @param certPassword  the password to get the (required) private key
     * @return a {@link SignatureParameters} instance (or null if provided data insufficient or cert
     *      not found)
     * @throws Exception as of the underlying classes
     */
    public static SignatureParameters create(
        final CertificateStoreInfo certStoreInfo, final String certAlias, final String certPassword)
        throws Exception {
        return create(certStoreInfo, certAlias, certPassword, null, null);
    }

    /**
     * loads a default keystore and extracts - via the alias - the certificate upon which the
     * SignatureParameters is created.
     *
     * @param certStoreInfo       access information for the keystore
     * @param certAlias           the alias of the certificate that should be used
     * @param certPassword        the password to get the (required) private key
     * @param encryptionAlgorithm the algorithm used to sign
     * @param digestAlgorithm     the algorithm used to create the digest
     * @return a {@link SignatureParameters} instance (or null if provided data insufficient or cert
     *      not found)
     * @throws Exception as of the underlying classes
     */
    public static SignatureParameters create(
        final CertificateStoreInfo certStoreInfo, final String certAlias, final String certPassword,
        final EncryptionAlgorithm encryptionAlgorithm, final DigestAlgorithm digestAlgorithm)
        throws Exception {
        LOG.lDetail(
            "parameters: {} for cert-alias '{}' algorithms: signature {} signing {}", certStoreInfo,
            certAlias, encryptionAlgorithm, digestAlgorithm
        );
        final var params = new SignatureParameters();

        // check if configuration is feasible
        if (certStoreInfo == null) {
            LOG.lWarn("no information about the keystore provided");
            return null;
        }
        if (!certStoreInfo.isValid()) {
            LOG.lWarn("the information about the keystore is invalid");
            return null;
        }
        if (StringUtils.isEmpty(certAlias)) {
            LOG.lWarn("the alias for getting the certificate is empty/null");
            return null;
        }
        if (certPassword == null) {
            LOG.lWarn("the password for getting the private key is null");
            return null;
        }

        final Resource ksLocation = certStoreInfo.getLocation();
        LOG.lDetail("loading keystore from url: {}", ksLocation);

        try (var ksStream = ksLocation.getInputStream()) {
            var pwProtection =
                new KeyStore.PasswordProtection(certStoreInfo.getPassword().toCharArray());
            var keyStoreSignatureTokenConnection =
                new KeyStoreSignatureTokenConnection(ksStream, "JKS", pwProtection);
            var keyPwProtection = new KeyStore.PasswordProtection(certPassword.toCharArray());
            var key = keyStoreSignatureTokenConnection.getKey(certAlias, keyPwProtection);
            params.setSignatureTokenConnection(keyStoreSignatureTokenConnection);
            params.setPrivateKey(key);
        }

        if (encryptionAlgorithm != null) {
            params.setEncryptionAlgorithm(encryptionAlgorithm);
        }
        if (digestAlgorithm != null) {
            params.setDigestAlgorithm(digestAlgorithm);
        }

        return params;
    }
}
