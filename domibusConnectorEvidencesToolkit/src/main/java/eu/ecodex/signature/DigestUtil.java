/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
