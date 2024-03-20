
package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DetachedSignature;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;

import javax.validation.constraints.NotNull;


/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorMessageDocumentBuilder {


    private LargeFileReference documentContent;
    private String documentName;
    
    private DetachedSignature detachedSignature = null;
    
    public static DomibusConnectorMessageDocumentBuilder createBuilder() {
        return new DomibusConnectorMessageDocumentBuilder();
    }
    
    private DomibusConnectorMessageDocumentBuilder() {}


    public DomibusConnectorMessageDocumentBuilder setName(@NotNull String documentName) {
        this.documentName = documentName;
        return this;
    }
    
    public DomibusConnectorMessageDocumentBuilder setContent(@NotNull LargeFileReference documentContent) {
        this.documentContent = documentContent;
        return this;
    }
    
    public DomibusConnectorMessageDocumentBuilder withDetachedSignature(DetachedSignature signature) {
        this.detachedSignature = signature;
        return this;
    }
    
    public DomibusConnectorMessageDocumentBuilder copyPropertiesFrom(DomibusConnectorMessageDocument doc) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null here!");
        }
        this.detachedSignature = doc.getDetachedSignature();
        this.documentContent = doc.getDocument();
        this.documentName = doc.getDocumentName();
        return this;
    }
        
    public DomibusConnectorMessageDocument build() {                
        if (documentName == null) {
            throw new IllegalArgumentException("documentName can not be null!");
        }
        if (documentContent == null) {
            throw new IllegalArgumentException("documentContent can not be null!");
        }
        DomibusConnectorMessageDocument doc = new DomibusConnectorMessageDocument(documentContent, documentName, detachedSignature);
        return doc;
    }
    
}
