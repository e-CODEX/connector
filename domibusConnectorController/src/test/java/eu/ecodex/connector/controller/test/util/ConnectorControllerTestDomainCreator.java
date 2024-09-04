/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.test.util;

import eu.ecodex.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.ecodex.connector.domain.model.DetachedSignature;
import eu.ecodex.connector.domain.model.DetachedSignatureMimeType;
import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageContent;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDetails;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDocument;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageError;
import eu.ecodex.connector.domain.model.DomibusConnectorParty;
import eu.ecodex.connector.domain.model.DomibusConnectorService;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import eu.ecodex.connector.domain.testutil.LargeFileReferenceGetSetBased;

/**
 * This class contains static methods to create various objects used for testing the
 * ConnectorController class in Domibus.
 */
public class ConnectorControllerTestDomainCreator {
    /**
     * Creates a new instance of DomibusConnectorParty representing a party in the AT country.
     *
     * @return A new instance of DomibusConnectorParty with the partyId, partyIdType, and role set.
     */
    public static DomibusConnectorParty createPartyAT() {
        DomibusConnectorParty p =
            new DomibusConnectorParty(
                "AT", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", "GW"
            );
        return p;
    }

    /**
     * Creates a new instance of DomibusConnectorParty representing a party in Germany (DE).
     *
     * @return A new instance of DomibusConnectorParty with the partyId, partyIdType,
     *      and role set for Germany.
     */
    public static DomibusConnectorParty createPartyDE() {
        DomibusConnectorParty p =
            new DomibusConnectorParty(
                "DE", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", "GW"
            );
        return p;
    }

    /**
     * Creates and returns a new instance of {@link DomibusConnectorAction} with
     * the specified action.
     *
     * @return A new instance of {@link DomibusConnectorAction} with the specified action.
     */
    public static DomibusConnectorAction createActionForm_A() {
        DomibusConnectorAction a = new DomibusConnectorAction("Form_A");
        // DomibusConnectorAction a = new DomibusConnectorAction("Form_A", true);
        return a;
    }

    /**
     * Creates and returns a new instance of {@link DomibusConnectorAction} with
     * the action "RelayREMMDAcceptanceRejection".
     *
     * @return A new instance of {@link DomibusConnectorAction} with the specified action.
     */
    public static DomibusConnectorAction createActionRelayREMMDAcceptanceRejection() {
        DomibusConnectorAction a = new DomibusConnectorAction("RelayREMMDAcceptanceRejection");
        // DomibusConnectorAction a = new
        // DomibusConnectorAction("RelayREMMDAcceptanceRejection", true);
        return a;
    }

    public static DomibusConnectorService createServiceEPO() {
        DomibusConnectorService s = new DomibusConnectorService("EPO", "urn:e-codex:services:");
        return s;
    }

    /**
     * Creates a new instance of {@link DomibusConnectorMessageConfirmation} with default values.
     *
     * @return A new instance of {@link DomibusConnectorMessageConfirmation} with default values.
     */
    public static DomibusConnectorMessageConfirmation createMessageDeliveryConfirmation() {
        DomibusConnectorMessageConfirmation confirmation =
            new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("EVIDENCE1_DELIVERY".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.DELIVERY);
        return confirmation;
    }

    /**
     * Creates a new instance of {@link DomibusConnectorMessageConfirmation}
     * for non-delivery confirmation.
     * The method sets the evidence type to {@link DomibusConnectorEvidenceType#NON_DELIVERY}
     * and initializes the evidence field as null.
     *
     * @return A new instance of {@link DomibusConnectorMessageConfirmation} for non-delivery
     *      confirmation.
     */
    public static DomibusConnectorMessageConfirmation createMessageNonDeliveryConfirmation() {
        DomibusConnectorMessageConfirmation confirmation =
            new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("EVIDENCE1_NON_DELIVERY".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.NON_DELIVERY);
        return confirmation;
    }

    /**
     * Creates a simple message attachment.
     *
     * @return A new instance of DomibusConnectorMessageAttachment with the attachment and
     *      identifier set, and default values for other attributes.
     */
    public static DomibusConnectorMessageAttachment createSimpleMessageAttachment() {

        LargeFileReferenceGetSetBased inMemory = new LargeFileReferenceGetSetBased();
        inMemory.setBytes("attachment".getBytes());

        DomibusConnectorMessageAttachment attachment =
            new DomibusConnectorMessageAttachment(inMemory, "identifier");
        attachment.setName("name");
        attachment.setMimeType("application/garbage");
        return attachment;
    }

    /**
     * Creates a simple test message with default values for the message details and content.
     *
     * @return A new instance of DomibusConnectorMessage with the message details and content set.
     */
    public static DomibusConnectorMessage createSimpleTestMessage() {

        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId("conversation1");
        messageDetails.setEbmsMessageId("ebms1");

        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        DomibusConnectorMessage msg = new DomibusConnectorMessage(messageDetails, messageContent);
        // msg.setDbMessageId(78L);
        // msg.getMessageDetails().
        return msg;
    }

    /**
     * Creates a new instance of {@link DomibusConnectorMessage} with default values for
     * the message details and content.
     * The method sets the message content with an XML content and a document attachment.
     * It also adds a message delivery confirmation, a simple message attachment, and a message
     * error to the message.
     *
     * @return A new instance of {@link DomibusConnectorMessage} with the message details
     *      and content set.
     */
    public static DomibusConnectorMessage createMessage() {
        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent("xmlContent".getBytes());

        DetachedSignature detachedSignature =
            new DetachedSignature("detachedSignature".getBytes(), "signaturename",
                                  DetachedSignatureMimeType.BINARY
            );

        LargeFileReferenceGetSetBased inMemory = new LargeFileReferenceGetSetBased();
        inMemory.setBytes("documentbytes".getBytes());
        inMemory.setReadable(true);

        DomibusConnectorMessageDocument messageDocument =
            new DomibusConnectorMessageDocument(inMemory, "Document1.pdf", detachedSignature);

        messageContent.setDocument(messageDocument);

        DomibusConnectorMessageDetails messageDetails = createDomibusConnectorMessageDetails();

        DomibusConnectorMessage msg = new DomibusConnectorMessage(messageDetails, messageContent);
        msg.addTransportedMessageConfirmation(createMessageDeliveryConfirmation());
        msg.addAttachment(createSimpleMessageAttachment());
        msg.addError(createMessageError());
        return msg;
    }

    /**
     * Creates and initializes a new instance of DomibusConnectorMessageDetails.
     *
     * @return A new instance of DomibusConnectorMessageDetails with the
     *      conversationId, ebmsMessageId, and backendMessageId set.
     */
    public static DomibusConnectorMessageDetails createDomibusConnectorMessageDetails() {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId("conversation1");
        messageDetails.setEbmsMessageId("ebms1");
        messageDetails.setBackendMessageId("national1");
        messageDetails.setFinalRecipient("finalRecipient");
        messageDetails.setOriginalSender("originalSender");
        messageDetails.setRefToMessageId("refToMessageId");

        messageDetails.setAction(createActionForm_A());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(createPartyAT());
        messageDetails.setFromParty(createPartyDE());

        return messageDetails;
    }

    /**
     * creates an error message with #createSimpleDomibusConnectorMessage as message.
     * "error detail message" as details
     * "error message" as text
     * "error source" as error source
     *
     * @return the MessageError
     */
    public static DomibusConnectorMessageError createMessageError() {
        return DomibusConnectorMessageErrorBuilder.createBuilder()
            .setDetails("error detail message")
            .setText("error message")
            .setSource("error source")
            .build();
    }
}
