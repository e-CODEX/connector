package eu.domibus.connector.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;


/**
 * The DomibusConnectorMessageContent holds the main content of a message. This is
 * the XML data of the main Form of the message and the printable document that
 * most of the {@link DomibusConnectorAction} require.
 *
 * A message is a business message only if a messageContent is
 * present
 *
 *
 * @author riederb
 * @version 1.0
 * updated 29-Dez-2017 10:12:49
 */
public class DomibusConnectorMessageContent implements Serializable {

	private byte xmlContent[];	//TODO: this should also be a LargeFileReference so it is also processed by content deletion!
	@Nullable
	private DomibusConnectorMessageDocument document;

	public DomibusConnectorMessageContent(){

	}

	public byte[] getXmlContent(){
		return this.xmlContent;
	}

	/**
	 * 
	 * @param xmlContent    newVal
	 */
	public void setXmlContent(byte[] xmlContent){
		this.xmlContent = xmlContent;
	}

	public DomibusConnectorMessageDocument getDocument(){
		return this.document;
	}

	/**
	 * 
	 * @param document    newVal
	 */
	public void setDocument(DomibusConnectorMessageDocument document){
		this.document = document;
	}

	@Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("document", this.document);
        return builder.toString();        
    }
    
}