package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.LargeFileReference;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorMessageAttachmentBuilder {
    private String identifier;
    private LargeFileReference attachment;
    private String name;
    private String mimeType;
    private String description;

    private DomibusConnectorMessageAttachmentBuilder() {
    }

    public static DomibusConnectorMessageAttachmentBuilder createBuilder() {
        return new DomibusConnectorMessageAttachmentBuilder();
    }

    public DomibusConnectorMessageAttachmentBuilder setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public DomibusConnectorMessageAttachmentBuilder setAttachment(LargeFileReference attachment) {
        this.attachment = attachment;
        return this;
    }

    public DomibusConnectorMessageAttachmentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public DomibusConnectorMessageAttachmentBuilder withMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public DomibusConnectorMessageAttachmentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public DomibusConnectorMessageAttachmentBuilder copyPropertiesFrom(DomibusConnectorMessageAttachment attachment) {
        this.attachment = attachment.getAttachment();
        this.description = attachment.getDescription();
        this.identifier = attachment.getIdentifier();
        this.mimeType = attachment.getMimeType();
        this.name = attachment.getName();
        return this;
    }

    public DomibusConnectorMessageAttachment build() {
        if (this.attachment == null) {
            throw new IllegalArgumentException("Attachment must be provided!");
        }
        if (this.identifier == null) {
            throw new IllegalArgumentException("identifier must be provided!");
        }
        DomibusConnectorMessageAttachment domibusConnectorMessageAttachment =
                new DomibusConnectorMessageAttachment(attachment, identifier);
        domibusConnectorMessageAttachment.setDescription(description);
        domibusConnectorMessageAttachment.setMimeType(mimeType);
        domibusConnectorMessageAttachment.setName(name);
        return domibusConnectorMessageAttachment;
    }
}
