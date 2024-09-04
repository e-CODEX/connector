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
