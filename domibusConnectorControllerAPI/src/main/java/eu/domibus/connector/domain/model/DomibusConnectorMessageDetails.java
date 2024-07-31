/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

/**
 * Holds the routing information for the {@link DomibusConnectorMessage}. The data represented is
 * needed to be able to send the message to other participants.
 *
 * @author riederb
 * @version 1.0
 */
@Data
@NoArgsConstructor
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
    // should be moved to domibusConnnectorMessage
    private DomibusConnectorMessageDirection direction;

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
        var builder = new ToStringCreator(this);
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
