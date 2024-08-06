/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.ecodex.signature;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;

/**
 * The DigestUtil class provides utility methods for digesting byte arrays using different
 * algorithms.
 */
@UtilityClass
public class DigestUtil {
    public static byte[] digestSHA256(final byte[] bytes) {
        return digest(bytes, DigestAlgorithm.SHA256);
    }

    /**
     * Computes the digest of the given byte array using the specified algorithm. The algorithm must
     * be supported by the underlying MessageDigest implementation.
     *
     * @param bytes     the byte array to digest
     * @param algorithm the name of the algorithm to use for the digest computation. If set to null,
     *                  the default algorithm will be used.
     * @return the computed digest as a byte array
     * @throws RuntimeException if the algorithm is not available
     */
    public static byte[] digest(final byte[] bytes, final DigestAlgorithm algorithm) {
        return digest(bytes, algorithm == null ? null : algorithm.getName());
    }

    /**
     * Computes the digest of the given byte array using the specified algorithm. The algorithm must
     * be supported by the underlying MessageDigest implementation.
     *
     * @param bytes     the byte array to digest
     * @param algorithm the name of the algorithm to use for the digest computation
     * @return the computed digest as a byte array
     * @throws RuntimeException if the algorithm is not available
     */
    public static byte[] digest(final byte[] bytes, final String algorithm) {
        try {
            final var digest = MessageDigest.getInstance(algorithm);
            final byte[] digestValue = digest.digest(bytes);
            return Base64.encodeBase64(digestValue);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
