package eu.domibus.connector.domain.model;

import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

import java.io.Serializable;


/**
 * Holds the printable document to a message. The document itself is a byte[]. A
 * documentName that the document is identified with and optionally a {@link
 * DetachedSignature} that the document is signed with are also content of this
 * object.
 *
 * @author riederb
 * @version 1.0
 */
public class DomibusConnectorMessageDocument implements Serializable {
    private LargeFileReference document;
    private String documentName;
    private DetachedSignature detachedSignature;
    private String hashValue;

    public DomibusConnectorMessageDocument() {
    }

    /**
     * Constructor for DomibusConnectorMessageDocument with all attributes required
     * and one optional attribute.
     *
     * @param document          the printable document as a byte[]
     * @param documentName      the name of the printable document the document is
     *                          identified with.
     * @param detachedSignature may be null. If the document is signed with a
     *                          detached signature, the signature parameters are given here.
     */
    public DomibusConnectorMessageDocument(
            final LargeFileReference document,
            final String documentName,
            final DetachedSignature detachedSignature) {
        this.document = document;
        this.documentName = documentName;
        this.detachedSignature = detachedSignature;
    }

    public LargeFileReference getDocument() {
        return this.document;
    }

    public void setDocument(LargeFileReference reference) {
        this.document = reference;
    }

    public String getDocumentName() {
        return this.documentName;
    }

    public @Nullable DetachedSignature getDetachedSignature() {
        return this.detachedSignature;
    }

    public @Nullable String getHashValue() {
        return this.hashValue;
    }

    /**
     * @param newVal the hashValue
     */
    public void setHashValue(String newVal) {
        this.hashValue = newVal;
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("documentName", this.documentName);
        return builder.toString();
    }
}
