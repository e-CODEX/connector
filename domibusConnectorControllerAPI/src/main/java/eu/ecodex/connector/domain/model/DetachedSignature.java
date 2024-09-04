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
import lombok.Getter;

/**
 * The DetachedSignature class represents a detached signature for a document. It contains the
 * signature data, signature name, and MIME type of the signature.
 */
@Getter
public class DetachedSignature implements Serializable {
    private byte[] detachedSignature;
    private String detachedSignatureName;
    private DetachedSignatureMimeType mimeType;

    public DetachedSignature() {
    }

    /**
     * Constructs a DetachedSignature object with the given detached signature, detached signature
     * name, and MIME type.
     *
     * @param detachedSignature     The detached signature data as a byte array.
     * @param detachedSignatureName The name of the detached signature.
     * @param mimeType              The MIME type of the detached signature.
     */
    public DetachedSignature(final byte[] detachedSignature, final String detachedSignatureName,
                             final DetachedSignatureMimeType mimeType) {
        this.detachedSignature = detachedSignature;
        this.detachedSignatureName = detachedSignatureName;
        this.mimeType = mimeType;
    }
}
