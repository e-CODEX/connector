package eu.domibus.connector.domain.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.style.ToStringCreator;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * This domain object contains all data of a message. At least the {@link
 * DomibusConnectorMessageDetails} and the {@link DomibusConnectorMessageContent}
 * must be given at the time of creation as they represent the minimum structure
 * of a message. While the message is processed by the domibusConnector, the data
 * inside this structure changes up to the point where the message is completely
 * finished.
 * @author riederb
 * @version 1.0
 */
@Validated
public class DomibusConnectorMessage implements Serializable {

	@NotNull
	@Valid
	private DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId = DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
	@NotNull
	@Valid
	private DomibusConnectorMessageId connectorMessageId;
	@NotNull
	@Valid
	private DomibusConnectorMessageDetails messageDetails;

	private DomibusConnectorMessageContent messageContent;
	private final List<DomibusConnectorMessageAttachment> messageAttachments = new ArrayList<>();
	//holds all message confirmations which are transported with this message
	private final List<DomibusConnectorMessageConfirmation> transportedMessageConfirmations = new ArrayList<>();
	//holds all message confirmations which are related to this business message
	private final List<DomibusConnectorMessageConfirmation> relatedMessageConfirmations = new ArrayList<>();
	//holds all errors which occured during message processing...
	private final List<DomibusConnectorMessageError> messageProcessErrors = new ArrayList<>();

	private DCMessageProcessSettings dcMessageProcessSettings = new DCMessageProcessSettings();


	/**
	 * Default constructor, needed for frameworks
	 * to serialize and deserialize objects of this class
	 */
    public DomibusConnectorMessage() {}

	/**
	 * This constructor initializes an instance of a DomibusConnectorMessage in case
	 * it is not a confirmation message. At least the messageDetails and the
	 * messageContent must be given.
	 * 
	 * @param messageDetails    The details for message routing.
	 * @param messageContent    The content of the message.
     * 
     * 
	 */
	public DomibusConnectorMessage(final DomibusConnectorMessageDetails messageDetails, final DomibusConnectorMessageContent messageContent){
	   this.messageDetails = messageDetails;
	   this.messageContent = messageContent;
	}

    /**
	 * This constructor initializes an instance of a DomibusConnectorMessage in case
	 * it is not a confirmation message. At least the messageDetails and the
	 * messageContent must be given.
	 * 
     * @param connectorMessageId The internal connector message process id
	 * @param messageDetails    The details for message routing.
	 * @param messageContent    The content of the message.
	 */
	public DomibusConnectorMessage(
            final String connectorMessageId,
            final DomibusConnectorMessageDetails messageDetails, 
            final DomibusConnectorMessageContent messageContent){
        this.connectorMessageId = new DomibusConnectorMessageId(connectorMessageId);
        this.messageDetails = messageDetails;
        this.messageContent = messageContent;
	}
    
	/**
	 * This constructor initializes an instance of a DomibusConnectorMessage in case
	 * it is a confirmation message. At least the messageDetails and the
	 * messageConfirmation must be given.
	 * 
	 * @param messageDetails messageDetails
	 * @param messageConfirmation messageConfirmation
     * 
     * 
	 */
	public DomibusConnectorMessage(final DomibusConnectorMessageDetails messageDetails, final DomibusConnectorMessageConfirmation messageConfirmation){
	   this.messageDetails = messageDetails;
	   addTransportedMessageConfirmation(messageConfirmation);
	}
    
    /**
	 * This constructor initializes an instance of a DomibusConnectorMessage in case
	 * it is a confirmation message. At least the messageDetails and the
	 * messageConfirmation must be given.
	 * 
     * @param connectorMessageId internal connector message process id
	 * @param messageDetails messageDetails
	 * @param messageConfirmation messageConfirmation
	 */
    public DomibusConnectorMessage(
            final String connectorMessageId, 
            final DomibusConnectorMessageDetails messageDetails, 
            final DomibusConnectorMessageConfirmation messageConfirmation) {
        this.connectorMessageId = new DomibusConnectorMessageId(connectorMessageId);
        this.messageDetails = messageDetails;
        addTransportedMessageConfirmation(messageConfirmation);
    }


