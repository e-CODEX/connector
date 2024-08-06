/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.evidences;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

/**
 * The {@code HashValueBuilder} class is responsible for building hash values using the specified
 * digest algorithm.
 */
public class HashValueBuilder {
    private final MessageDigest digester;

    /**
     * The {@code HashValueBuilder} class is responsible for building hash values using the
     * specified digest algorithm.
     */
    public HashValueBuilder(DigestAlgorithm digestAlgorithm) {
        try {
            digester = digestAlgorithm.getMessageDigest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String buildHashValueAsString(byte[] originalMessage) {
        final byte[] resultByte = buildHashValue(originalMessage);
        return Hex.encodeHexString(resultByte);
    }

    /**
     * Builds a hash value using the specified digest algorithm.
     *
     * @param originalMessage The byte array representing the original message to be hashed.
     * @return The hash value of the original message as a byte array.
     */
    public byte[] buildHashValue(byte[] originalMessage) {
        digester.reset();
        digester.update(originalMessage);

        return digester.digest();
    }

    public String getAlgorithm() {
        return digester.getAlgorithm();
    }
}
