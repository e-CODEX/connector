/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;
import org.springframework.validation.annotation.Validated;

/**
 * This domain object contains all data of a message. At least the
 * {@link DomibusConnectorMessageDetails} and the {@link DomibusConnectorMessageContent} must be
 * given at the time of creation as they represent the minimum structure of a message. While the
 * message is processed by the domibusConnector, the data inside this structure changes up to the
 * point where the message is completely finished.
 *
 * @author riederb
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Validated
public class DomibusConnectorMessage implements Serializable {
    @NotNull
    @Valid
    private DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId =
        DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
    @NotNull
    @Valid
    private DomibusConnectorMessageId connectorMessageId;
    @NotNull
    @Valid
    private DomibusConnectorMessageDetails messageDetails;
    private DomibusConnectorMessageContent messageContent;
    private final List<DomibusConnectorMessageAttachment> messageAttachments = new ArrayList<>();
    // holds all message confirmations which are transported with this message
    private final List<DomibusConnectorMessageConfirmation> transportedMessageConfirmations =
        new ArrayList<>();
    // holds all message confirmations which are related to this business message
    private final List<DomibusConnectorMessageConfirmation> relatedMessageConfirmations =
        new ArrayList<>();
    // holds all errors which occurred during message processing...
    private final List<DomibusConnectorMessageError> messageProcessErrors = new ArrayList<>();
    private DCMessageProcessSettings dcMessageProcessSettings = new DCMessageProcessSettings();

    /**
     * This constructor initializes an instance of a DomibusConnectorMessage in case it is not a
     * confirmation message. At least the messageDetails and the messageContent must be given.
     *
     * @param messageDetails The details for message routing.
     * @param messageContent The content of the message.
     */
    public DomibusConnectorMessage(final DomibusConnectorMessageDetails messageDetails,
                                   final DomibusConnectorMessageContent messageContent) {
        this.messageDetails = messageDetails;
        this.messageContent = messageContent;
    }

    /**
     * This constructor initializes an instance of a DomibusConnectorMessage in case it is not a
     * confirmation message. At least the messageDetails and the messageContent must be given.
     *
     * @param connectorMessageId The internal connector message process id
     * @param messageDetails     The details for message routing.
     * @param messageContent     The content of the message.
     */
    public DomibusConnectorMessage(
        final String connectorMessageId,
        final DomibusConnectorMessageDetails messageDetails,
        final DomibusConnectorMessageContent messageContent) {
        this.connectorMessageId = new DomibusConnectorMessageId(connectorMessageId);
        this.messageDetails = messageDetails;
        this.messageContent = messageContent;
    }

    /**
     * This constructor initializes an instance of a DomibusConnectorMessage in case it is a
     * confirmation message. At least the messageDetails and the messageConfirmation must be given.
     *
     * @param messageDetails      messageDetails
     * @param messageConfirmation messageConfirmation
     */
    public DomibusConnectorMessage(final DomibusConnectorMessageDetails messageDetails,
                                   final DomibusConnectorMessageConfirmation messageConfirmation) {
        this.messageDetails = messageDetails;
        addTransportedMessageConfirmation(messageConfirmation);
    }

    /**
     * This constructor initializes an instance of a DomibusConnectorMessage in case it is a
     * confirmation message. At least the messageDetails and the messageConfirmation must be given.
     *
     * @param connectorMessageId  internal connector message process id
     * @param messageDetails      messageDetails
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

    public DomibusConnectorBusinessDomain.BusinessDomainId getMessageLaneId() {
        return businessDomainId;
    }

    public void setMessageLaneId(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        this.businessDomainId = businessDomainId;
    }

    /**
     * Method to add a new {@link DomibusConnectorMessageConfirmation} to the collection.
     *
     * <p>The confirmations here are related to the message document/content
     *
     * <p>The collection is initialized, so no new collection needs to be
     * created or set.
     *
     * @param confirmation confirmation
     * @return for return see: {@link List#add(Object)}
     */
    public boolean addRelatedMessageConfirmation(
        final DomibusConnectorMessageConfirmation confirmation) {
        return this.relatedMessageConfirmations.add(confirmation);
    }

    /**
     * Method to add a new {@link DomibusConnectorMessageAttachment} to the collection. The
     * collection is initialized, so no new collection needs to be created or set.
     *
     * @param attachment attachment
     */
    public void addAttachment(final DomibusConnectorMessageAttachment attachment) {
        this.messageAttachments.add(attachment);
    }

    /**
     * Method to add a new {@link DomibusConnectorMessageConfirmation} to the collection. This
     * collection holds only Confirmations which are transported with this message. In case of a
     * business message they are also related to it. The collection is initialized, so no new
     * collection needs to be created or set.
     *
     * @param confirmation confirmation
     */
    public boolean addTransportedMessageConfirmation(
        final DomibusConnectorMessageConfirmation confirmation) {
        if (!this.transportedMessageConfirmations.contains(confirmation)) {
            return this.transportedMessageConfirmations.add(confirmation);
        } else {
            return false; // duplicate
        }
    }

    /**
     * Method to add a new {@link DomibusConnectorMessageError} to the collection. This collection
     * is filled during the processing of the message inside the domibusConnector, or, if there are
     * message related errors reported by the gateway.
     *
     * @param error error
     */
    public void addError(final DomibusConnectorMessageError error) {
        this.messageProcessErrors.add(error);
    }

    @JsonProperty
    public DomibusConnectorMessageId getConnectorMessageId() {
        return connectorMessageId;
    }

    @JsonIgnore
    public List<DomibusConnectorMessageError> getMessageProcessErrors() {
        return this.messageProcessErrors;
    }

    /**
     * Sets the connector message ID for the DomibusConnectorMessage.
     *
     * @param connectorMessageId The connector message ID to be set.
     * @deprecated This method is deprecated and should not be used.
     */
    @Deprecated
    @JsonIgnore
    public void setConnectorMessageId(String connectorMessageId) {
        this.connectorMessageId = new DomibusConnectorMessageId(connectorMessageId);
    }

    @JsonProperty
    public void setConnectorMessageId(DomibusConnectorMessageId messageId) {
        this.connectorMessageId = messageId;
    }

    /**
     * Retrieves the connector message ID as a string.
     *
     * @return The connector message ID as a string. Returns null if connectorMessageId is null.
     * @deprecated This method is deprecated and should not be used.
     */
    @Deprecated
    @JsonIgnore
    public String getConnectorMessageIdAsString() {
        if (connectorMessageId == null) {
            return null;
        }
        return connectorMessageId.getConnectorMessageId();
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("connectorMessageId", this.connectorMessageId);
        builder.append("messageDetails", this.messageDetails);
        return builder.toString();
    }
}
