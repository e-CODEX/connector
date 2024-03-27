package eu.domibus.connector.domain.model;

import org.springframework.core.style.ToStringCreator;

import java.io.Serializable;


/**
 * This object contains an attachment for a message. With every message there can
 * be some documents (mostly PDF's) sent along with. Therefore those documents are
 * attached to the message over this type.  Attributes:  attachment: The data
 * itself in byte[]  name: The name of the attachment. Most usefull usage is the
 * file name of the attachment.  mimeType: The type of the attachment. Example:
 * "text/xml", "application/pdf"  description:
 *
 * @author riederb
 * @version 1.0
 */
public class DomibusConnectorMessageAttachment implements Serializable {
    private String identifier;
    private LargeFileReference attachment;
    private String name;
    private String mimeType;
    private String description;

    public DomibusConnectorMessageAttachment() {
    }

    /**
     * Constructor filling the two mandatory attributes
     *
     * @param attachment The data
     * @param identifier Identifies the attachment for transformation and
     *                   transportation
     */
    public DomibusConnectorMessageAttachment(final LargeFileReference attachment, final String identifier) {
        this.attachment = attachment;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public LargeFileReference getAttachment() {
        return this.attachment;
    }

    public void setAttachment(LargeFileReference attachment) {
        this.attachment = attachment;
    }

    public String getName() {
        return this.name;
    }

    /**
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * @param mimeType mimeType
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("identifier", this.identifier);
        builder.append("name", this.name);
        builder.append("mimeType", this.mimeType);
        builder.append("dataReference", this.attachment);
        return builder.toString();
    }
}
