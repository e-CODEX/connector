
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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/DigestUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.experimental.UtilityClass;
import org.bouncycastle.util.encoders.Base64;

/**
 * Provides convenience-methods for Digest.
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@UtilityClass
public class DigestUtil {

    /**
     * Generate a hash value for binary data using the {@link DigestAlgorithm} SHA256 The hash
     * value is encoded using the base64 algorithm.
     *
     * @param bytes The input to be digested
     * @return The result
     */
    public static byte[] digestSHA256(final byte[] bytes) {
        return digest(bytes, "SHA-256");
    }

    /**
     * Generate a hash value for binary data using the specified {@link DigestAlgorithm}
     * parameters. The hash value is encoded using the base64 algorithm.
     *
     * @param bytes     The input to be digested
     * @param algorithm The {@link DigestAlgorithm}
     * @return The result
     */
    public static byte[] digest(final byte[] bytes, final DigestAlgorithm algorithm) {
        return digest(bytes, algorithm == null ? null : algorithm.getName());
    }

    /**
     * Generate a hash value for binary data using the specified algorithm parameters. The hash
     * value is encoded using the base64 algorithm.
     *
     * @param bytes     The input to be digested
     * @param algorithm The name of the algorithm requested
     * @return The result
     */
    public static byte[] digest(final byte[] bytes, final String algorithm) {
        try {
            final var digest = MessageDigest.getInstance(algorithm);
            final byte[] digestValue = digest.digest(bytes);
            return Base64.encode(digestValue);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
