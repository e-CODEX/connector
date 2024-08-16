/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.testutil;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DetachedSignature;
import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageAttachmentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDocumentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

/**
 * The DomainEntityCreator class provides static methods for creating various instances of
 * DomibusConnectorParty, DomibusConnectorAction, DomibusConnectorService,
 * DomibusConnectorMessageConfirmation, DomibusConnectorMessageAttachment, LargeFileReference,
 * DomibusConnectorMessage, DomibusConnectorMessageDetails, DomibusConnectorMessageError,
 * DomibusConnectorMessageDocument, DomibusConnectorMessageContent, and Date objects.
 *
 * <p>This class is used for testing and creating domain objects in the Domibus connector.
 */
@SuppressWarnings("squid:S1135")
@UtilityClass
public class DomainEntityCreator {
    public static final String FINAL_RECIPIENT = "finalRecipient";
    public static final String XML_CONTENT = "<xmlContent/>";
    public static final String DETACHED_SIGNATURE = "detachedSignature";
    public static final String SIGNATURE_NAME = "signaturename";
    public static final String EBMS_1 = "ebms1";
    public static final String DOCUMENT_BYTES = "documentbytes";
    public static final String PDF_DOCUMENT_NAME = "Document1.pdf";
    public static final String ORIGINAL_SENDER = "originalSender";
    public static final String URN_OASIS_NAMES_TC_EBCORE_PARTYID_TYPE_ISO_3166_1 =
        "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1";

    /**
     * Creates a DomibusConnectorParty object representing a party that acts as an initiator. The
     * party is identified by the party ID, party ID type, and role.
     *
     * @return The DomibusConnectorParty object representing the party with the role as an
     *      initiator.
     */
    public static DomibusConnectorParty createPartyATasInitiator() {
        var initiatorParty = new DomibusConnectorParty(
            "AT",
            URN_OASIS_NAMES_TC_EBCORE_PARTYID_TYPE_ISO_3166_1,
            "GW"
        );
        initiatorParty.setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        return initiatorParty;
    }

    /**
     * Creates a DomibusConnectorParty object representing a party with the country code "DE", the
     * party ID type "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", and the role "GW". The role
     * type of the party is set to RESPONDER.
     *
     * @return The DomibusConnectorParty object representing the party with the country code "DE",
     *      the party ID type "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", and the role "
     *      GW" as a RESPONDER.
     */
    public static DomibusConnectorParty createPartyDE() {
        var partyDE =
            new DomibusConnectorParty(
                "DE",
                URN_OASIS_NAMES_TC_EBCORE_PARTYID_TYPE_ISO_3166_1,
                "GW"
            );
        partyDE.setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        return partyDE;
    }

    /**
     * Creates a DomibusConnectorParty object representing a party with the ID "domibus-red", ID
     * type "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", and role "GW". This method is used
     * to create a party object for the Domibus connector.
     *
     * @return The DomibusConnectorParty object representing the party with the ID "domibus-red", ID
     *      type "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", and role "GW".
     */
    public static DomibusConnectorParty createPartyDomibusRed() {
        return new DomibusConnectorParty(
            "domibus-red", URN_OASIS_NAMES_TC_EBCORE_PARTYID_TYPE_ISO_3166_1, "GW"
        );
    }

    /**
     * Creates a DomibusConnectorParty object representing a party with the ID "domibus-blue", ID
     * type "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", and role "GW".
     *
     * @return The DomibusConnectorParty object representing the party with the ID "domibus-blue",
     *      ID type "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", and role "GW".
     */
    public static DomibusConnectorParty createPartyDomibusBlue() {
        return new DomibusConnectorParty(
            "domibus-blue", URN_OASIS_NAMES_TC_EBCORE_PARTYID_TYPE_ISO_3166_1, "GW"
        );
    }

    /**
     * Creates a DomibusConnectorAction object representing an action associated with a message in
     * the Domibus system. An action can be performed on a message, such as sending, receiving, or
     * deleting.
     *
     * <p>Instances of this class are used to store the action information of a message in the
     * DomibusConnectorPModeSet class.
     *
     * @return The DomibusConnectorAction object representing the action "Form_A".
     */
    public static DomibusConnectorAction createActionFormA() {
        return new DomibusConnectorAction("Form_A");
    }

