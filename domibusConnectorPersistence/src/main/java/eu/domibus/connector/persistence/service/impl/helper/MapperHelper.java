/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl.helper;

import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;

/**
 * Utility class that provides helper methods for mapping operations.
 */
@UtilityClass
public class MapperHelper {
    /**
     * Converts a byte array to a string using the UTF-8 character set.
     *
     * @param bytes The byte array to convert. Can be null.
     * @return The string representation of the byte array, or null if the input array is null.
     * @throws RuntimeException if the UTF-8 encoding is not supported.
     */
    @SuppressWarnings("squid:S00112")
    @Nullable
    public static String convertByteArrayToString(@Nullable byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
