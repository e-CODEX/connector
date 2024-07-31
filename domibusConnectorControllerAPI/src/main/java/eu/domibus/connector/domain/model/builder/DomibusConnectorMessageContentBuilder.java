/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import java.util.Arrays;

/**
 * The DomibusConnectorMessageContentBuilder class is used to build a DomibusConnectorMessageContent
 * object. It provides methods to set the XML content and document of the message, as well as a
 * method to build the final object.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorMessageContentBuilder {
    private byte[] xmlContent;
    private DomibusConnectorMessageDocument document;

    private DomibusConnectorMessageContentBuilder() {
    }

    public static DomibusConnectorMessageContentBuilder createBuilder() {
        return new DomibusConnectorMessageContentBuilder();
    }

    public DomibusConnectorMessageContentBuilder setXmlContent(byte[] xmlContent) {
        this.xmlContent = xmlContent;
        return this;
    }

    public DomibusConnectorMessageContentBuilder setDocument(
        DomibusConnectorMessageDocument document) {
        this.document = document;
        return this;
    }

    /**
     * Builds a DomibusConnectorMessageContent object.
     *
     * @return the built DomibusConnectorMessageContent object
     */
    public DomibusConnectorMessageContent build() {
        var content = new DomibusConnectorMessageContent();
        content.setDocument(document);
        content.setXmlContent(xmlContent);
        return content;
    }

    public boolean canBuild() {
        return xmlContent != null && document != null;
    }

    /**
     * Copies properties from the given DomibusConnectorMessageContent to the current instance.
     *
     * @param content the DomibusConnectorMessageContent to copy properties from
     * @return the updated DomibusConnectorMessageContentBuilder instance
     */
    public DomibusConnectorMessageContentBuilder copyPropertiesFrom(
        DomibusConnectorMessageContent content) {
        if (content.getDocument() != null) {
            this.document = DomibusConnectorMessageDocumentBuilder.createBuilder()
                .copyPropertiesFrom(content.getDocument())
                .build();
        }
        this.xmlContent = Arrays.copyOf(content.getXmlContent(), content.getXmlContent().length);
        return this;
    }
}
