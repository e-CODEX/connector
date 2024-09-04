/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * The DetachedSignatureMimeType enum represents different MIME types for detached signatures.
 *
 * @author riederb
 * @version 1.0
 */
public enum DetachedSignatureMimeType implements Serializable {
    /**
     * application/octet-stream.
     */
    BINARY("application/octet-stream"),
    /**
     * text/xml.
     */
    XML("text/xml"),
    /**
     * application/pkcs7-signature.
     */
    PKCS7("application/pkcs7-signature");
    private final String code;

    /**
     * The DetachedSignatureMimeType class represents different MIME types for detached signatures.
     *
     * @param code The value of the mime-type's code.
     */
    DetachedSignatureMimeType(final String code) {
        this.code = code;
    }

    /**
     * Retrieves the code of the DetachedSignatureMimeType.
     *
     * @return The code of the DetachedSignatureMimeType.
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Retrieves the DetachedSignatureMimeType corresponding to the given code.
     *
     * @param code The code of the DetachedSignatureMimeType to retrieve.
     * @return The DetachedSignatureMimeType corresponding to the given code.
     * @throws NoSuchElementException if there is no DetachedSignatureMimeType with code
     */
    public static DetachedSignatureMimeType fromCode(String code) {
        return Stream.of(DetachedSignatureMimeType.values())
            .filter(detachedSignatureMimeType -> detachedSignatureMimeType.getCode().equals(code))
            .findFirst()
            .get();
    }
}
