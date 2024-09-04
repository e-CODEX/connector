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

import eu.ecodex.connector.domain.model.DomibusConnectorMessageContent;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDocument;
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
