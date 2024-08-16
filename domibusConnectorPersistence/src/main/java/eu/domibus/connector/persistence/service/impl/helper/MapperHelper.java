/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
