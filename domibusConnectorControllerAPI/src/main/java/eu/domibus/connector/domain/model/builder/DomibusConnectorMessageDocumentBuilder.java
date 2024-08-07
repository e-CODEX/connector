/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DetachedSignature;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import eu.domibus.connector.domain.model.LargeFileReference;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

/**
 * The DomibusConnectorMessageDocumentBuilder class is responsible for building instances of
 * DomibusConnectorMessageDocument.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@NoArgsConstructor
public final class DomibusConnectorMessageDocumentBuilder {
    private LargeFileReference documentContent;
    private String documentName;
    private DetachedSignature detachedSignature = null;

    public static DomibusConnectorMessageDocumentBuilder createBuilder() {
        return new DomibusConnectorMessageDocumentBuilder();
    }

    public DomibusConnectorMessageDocumentBuilder setName(@NotNull String documentName) {
        this.documentName = documentName;
        return this;
    }

    public DomibusConnectorMessageDocumentBuilder setContent(
        @NotNull LargeFileReference documentContent) {
        this.documentContent = documentContent;
        return this;
    }

    public DomibusConnectorMessageDocumentBuilder withDetachedSignature(
        DetachedSignature signature) {
        this.detachedSignature = signature;
        return this;
    }

    /**
     * Copies properties from the given DomibusConnectorMessageDocument to the current instance.
     * Updates the detached signature, document content, and document name.
     *
     * @param doc the DomibusConnectorMessageDocument to copy properties from
     * @return the updated DomibusConnectorMessageDocumentBuilder instance
     * @throws IllegalArgumentException if the given document is null
     */
    public DomibusConnectorMessageDocumentBuilder copyPropertiesFrom(
        DomibusConnectorMessageDocument doc) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null here!");
        }
        this.detachedSignature = doc.getDetachedSignature();
        this.documentContent = doc.getDocument();
        this.documentName = doc.getDocumentName();
        return this;
    }

    /**
     * Builds a {@link DomibusConnectorMessageDocument} object.
     *
     * <p>This method checks if the {@code documentName} and {@code documentContent}
     * are not null and throws an {@link IllegalArgumentException} if either of them is null.
     * It then creates a new instance of {@link DomibusConnectorMessageDocument} using the provided
     * {@code documentContent}, {@code documentName}, and {@code detachedSignature}, and returns it.
     *
     * @return the built DomibusConnectorMessageDocument object
     * @throws IllegalArgumentException if the documentName or documentContent is null
     */
    public DomibusConnectorMessageDocument build() {
        if (documentName == null) {
            throw new IllegalArgumentException("documentName can not be null!");
        }
        if (documentContent == null) {
            throw new IllegalArgumentException("documentContent can not be null!");
        }
        return new DomibusConnectorMessageDocument(
            documentContent, documentName, detachedSignature
        );
    }
}
