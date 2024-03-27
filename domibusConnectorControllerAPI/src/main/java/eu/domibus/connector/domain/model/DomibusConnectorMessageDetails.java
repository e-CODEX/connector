package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;


/**
 * Holds the routing information for the {@link DomibusConnectorMessage}. The data
 * represented is needed to be able to send the message to other participants.
 *
 * @author riederb
 * @version 1.0
 */
public class DomibusConnectorMessageDetails implements Serializable {
    @Nullable
    private String backendMessageId;
    @Nullable
    private String refToBackendMessageId;
    // AS4 properties:
    @Nullable
    private String ebmsMessageId;
    @Nullable
    private String refToMessageId;
    @Nullable
    private String conversationId;
    private String originalSender;
    private String finalRecipient;
    private DomibusConnectorService service;
    private DomibusConnectorAction action;
    private DomibusConnectorParty fromParty;
    private DomibusConnectorParty toParty;
    // end of AS4 properties

    // the backend client name the message is received from or should be delivered to
    @Nullable
    private String connectorBackendClientName;
    // the gateway name the message is received from or should be submitted to
    @Nullable
    private String gatewayName;
    // should be moved to the transportStateService
    @Nullable
    private Date deliveredToGateway;
    // should be moved to the transportStateService
    @Nullable
    private Date deliveredToBackend;
    @Nullable
    private ZonedDateTime confirmed;
    @Nullable
    private ZonedDateTime rejected;
    @Nullable
    private DomibusConnectorMessageId causedBy;
    @Nullable
    private Date failed;

    // should be moved to domibusConnectorMessage
    private DomibusConnectorMessageDirection direction;

    public DomibusConnectorMessageDetails() {
    }

    public String getBackendMessageId() {
        return this.backendMessageId;
    }

    /**
     * @param backendMessageId backendMessageId
     */
    public void setBackendMessageId(String backendMessageId) {
        this.backendMessageId = backendMessageId;
    }

    public String getEbmsMessageId() {
        return this.ebmsMessageId;
    }

    /**
     * @param ebmsMessageId ebmsMessageId
     */
    public void setEbmsMessageId(String ebmsMessageId) {
        this.ebmsMessageId = ebmsMessageId;
    }

    public String getRefToMessageId() {
        return this.refToMessageId;
    }

    /**
     * @param refToMessageId refToMessageId
     */
    public void setRefToMessageId(String refToMessageId) {
        this.refToMessageId = refToMessageId;
    }

    public String getConversationId() {
        return this.conversationId;
    }

    /**
     * @param conversationId conversationId
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getOriginalSender() {
        return this.originalSender;
    }

    /**
     * @param originalSender originalSender
     */
    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }

    public String getFinalRecipient() {
        return this.finalRecipient;
    }

    /**
     * @param finalRecipient finalRecipient
     */
    public void setFinalRecipient(String finalRecipient) {
        this.finalRecipient = finalRecipient;
    }

    public DomibusConnectorService getService() {
        return this.service;
    }

    /**
     * @param service service
     */
    public void setService(DomibusConnectorService service) {
        this.service = service;
    }

    public DomibusConnectorAction getAction() {
        return this.action;
    }

    /**
     * @param action action
     */
    public void setAction(DomibusConnectorAction action) {
        this.action = action;
    }

    public DomibusConnectorParty getFromParty() {
        return this.fromParty;
    }

    /**
     * @param fromParty fromParty
     */
    public void setFromParty(DomibusConnectorParty fromParty) {
        this.fromParty = fromParty;
    }

    public DomibusConnectorParty getToParty() {
        return this.toParty;
    }

    /**
     * @param toParty toParty
     */
    public void setToParty(DomibusConnectorParty toParty) {
        this.toParty = toParty;
    }

    @Nullable
    public String getConnectorBackendClientName() {
        return connectorBackendClientName;
    }

    public void setConnectorBackendClientName(@Nullable String connectorBackendClientName) {
        this.connectorBackendClientName = connectorBackendClientName;
    }

    @Nullable
    public Date getDeliveredToGateway() {
        return deliveredToGateway;
    }

    public void setDeliveredToGateway(@Nullable Date deliveredToGateway) {
        this.deliveredToGateway = deliveredToGateway;
    }

    @Nullable
    public Date getDeliveredToBackend() {
        return deliveredToBackend;
    }

    public void setDeliveredToBackend(@Nullable Date deliveredToBackend) {
        this.deliveredToBackend = deliveredToBackend;
    }

    @Nullable
    public DomibusConnectorMessageId getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(@Nullable DomibusConnectorMessageId causedBy) {
        this.causedBy = causedBy;
    }

    public DomibusConnectorMessageDirection getDirection() {
        return direction;
    }

    public void setDirection(DomibusConnectorMessageDirection direction) {
        this.direction = direction;
    }

    @Nullable
    public String getRefToBackendMessageId() {
        return refToBackendMessageId;
    }

    public void setRefToBackendMessageId(@Nullable String refToBackendMessageId) {
        this.refToBackendMessageId = refToBackendMessageId;
    }

    @Nullable
    public ZonedDateTime getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(@Nullable ZonedDateTime confirmed) {
        this.confirmed = confirmed;
    }

    @Nullable
    public ZonedDateTime getRejected() {
        return rejected;
    }

    public void setRejected(@Nullable ZonedDateTime rejected) {
        this.rejected = rejected;
    }

    @Nullable
    public Date getFailed() {
        return failed;
    }

    public void setFailed(@Nullable Date failed) {
        this.failed = failed;
    }

    @Nullable
    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(@Nullable String gatewayName) {
        this.gatewayName = gatewayName;
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("direction", this.direction);
        builder.append("ebmsMessageId", this.ebmsMessageId);
        builder.append("backendMessageId", this.backendMessageId);
        builder.append("refToMessageId", this.refToMessageId);
        builder.append("originalSender", this.originalSender);
        builder.append("finalRecipient", this.finalRecipient);
        builder.append("conversationId", this.conversationId);
        builder.append("fromParty", this.fromParty);
        builder.append("toParty", this.toParty);

        return builder.toString();
    }
}
