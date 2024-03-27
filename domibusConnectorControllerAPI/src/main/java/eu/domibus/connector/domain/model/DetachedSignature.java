package eu.domibus.connector.domain.model;

import java.io.Serializable;


/**
 * @author riederb
 * @version 1.0
 */
public class DetachedSignature implements Serializable {
    private byte[] detachedSignature;
    private String detachedSignatureName;
    private DetachedSignatureMimeType mimeType;

    public DetachedSignature() {
    }

    /**
     * @param detachedSignature     detachedSignature
     * @param detachedSignatureName detachedSignatureName
     * @param mimeType              mimeType
     */
    public DetachedSignature(
            final byte[] detachedSignature,
            final String detachedSignatureName,
            final DetachedSignatureMimeType mimeType) {
        this.detachedSignature = detachedSignature;
        this.detachedSignatureName = detachedSignatureName;
        this.mimeType = mimeType;
    }

    public byte[] getDetachedSignature() {
        return this.detachedSignature;
    }

    public String getDetachedSignatureName() {
        return this.detachedSignatureName;
    }

    public DetachedSignatureMimeType getMimeType() {
        return this.mimeType;
    }
}