    @JsonProperty
	public DomibusConnectorMessageId getConnectorMessageId() {
		return connectorMessageId;
	}


	@JsonProperty
	public void setConnectorMessageId(DomibusConnectorMessageId messageId) {
		this.connectorMessageId = messageId;
	}

	public DomibusConnectorMessageDetails getMessageDetails(){
		return this.messageDetails;
	}

	public void setMessageDetails(DomibusConnectorMessageDetails messageDetails) {
		this.messageDetails = messageDetails;
	}

	public DomibusConnectorMessageContent getMessageContent(){
		return this.messageContent;
	}

	public List<DomibusConnectorMessageAttachment> getMessageAttachments(){
		return this.messageAttachments;
	}

	public List<DomibusConnectorMessageConfirmation> getTransportedMessageConfirmations(){
		return this.transportedMessageConfirmations;
	}

	public List<DomibusConnectorMessageConfirmation> getRelatedMessageConfirmations() {
		return relatedMessageConfirmations;
	}

	public DomibusConnectorBusinessDomain.BusinessDomainId getMessageLaneId() {
		return businessDomainId;
	}

	public void setMessageLaneId(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
		this.businessDomainId = businessDomainId;
	}

	/**
	 * Method to add a new {@link DomibusConnectorMessageConfirmation} to the
	 * collection.
	 *
	 * The confirmations here are related to the message document/content
	 *
	 * The collection is initialized, so no new collection needs to be
	 * created or set.
	 *
	 * @param confirmation    confirmation
	 * @return for return see: {@link List#add(Object)}
	 */
	public boolean addRelatedMessageConfirmation(final DomibusConnectorMessageConfirmation confirmation){
		return this.relatedMessageConfirmations.add(confirmation);
	}

	/**
	 * Method to add a new {@link DomibusConnectorMessageAttachment} to the collection.
	 * The collection is initialized, so no new collection needs to be created or set.
	 * 
	 * @param attachment    attachment
	 */
	public void addAttachment(final DomibusConnectorMessageAttachment attachment){
	   	this.messageAttachments.add(attachment);
	}

	/**
	 * Method to add a new {@link DomibusConnectorMessageConfirmation} to the
	 * collection. This collection holds only Confirmations which are transported
	 * with this message. In case of a business message they are also related
	 * to it.
	 * The collection is initialized, so no new collection needs to be
	 * created or set.
	 * 
	 * @param confirmation    confirmation
	 */
	public boolean addTransportedMessageConfirmation(final DomibusConnectorMessageConfirmation confirmation){
		if (!this.transportedMessageConfirmations.contains(confirmation)) {
			return this.transportedMessageConfirmations.add(confirmation);
		} else {
			return false; //duplicate
		}
	}

	public DCMessageProcessSettings getDcMessageProcessSettings() {
		return dcMessageProcessSettings;
	}

	public void setDcMessageProcessSettings(DCMessageProcessSettings dcMessageProcessSettings) {
		this.dcMessageProcessSettings = dcMessageProcessSettings;
	}

	@JsonIgnore
	public List<DomibusConnectorMessageError> getMessageProcessErrors(){
		return this.messageProcessErrors;
	}
	/**
	 * Method to add a new {@link DomibusConnectorMessageError} to the collection.
	 * This collection is filled during the processing of the message inside the
	 * domibusConnector, or, if there are message related errors reported by the
	 * gateway.
	 * 
	 * @param error    error
	 */
	public void addError(final DomibusConnectorMessageError error){
	   	this.messageProcessErrors.add(error);
	}

	@Deprecated
	@JsonIgnore
    public String getConnectorMessageIdAsString() {
		if (connectorMessageId == null) {
			return null;
		}
        return connectorMessageId.getConnectorMessageId();
    }

	@Deprecated
	@JsonIgnore
    public void setConnectorMessageId(String connectorMessageId) {
        this.connectorMessageId = new DomibusConnectorMessageId(connectorMessageId);
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("connectorMessageId", this.connectorMessageId);
        builder.append("messageDetails", this.messageDetails);
        return builder.toString();
	}

	public void setMessageContent(DomibusConnectorMessageContent messageContent) {
		this.messageContent = messageContent;
	}
}