    /**
     * Creates a DomibusConnectorAction object representing the action
     * "RelayREMMDAcceptanceRejection".
     *
     * <p>This method is used to create an action object associated with a message in the Domibus
     * system. An action can be performed on a message, such as relaying, accepting, or rejecting.
     *
     * @return The DomibusConnectorAction object representing the action
     *      "RelayREMMDAcceptanceRejection".
     */
    public static DomibusConnectorAction createActionRelayREMMDAcceptanceRejection() {
        return new DomibusConnectorAction("RelayREMMDAcceptanceRejection");
    }

    /**
     * Creates a new instance of DomibusConnectorService with the service type "EPO" and the service
     * type "urn:e-codex:services:".
     *
     * @return The DomibusConnectorService object representing the service type "EPO" and the
     *      service type "urn:e-codex:services:".
     */
    public static DomibusConnectorService createServiceEPO() {
        return new DomibusConnectorService("EPO", "urn:e-codex:services:");
    }

    /**
     * Creates a {@code DomibusConnectorMessageConfirmation} object representing the acceptance
     * confirmation for a message submission. The confirmation object includes the evidence in the
     * form of a byte array and the evidence type.
     *
     * @return The created {@code DomibusConnectorMessageConfirmation} object.
     */
    public static DomibusConnectorMessageConfirmation
    createMessageSubmissionAcceptanceConfirmation() {
        var confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_SUBMISSION_ACCEPTANCE/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
        return confirmation;
    }

