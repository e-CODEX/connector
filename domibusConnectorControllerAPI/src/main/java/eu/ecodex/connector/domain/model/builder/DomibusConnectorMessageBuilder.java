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

import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageContent;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDetails;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageError;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builder for @see eu.ecodex.connector.domain.model.DomibusConnectorMessage
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorMessageBuilder {
    private DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId;
    private DomibusConnectorMessageDetails messageDetails;
    private DomibusConnectorMessageContent messageContent;
    private List<DomibusConnectorMessageAttachment> messageAttachments = new ArrayList<>();
    private List<DomibusConnectorMessageConfirmation> transportedConfirmations = new ArrayList<>();
    private List<DomibusConnectorMessageError> messageErrors = new ArrayList<>();
    private DomibusConnectorMessageId connectorMessageId;
    private List<DomibusConnectorMessageConfirmation> relatedMessageConfirmations =
        new ArrayList<>();

    public static DomibusConnectorMessageBuilder createBuilder() {
        return new DomibusConnectorMessageBuilder();
    }

    private DomibusConnectorMessageBuilder() {
    }

    /**
     * is required.
     *
     * @param msgDetails the messageDetails
     * @return the builder
     */
    public DomibusConnectorMessageBuilder setMessageDetails(
        DomibusConnectorMessageDetails msgDetails) {
        this.messageDetails = msgDetails;
        return this;
    }

    /**
     * Is required if no confirmation is added to the message.
     *
     * @param msgContent the message content to set
     * @return the updated DomibusConnectorMessageBuilder instance
     */
    public DomibusConnectorMessageBuilder setMessageContent(
        DomibusConnectorMessageContent msgContent) {
        this.messageContent = msgContent;
        return this;
    }

    /**
     * Add none to multiple message attachments is optional.
     *
     * @param attachment the DomibusConnectorMessageAttachment to be added
     * @return the updated DomibusConnectorMessageBuilder instance
     */
    public DomibusConnectorMessageBuilder addAttachment(
        DomibusConnectorMessageAttachment attachment) {
        this.messageAttachments.add(attachment);
        return this;
    }

    public DomibusConnectorMessageBuilder addAttachments(
        List<DomibusConnectorMessageAttachment> domibusConnectorMessageAttachments) {
        this.messageAttachments.addAll(domibusConnectorMessageAttachments);
        return this;
    }

    /**
     * Add multiple confirmations to the message is required if no message content is set.
     *
     * @param confirmation the confirmation
     * @return the builder
     */
    public DomibusConnectorMessageBuilder addTransportedConfirmations(
        DomibusConnectorMessageConfirmation confirmation) {
        this.transportedConfirmations.add(confirmation);
        return this;
    }

    /**
     * Adds the given list of transported confirmations to the DomibusConnectorMessageBuilder.
     *
     * @param confirmations the list of DomibusConnectorMessageConfirmation objects to add
     * @return the DomibusConnectorMessageBuilder instance
     */
    public DomibusConnectorMessageBuilder addTransportedConfirmations(
        List<DomibusConnectorMessageConfirmation> confirmations) {
        this.transportedConfirmations.addAll(confirmations);
        return this;
    }

    /**
     * Adds a {@link DomibusConnectorMessageError} to the message.
     *
     * @param error the {@link DomibusConnectorMessageError} to add
     * @return the updated {@link DomibusConnectorMessageBuilder} instance
     */
    public DomibusConnectorMessageBuilder addError(DomibusConnectorMessageError error) {
        this.messageErrors.add(error);
        return this;
    }

    /**
     * Adds a list of {@link DomibusConnectorMessageError} objects to the message errors.
     *
     * @param errors the list of {@link DomibusConnectorMessageError} objects to add
     * @return the updated {@link DomibusConnectorMessageBuilder} instance
     */
    public DomibusConnectorMessageBuilder addErrors(List<DomibusConnectorMessageError> errors) {
        this.messageErrors.addAll(errors);
        return this;
    }

    /**
     * Builds a {@link DomibusConnectorMessage} object with the specified properties.
     *
     * @return the built {@link DomibusConnectorMessage} object
     * @throws IllegalArgumentException if message details are not set
     */
    public DomibusConnectorMessage build() {
        var message = new DomibusConnectorMessage();
        if (this.messageDetails == null) {
            throw new IllegalArgumentException("Setting message details is required!");
        }
        message.setMessageDetails(this.messageDetails);
        message.setConnectorMessageId(this.connectorMessageId);
        message.setMessageLaneId(this.businessDomainId);
        message.setMessageContent(this.messageContent);
        message.getMessageAttachments().addAll(this.messageAttachments);
        message.getTransportedMessageConfirmations().addAll(this.transportedConfirmations);
        message.getMessageProcessErrors().addAll(this.messageErrors);
        message.getRelatedMessageConfirmations().addAll(this.relatedMessageConfirmations);

        this.connectorMessageId = null;
        return message;
    }

    /**
     * Copies properties from the given DomibusConnectorMessage to the current instance.
     *
     * @param message the DomibusConnectorMessage to copy properties from
     * @return the updated DomibusConnectorMessageBuilder instance
     */
    public DomibusConnectorMessageBuilder copyPropertiesFrom(DomibusConnectorMessage message) {
        this.messageDetails = DomibusConnectorMessageDetailsBuilder.create()
            .copyPropertiesFrom(message.getMessageDetails())
            .build();
        this.connectorMessageId = message.getConnectorMessageId();
        this.businessDomainId = message.getMessageLaneId();
        if (message.getMessageContent() != null) {
            this.messageContent = DomibusConnectorMessageContentBuilder.createBuilder()
                .copyPropertiesFrom(message.getMessageContent())
                .build();
        }
        this.messageAttachments = message.getMessageAttachments()
            .stream()
            .map(a -> DomibusConnectorMessageAttachmentBuilder.createBuilder()
                .copyPropertiesFrom(a).build())
            .collect(Collectors.toList());

        this.transportedConfirmations = message.getTransportedMessageConfirmations()
            .stream()
            .map(c -> DomibusConnectorMessageConfirmationBuilder.createBuilder()
                .copyPropertiesFrom(c).build())
            .collect(Collectors.toList());

        this.relatedMessageConfirmations = message.getRelatedMessageConfirmations()
            .stream()
            .map(c -> DomibusConnectorMessageConfirmationBuilder.createBuilder()
                .copyPropertiesFrom(c).build())
            .collect(Collectors.toList());

        return this;
    }

    /**
     * Sets the connector message ID for the DomibusConnectorMessageBuilder.
     *
     * @param dcMsgId the connector message ID to set
     * @return the updated DomibusConnectorMessageBuilder instance
     */
    public DomibusConnectorMessageBuilder setConnectorMessageId(DomibusConnectorMessageId dcMsgId) {
        this.connectorMessageId = dcMsgId;
        return this;
    }

    /**
     * The internal message processing id is required.
     *
     * @param connectorMessageId - the message id
     * @return the builder
     */
    public DomibusConnectorMessageBuilder setConnectorMessageId(String connectorMessageId) {
        this.connectorMessageId = new DomibusConnectorMessageId(connectorMessageId);
        return this;
    }

    public DomibusConnectorMessageBuilder setMessageLaneId(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId) {
        this.businessDomainId = laneId;
        return this;
    }

    public DomibusConnectorMessageBuilder addRelatedConfirmations(
        List<DomibusConnectorMessageConfirmation> collect) {
        this.relatedMessageConfirmations = collect;
        return this;
    }

    public DomibusConnectorMessageBuilder addRelatedConfirmation(
        DomibusConnectorMessageConfirmation c) {
        this.relatedMessageConfirmations.add(c);
        return this;
    }
}
