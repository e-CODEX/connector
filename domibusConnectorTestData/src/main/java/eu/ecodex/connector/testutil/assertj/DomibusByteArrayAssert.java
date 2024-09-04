/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.testutil.assertj;

import java.nio.charset.StandardCharsets;
import org.assertj.core.api.AbstractByteArrayAssert;

/**
 * AssertJ assertion which has a method to check if a byte array contains a specific UTF8 String.
 */
public class DomibusByteArrayAssert extends AbstractByteArrayAssert<DomibusByteArrayAssert> {
    public DomibusByteArrayAssert(byte[] actual) {
        super(actual, DomibusByteArrayAssert.class);
    }

    public static DomibusByteArrayAssert assertThat(byte[] actual) {
        return new DomibusByteArrayAssert(actual);
    }

    /**
     * Checks if a byte array contains the specified UTF-8 encoded string.
     *
     * @param expected The expected UTF-8 string to be contained in the byte array.
     * @return The DomibusByteArrayAssert instance.
     * @throws RuntimeException If an UnsupportedEncodingException occurs.
     * @since -
     */
    public DomibusByteArrayAssert containsUTF8String(String expected) {
        isNotNull();
        var byteString = new String(actual, StandardCharsets.UTF_8);
        if (!expected.equals(byteString)) {
            failWithMessage(
                "Expected byte array contains <%s> but was <%s>", expected, byteString);
        }
        return this;
    }
}
