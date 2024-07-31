/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

/**
 * Holds the printable document to a message. The document itself is a byte[]. A documentName that
 * the document is identified with and optionally a {@link DetachedSignature} that the document is
 * signed with are also content of this object.
 *
 * @author riederb
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class DomibusConnectorMessageDocument implements Serializable {
    private LargeFileReference document;
    private String documentName;
    private DetachedSignature detachedSignature;
    private String hashValue;

    /**
     * Constructor for DomibusConnectorMessageDocument with all attributes required and one optional
     * attribute.
     *
     * @param document          the printable document as a byte[]
     * @param documentName      the name of the printable document the document is identified with.
     * @param detachedSignature may be null. If the document is signed with a detached signature,
     *                          the signature parameters are given here.
     */
    public DomibusConnectorMessageDocument(final LargeFileReference document,
                                           final String documentName,
                                           final DetachedSignature detachedSignature) {
        this.document = document;
        this.documentName = documentName;
        this.detachedSignature = detachedSignature;
    }

    public @Nullable DetachedSignature getDetachedSignature() {
        return this.detachedSignature;
    }

    public @Nullable String getHashValue() {
        return this.hashValue;
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("documentName", this.documentName);
        return builder.toString();
    }
}