    /**
     * Creates a {@code DomibusConnectorMessageConfirmation} object representing the rejection
     * confirmation for a message submission. The confirmation object includes the evidence in the
     * form of a byte array and the evidence type.
     *
     * @return The created {@code DomibusConnectorMessageConfirmation} object.
     */
    public static DomibusConnectorMessageConfirmation
    createMessageSubmissionRejectionConfirmation() {
        var confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_SUBMISSION_REJECT/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.SUBMISSION_REJECTION);
        return confirmation;
    }

    /**
     * Creates a {@code DomibusConnectorMessageConfirmation} object representing the acceptance
     * confirmation for a message relayed using REMMD (Routing Electronic Messages for Member States
     * to Domibus) protocol. The confirmation object includes the evidence in the form of a byte
     * array and the evidence type.
     *
     * @return The created {@code DomibusConnectorMessageConfirmation} object representing the
     *      acceptance confirmation for a message relayed using REMMD protocol.
     */
    public static DomibusConnectorMessageConfirmation
    createMessageRelayRemmdAcceptanceConfirmation() {
        var confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_RELAY_REMMD/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE);
        return confirmation;
    }

    /**
     * Creates a {@code DomibusConnectorMessageConfirmation} object representing the delivery
     * confirmation for a message. The confirmation object includes the evidence in the form of a
     * byte array and the evidence type.
     *
     * @return The created {@code DomibusConnectorMessageConfirmation} object.
     */
    public static DomibusConnectorMessageConfirmation createMessageDeliveryConfirmation() {
        var confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_DELIVERY/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.DELIVERY);
        return confirmation;
    }

    /**
     * Creates a {@code DomibusConnectorMessageConfirmation} object representing the non-delivery
     * confirmation for a message. The confirmation object includes the evidence in the form of a
     * byte array and the evidence type.
     *
     * @return The created {@code DomibusConnectorMessageConfirmation} object.
     */
    public static DomibusConnectorMessageConfirmation createMessageNonDeliveryConfirmation() {
        var confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_NON_DELIVERY/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.NON_DELIVERY);
        return confirmation;
    }

    /**
     * Creates a {@code DomibusConnectorMessageConfirmation} object representing the retrieval
     * evidence confirmation for a message. The confirmation object includes the evidence in the
     * form of a byte array and the evidence type.
     *
     * @return The created {@code DomibusConnectorMessageConfirmation} object representing the
     *      retrieval evidence confirmation for a message.
     */
    public static DomibusConnectorMessageConfirmation createRetrievalEvidenceMessage() {
        var confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE_RETRIEVAL/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.RETRIEVAL);
        return confirmation;
    }

    /**
     * Creates a simple message attachment for the Domibus Connector. This method creates a new
     * instance of the DomibusConnectorMessageAttachment class and initializes its properties, such
     * as attachment name, MIME type, description, and identifier.
     *
     * @return The created DomibusConnectorMessageAttachment object.
     */
    public static DomibusConnectorMessageAttachment createSimpleMessageAttachment() {
        var attachment = new DomibusConnectorMessageAttachment(
            connectorBigDataReferenceFromDataSource("attachment"), "identifier"
        );
        attachment.setName("name");
        attachment.setMimeType("application/garbage");
        attachment.setDescription("a description");
        return attachment;
    }

    /**
     * Connects a big data reference from a data source.
     *
     * @param input The input data source as a string.
     * @return The reference to the big data as a LargeFileReference object.
     */
    public static LargeFileReference connectorBigDataReferenceFromDataSource(String input) {
        var reference = new LargeFileReferenceGetSetBased();
        reference.setBytes(input.getBytes());
        reference.setReadable(true);
        return reference;
    }

    /**
     * Connects a big data reference from a data source.
     *
     * @param input The input data source as a Resource object.
     * @return The reference to the big data as a LargeFileReference object.
     */
    @SneakyThrows
    public static LargeFileReference connectorBigDataReferenceFromDataSource(Resource input) {
        var reference = new LargeFileReferenceGetSetBased();
        reference.setBytes(StreamUtils.copyToByteArray(input.getInputStream()));
        reference.setReadable(true);
        return reference;
    }

    /**
     * Creates a DomibusConnectorMessage object representing an evidence non-delivery message. The
     * message includes details, confirmation, and other properties needed for non-delivery
     * evidence.
     *
     * @return The created DomibusConnectorMessage object.
     */
    public static DomibusConnectorMessage createEvidenceNonDeliveryMessage() {
        var messageDetails = createDomibusConnectorMessageDetails();
        var nonDeliveryConfirmation = createMessageNonDeliveryConfirmation();

        return DomibusConnectorMessageBuilder
            .createBuilder()
            .setConnectorMessageId("id1")
            .setMessageDetails(
                messageDetails)
            .addTransportedConfirmations(
                nonDeliveryConfirmation)
            .build();
    }

    /**
     * Creates a simple test message for the Domibus Connector.
     *
     * <p>This method creates a simple test message, with preset values for the message details
     * including the direction, conversation ID, and EBMS message ID. The message content is
     * initially empty. This method is useful for testing purposes, as it allows the creation of a
     * message with minimum required fields.
     *
     * @return a DomibusConnectorMessage object representing the created test message.
     */
    public static DomibusConnectorMessage createSimpleTestMessage() {
        var messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
        messageDetails.setConversationId("conversation1");
        messageDetails.setEbmsMessageId(EBMS_1);

        var messageContent = new DomibusConnectorMessageContent();
        // msg.setDbMessageId(78L);
        // msg.getMessageDetails().
        return new DomibusConnectorMessage(messageDetails, messageContent);
    }

    /**
     * Creates a new instance of {@link DomibusConnectorMessage} with default values.
     *
     * @return A {@link DomibusConnectorMessage} object with default properties.
     */
    public static DomibusConnectorMessage createMessage() {
        var messageDetails = createDomibusConnectorMessageDetails();
        var messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent(XML_CONTENT.getBytes());
        var detachedSignature = new DetachedSignature(
            DETACHED_SIGNATURE.getBytes(),
            SIGNATURE_NAME,
            DetachedSignatureMimeType.BINARY
        );

        var messageDocument = new DomibusConnectorMessageDocument(
            connectorBigDataReferenceFromDataSource(DOCUMENT_BYTES), PDF_DOCUMENT_NAME,
            detachedSignature
        );

        messageContent.setDocument(messageDocument);

        var message = new DomibusConnectorMessage(messageDetails, messageContent);
        message.addTransportedMessageConfirmation(createMessageDeliveryConfirmation());
        message.addAttachment(createSimpleMessageAttachment());
        message.addError(createMessageError());
        return message;
    }

    /**
     * Creates a new EPO (Electronic Purchase Order) message for the Domibus connector.
     *
     * @return a DomibusConnectorMessage object representing the newly created EPO message.
     */
    public static DomibusConnectorMessage createEpoMessage() {
        DomibusConnectorMessageDetails messageDetails = createDomibusConnectorEpoMessageDetails();

        var messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent(XML_CONTENT.getBytes());

        var detachedSignature = new DetachedSignature(
            DETACHED_SIGNATURE.getBytes(),
            SIGNATURE_NAME,
            DetachedSignatureMimeType.BINARY
        );

        var messageDocument = new DomibusConnectorMessageDocument(
            connectorBigDataReferenceFromDataSource(DOCUMENT_BYTES),
            PDF_DOCUMENT_NAME,
            detachedSignature
        );
        messageContent.setDocument(messageDocument);

        return DomibusConnectorMessageBuilder
            .createBuilder()
            .addAttachment(createSimpleMessageAttachment())
            .setMessageDetails(messageDetails)
            .setMessageContent(messageContent)
            .setConnectorMessageId("MSG1")
            .build();
    }

    /**
     * Creates a DomibusConnectorMessage for the EPO message form A from GWDomibusRed.
     *
     * @return The created DomibusConnectorMessage.
     */
    public static DomibusConnectorMessage createEpoMessageFormAFromGWDomibusRed() {
        var messageDetails = createDomibusConnectorEpoMessageFormAFromGWDomibusRed();

        var messageContent =
            new DomibusConnectorMessageContent(); // TODO: should be a asic container
        messageContent.setXmlContent(XML_CONTENT.getBytes());

        var detachedSignature = new DetachedSignature(
            DETACHED_SIGNATURE.getBytes(),
            SIGNATURE_NAME,
            DetachedSignatureMimeType.BINARY
        );

        var messageDocument = new DomibusConnectorMessageDocument(
            connectorBigDataReferenceFromDataSource(DOCUMENT_BYTES),
            PDF_DOCUMENT_NAME,
            detachedSignature
        );
        messageContent.setDocument(messageDocument);

        var message = DomibusConnectorMessageBuilder
            .createBuilder()
            .addAttachment(createSimpleMessageAttachment())
            .setMessageDetails(messageDetails)
            .setMessageContent(messageContent)
            .build();

        message.addTransportedMessageConfirmation(createMessageSubmissionAcceptanceConfirmation());

        return message;
    }

    /**
     * Creates a relay Remmd acceptance evidence for a given message.
     *
     * @param message the message for which the relay Remmd acceptance evidence is being created
     * @return a new DomibusConnectorMessage object that represents the relay Remmd acceptance
     *      evidence for the given message
     */
    public static DomibusConnectorMessage createRelayRemmdAcceptanceEvidenceForMessage(
        DomibusConnectorMessage message) {
        var messageDetails = new DomibusConnectorMessageDetails();
        BeanUtils.copyProperties(message.getMessageDetails(), messageDetails);

        // messageDetails.setConversationId(null);      //first message no conversation set yet!
        messageDetails.setEbmsMessageId(null); // message from backend
        messageDetails.setBackendMessageId(null);   // has not been processed by the backend yet
        messageDetails.setFinalRecipient(null);
        messageDetails.setOriginalSender(null);
        // reference the previous message
        messageDetails.setRefToMessageId(message.getMessageDetails().getEbmsMessageId());

        messageDetails.setAction(createActionFormA());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(message.getMessageDetails().getFromParty());
        messageDetails.setFromParty(message.getMessageDetails().getToParty());

        var messageDeliveryConfirmation = createMessageRelayRemmdAcceptanceConfirmation();
        // TODO: load correct xml
        //  messageDeliveryConfirmation.setEvidence("<xml></xml>".getBytes());

        return DomibusConnectorMessageBuilder
            .createBuilder()
            .setMessageDetails(messageDetails)
            .addTransportedConfirmations(messageDeliveryConfirmation)
            .build();
    }

    /**
     * Creates an evidence message for a given message and confirmation.
     *
     * @param message      the original message for which the evidence message is created
     * @param confirmation the confirmation for the original message
     * @return the evidence message created
     */
    public static DomibusConnectorMessage creatEvidenceMsgForMessage(
        DomibusConnectorMessage message, DomibusConnectorMessageConfirmation confirmation) {
        var messageDetails = new DomibusConnectorMessageDetails();
        BeanUtils.copyProperties(message.getMessageDetails(), messageDetails);

        messageDetails.setConversationId(message.getMessageDetails().getConversationId());
        messageDetails.setEbmsMessageId(null);
        messageDetails.setBackendMessageId(null);
        messageDetails.setFinalRecipient(message.getMessageDetails().getOriginalSender());
        messageDetails.setOriginalSender(message.getMessageDetails().getFinalRecipient());
        messageDetails.setRefToMessageId(
            message.getMessageDetails().getEbmsMessageId());     // reference the previous message

        messageDetails.setAction(createActionFormA());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(message.getMessageDetails().getFromParty());
        messageDetails.setFromParty(message.getMessageDetails().getToParty());
        // DomibusConnectorMessageConfirmation messageDeliveryConfirmation
        // = createMessageRelayRemmdAcceptanceConfirmation();

        // TODO: load correct xml
        //  messageDeliveryConfirmation.setEvidence("<xml></xml>".getBytes());

        return DomibusConnectorMessageBuilder
            .createBuilder()
            .setMessageDetails(messageDetails)
            .addTransportedConfirmations(confirmation)
            .build();
    }

    /**
     * Creates a delivery evidence for the given message.
     *
     * @param message the message for which to create the delivery evidence
     * @return the created delivery evidence as a {@code DomibusConnectorMessage} object
     */
    public static DomibusConnectorMessage createDeliveryEvidenceForMessage(
        DomibusConnectorMessage message) {
        var messageDetails = new DomibusConnectorMessageDetails();
        BeanUtils.copyProperties(message.getMessageDetails(), messageDetails);
        // messageDetails.setConversationId(null);      //first message no conversation set yet!
        messageDetails.setEbmsMessageId(null); // message from backend
        messageDetails.setBackendMessageId(null);   // has not been processed by the backend yet
        messageDetails.setFinalRecipient(null);
        messageDetails.setOriginalSender(null);
        // reference the previous message
        messageDetails.setRefToMessageId(message.getMessageDetails().getEbmsMessageId());

        messageDetails.setAction(createActionFormA());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(createPartyATasInitiator());
        messageDetails.setFromParty(createPartyDE());

        var messageDeliveryConfirmation = createMessageDeliveryConfirmation();
        // TODO: load correct xml
        //  messageDeliveryConfirmation.setEvidence("<xml></xml>".getBytes());

        return DomibusConnectorMessageBuilder
            .createBuilder()
            .setMessageDetails(messageDetails)
            .addTransportedConfirmations(messageDeliveryConfirmation)
            .build();
    }

    /**
     * Creates an instance of {@code DomibusConnectorMessageDetails} with default values for EPO
     * message details. This method is typically used to create the message details for the first
     * message in a conversation.
     *
     * @return an instance of {@code DomibusConnectorMessageDetails} with default values for EPO
     *      message details
     */
    public static DomibusConnectorMessageDetails createDomibusConnectorEpoMessageDetails() {
        var messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId(null);      // first message no conversation set yet!
        messageDetails.setEbmsMessageId(EBMS_1);
        messageDetails.setBackendMessageId(null);   // has not been processed by the backend yet
        messageDetails.setFinalRecipient(FINAL_RECIPIENT);
        messageDetails.setOriginalSender(ORIGINAL_SENDER);
        messageDetails.setRefToMessageId(null);     // is the first message
        messageDetails.setAction(createActionFormA());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(createPartyATasInitiator());
        messageDetails.setFromParty(createPartyDE());

        return messageDetails;
    }

    /**
     * Creates a DomibusConnectorMessageDetails object for a specific scenario. This method
     * initializes the properties of the object with default values. The conversationId,
     * ebmsMessageId, backendMessageId, and refToMessageId properties are set to specific values
     * based on the given scenario.
     *
     * @return a DomibusConnectorMessageDetails object with default property values
     */
    public static DomibusConnectorMessageDetails
    createDomibusConnectorEpoMessageFormAFromGWDomibusRed() {
        var messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId("conv567");      // first message no conversation set yet!
        messageDetails.setEbmsMessageId("ebms5123");
        messageDetails.setBackendMessageId(null);   // has not been processed by the backend yet
        messageDetails.setFinalRecipient(FINAL_RECIPIENT);
        messageDetails.setOriginalSender(ORIGINAL_SENDER);
        messageDetails.setRefToMessageId(null);     // is the first message

        messageDetails.setAction(createActionFormA());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(createPartyDomibusBlue());
        messageDetails.setFromParty(createPartyDomibusRed());

        return messageDetails;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageDetails and sets its properties to
     * predefined values.
     *
     * @return a DomibusConnectorMessageDetails object with predefined property values:
     *         - conversationId set to "conversation1"
     *         - ebmsMessageId set to "ebms1"
     *         - backendMessageId set to "national1"
     *         - finalRecipient set to "finalRecipient"
     *         - originalSender set to "originalSender"
     *         - refToMessageId set to "refToMessageId"
     *         - direction set to DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY
     *         - deliveredToGateway set to the parsed date and time "2018-01-01 12:12:12"
     *         - deliveredToBackend set to the parsed date and time "2018-01-01 12:12:12"
     *         - action set to the result of the createActionFormA method
     *         - service set to the result of the createServiceEPO method
     *         - toParty set to the result of the createPartyATasInitiator method
     *         - fromParty set to the result of the createPartyDE method
     */
    public static DomibusConnectorMessageDetails createDomibusConnectorMessageDetails() {
        var messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId("conversation1");
        messageDetails.setEbmsMessageId(EBMS_1);
        messageDetails.setBackendMessageId("national1");
        messageDetails.setFinalRecipient(FINAL_RECIPIENT);
        messageDetails.setOriginalSender(ORIGINAL_SENDER);
        messageDetails.setRefToMessageId("refToMessageId");

        messageDetails.setDirection(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
        messageDetails.setDeliveredToGateway(parseDateTime("2018-01-01 12:12:12"));
        messageDetails.setDeliveredToBackend(parseDateTime("2018-01-01 12:12:12"));

        messageDetails.setAction(createActionFormA());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(createPartyATasInitiator());
        messageDetails.setFromParty(createPartyDE());

        return messageDetails;
    }

    /**
     * Creates an error message with #createSimpleDomibusConnectorMessage as message "error detail
     * message" as details "error message" as text "error source" as error source.
     *
     * @return the MessageError
     */
    public static DomibusConnectorMessageError createMessageError() {
        return DomibusConnectorMessageErrorBuilder
            .createBuilder()
            .setSource(Object.class)
            .setDetails("error detail message")
            .setText("error message")
            .build();
    }

    /**
     * Creates a message attachment using the {@link DomibusConnectorMessageAttachmentBuilder}.
     *
     * @return a {@link DomibusConnectorMessageAttachment} object representing the created message
     *      attachment.
     */
    public static DomibusConnectorMessageAttachment createMessageAttachment() {
        return DomibusConnectorMessageAttachmentBuilder
            .createBuilder()
            .setAttachment(connectorBigDataReferenceFromDataSource("attachment"))
            .setIdentifier("identifier")
            .build();
    }

    /**
     * Creates a document without a signature.
     *
     * <p>This method creates a document without a signature using a pre-defined PDF file.
     * The document is built using the {@link DomibusConnectorMessageDocumentBuilder} with the name
     * set to "name" and the content set as the PDF file obtained from the classpath resource
     * "/pdf/ExamplePdf.pdf".
     *
     * @return the created {@link DomibusConnectorMessageDocument} without a signature
     */
    public static DomibusConnectorMessageDocument createDocumentWithNoSignature() {
        var pdf = new ClassPathResource("/pdf/ExamplePdf.pdf");
        return DomibusConnectorMessageDocumentBuilder
            .createBuilder()
            .setName("name")
            .setContent(connectorBigDataReferenceFromDataSource(pdf))
            .build();
    }

    /**
     * Creates a document with signature.
     *
     * @return a {@link DomibusConnectorMessageDocument} object representing the created document
     *      with signature.
     */
    public static DomibusConnectorMessageDocument createDocumentWithSignature() {
        var pdf = new ClassPathResource("/pdf/ExamplePdfSigned.pdf");
        return DomibusConnectorMessageDocumentBuilder
            .createBuilder()
            .setName("name")
            .setContent(connectorBigDataReferenceFromDataSource(pdf))
            .build();
    }

    /**
     * Creates a message content object with a document that has no digital signature.
     *
     * @return A {@link DomibusConnectorMessageContent} object that contains the XML content and the
     *      document
     * @throws RuntimeException if there is an UnsupportedEncodingException
     */
    public static DomibusConnectorMessageContent createMessageContentWithDocumentWithNoSignature() {
        var messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent(XML_CONTENT.getBytes(StandardCharsets.UTF_8));
        messageContent.setDocument(createDocumentWithNoSignature());

        return messageContent;
    }

    /**
     * Creates a message content object with XML content and no PDF document.
     *
     * @return a DomibusConnectorMessageContent object with XML content and no PDF document.
     */
    public static DomibusConnectorMessageContent
    createMessageContentWithDocumentWithNoPdfDocument() {
        var messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent(XML_CONTENT.getBytes(StandardCharsets.UTF_8));
        messageContent.setDocument(null);

        return messageContent;
    }

    public static final String DATE_FORMAT = "YYYY-MM-DD HH:mm:ss";

    /**
     * Parses a string representation of a date and time into a {@link Date} object.
     *
     * @param date the string representation of the date and time
     * @return the parsed Date object
     * @throws RuntimeException if the string cannot be parsed into a Date object
     */
    public static Date parseDateTime(String date) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("date parse error!", e);
        }
    }
}
