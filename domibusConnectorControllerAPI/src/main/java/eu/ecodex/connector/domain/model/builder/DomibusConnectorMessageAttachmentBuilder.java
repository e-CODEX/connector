/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.builder;

import eu.ecodex.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.LargeFileReference;

/**
 * The DomibusConnectorMessageAttachmentBuilder class is used to construct instances of
 * DomibusConnectorMessageAttachment. It provides a fluent builder interface to set various
 * properties of the attachment before building the final object.
 */
public final class DomibusConnectorMessageAttachmentBuilder {
    private String identifier;
    private LargeFileReference attachment;
    private String name;
    private String mimeType;
    private String description;

    public static DomibusConnectorMessageAttachmentBuilder createBuilder() {
        return new DomibusConnectorMessageAttachmentBuilder();
    }

    private DomibusConnectorMessageAttachmentBuilder() {
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

    /**
     * Copies properties from the given {@link DomibusConnectorMessageAttachment} to the current
     * instance.
     *
     * @param attachment the {@link DomibusConnectorMessageAttachment} to copy properties from
     * @return the updated {@link DomibusConnectorMessageAttachmentBuilder} instance
     */
    public DomibusConnectorMessageAttachmentBuilder copyPropertiesFrom(
        DomibusConnectorMessageAttachment attachment) {
        this.attachment = attachment.getAttachment();
        this.description = attachment.getDescription();
        this.identifier = attachment.getIdentifier();
        this.mimeType = attachment.getMimeType();
        this.name = attachment.getName();
        return this;
    }

    /**
     * Builds a DomibusConnectorMessageAttachment object with the provided properties.
     *
     * @return The built DomibusConnectorMessageAttachment object
     * @throws IllegalArgumentException if attachment or identifier is null
     */
    public DomibusConnectorMessageAttachment build() {
        if (this.attachment == null) {
            throw new IllegalArgumentException("Attachment must be provided!");
        }
        if (this.identifier == null) {
            throw new IllegalArgumentException("identifier must be provided!");
        }
        var domibusConnectorMessageAttachment =
            new DomibusConnectorMessageAttachment(attachment, identifier);
        domibusConnectorMessageAttachment.setDescription(description);
        domibusConnectorMessageAttachment.setMimeType(mimeType);
        domibusConnectorMessageAttachment.setName(name);
        return domibusConnectorMessageAttachment;
    }
}
