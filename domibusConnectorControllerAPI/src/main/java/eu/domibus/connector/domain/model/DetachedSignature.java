/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

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
