package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;

import java.util.Arrays;


/**
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

    public DomibusConnectorMessageContentBuilder setDocument(DomibusConnectorMessageDocument document) {
        this.document = document;
        return this;
    }

    public DomibusConnectorMessageContent build() {
        DomibusConnectorMessageContent content = new DomibusConnectorMessageContent();
        content.setDocument(document);
        content.setXmlContent(xmlContent);
        return content;
    }

    public boolean canBuild() {
        return xmlContent != null && document != null;
    }

    public DomibusConnectorMessageContentBuilder copyPropertiesFrom(DomibusConnectorMessageContent content) {
        if (content.getDocument() != null) {
            this.document = DomibusConnectorMessageDocumentBuilder
                    .createBuilder()
                    .copyPropertiesFrom(content.getDocument())
                    .build();
        }
        this.xmlContent = Arrays.copyOf(content.getXmlContent(), content.getXmlContent().length);
        return this;
    }
}
