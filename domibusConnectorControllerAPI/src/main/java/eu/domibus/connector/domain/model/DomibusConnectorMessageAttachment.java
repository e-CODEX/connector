/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.model;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;

/**
 * This object contains an attachment for a message. With every message there can be some documents
 * (mostly PDF's) sent along with. Therefore, those documents are attached to the message over this
 * type.  Attributes:  attachment: The data itself in byte[]  name: The name of the attachment. Most
 * usefull usage is the file name of the attachment.  mimeType: The type of the attachment. Example:
 * "text/xml", "application/pdf"  description:
 *
 * @author riederb
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class DomibusConnectorMessageAttachment implements Serializable {
    private String identifier;
    private LargeFileReference attachment;
    private String name;
    private String mimeType;
    private String description;

    /**
     * Constructor filling the two mandatory attributes.
     *
     * @param attachment The data
     * @param identifier Identifies the attachment for transformation and transportation
     */
    public DomibusConnectorMessageAttachment(final LargeFileReference attachment,
                                             final String identifier) {
        this.attachment = attachment;
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("identifier", this.identifier);
        builder.append("name", this.name);
        builder.append("mimeType", this.mimeType);
        builder.append("dataReference", this.attachment);
        return builder.toString();
    }
}
