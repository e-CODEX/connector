/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
