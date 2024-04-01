package eu.domibus.connector.domain.testutil;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageAttachmentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDocumentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DomainEntityCreator {
    public static String dateFormat = "YYYY-MM-DD HH:mm:ss";
    public static DomibusConnectorParty createPartyATasInitiator() {
        DomibusConnectorParty p =
                new DomibusConnectorParty("AT", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", "GW");
        p.setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        return p;
    }

    public static DomibusConnectorParty createPartyDE() {
        DomibusConnectorParty p =
                new DomibusConnectorParty("DE", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", "GW");
        p.setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        return p;
    }

    public static DomibusConnectorParty createPartyDomibusRed() {
        DomibusConnectorParty p =
                new DomibusConnectorParty("domibus-red", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", "GW");
        return p;
    }

    public static DomibusConnectorParty createPartyDomibusBlue() {
        DomibusConnectorParty p =
                new DomibusConnectorParty("domibus-blue", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", "GW");
        return p;
    }

    public static DomibusConnectorAction createActionForm_A() {
        DomibusConnectorAction a = new DomibusConnectorAction("Form_A");
        return a;
    }

    public static DomibusConnectorAction createActionRelayREMMDAcceptanceRejection() {
        DomibusConnectorAction a = new DomibusConnectorAction("RelayREMMDAcceptanceRejection");
        return a;
    }

    public static DomibusConnectorService createServiceEPO() {
        DomibusConnectorService s = new DomibusConnectorService("EPO", "urn:e-codex:services:");
        return s;
    }

    public static DomibusConnectorMessageConfirmation createMessageSubmissionAcceptanceConfirmation() {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_SUBMISSION_ACCEPTANCE/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
        return confirmation;
    }

    public static DomibusConnectorMessageConfirmation createMessageSubmissionRejectionConfirmation() {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_SUBMISSION_REJECT/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.SUBMISSION_REJECTION);
        return confirmation;
    }

    public static DomibusConnectorMessageConfirmation createMessageRelayRemmdAcceptanceConfirmation() {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_RELAY_REMMD/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE);
        return confirmation;
    }

    public static DomibusConnectorMessageConfirmation createMessageDeliveryConfirmation() {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_DELIVERY/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.DELIVERY);
        return confirmation;
    }

    public static DomibusConnectorMessageConfirmation createMessageNonDeliveryConfirmation() {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE1_NON_DELIVERY/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.NON_DELIVERY);
        return confirmation;
    }

    public static DomibusConnectorMessageConfirmation createRetrievalEvidenceMessage() {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("<EVIDENCE_RETRIEVAL/>".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.RETRIEVAL);
        return confirmation;
    }

    public static DomibusConnectorMessageAttachment createSimpleMessageAttachment() {
        DomibusConnectorMessageAttachment attachment = new DomibusConnectorMessageAttachment(
                connectorBigDataReferenceFromDataSource("attachment"), "identifier"
        );

        attachment.setName("name");
        attachment.setMimeType("application/garbage");
        attachment.setDescription("a description");
        return attachment;
    }

    public static LargeFileReference connectorBigDataReferenceFromDataSource(String input) {
        LargeFileReferenceGetSetBased reference = new LargeFileReferenceGetSetBased();
        reference.setBytes(input.getBytes());
        reference.setReadable(true);

        return reference;
    }

    @SneakyThrows
    public static LargeFileReference connectorBigDataReferenceFromDataSource(Resource input) {
        LargeFileReferenceGetSetBased reference = new LargeFileReferenceGetSetBased();
        reference.setBytes(StreamUtils.copyToByteArray(input.getInputStream()));
        reference.setReadable(true);

        return reference;
    }

    public static DomibusConnectorMessage createEvidenceNonDeliveryMessage() {
        DomibusConnectorMessageDetails messageDetails = createDomibusConnectorMessageDetails();
        DomibusConnectorMessageConfirmation nonDeliveryConfirmation = createMessageNonDeliveryConfirmation();

        return DomibusConnectorMessageBuilder
                .createBuilder()
                .setConnectorMessageId("id1")
                .setMessageDetails(messageDetails)
                .addTransportedConfirmations(nonDeliveryConfirmation)
                .build();
    }

    public static DomibusConnectorMessage createSimpleTestMessage() {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
        messageDetails.setConversationId("conversation1");
        messageDetails.setEbmsMessageId("ebms1");

        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        DomibusConnectorMessage msg = new DomibusConnectorMessage(messageDetails, messageContent);
        // msg.setDbMessageId(78L);
        // msg.getMessageDetails().
        return msg;
    }

    public static DomibusConnectorMessage createMessage() {
        DomibusConnectorMessageDetails messageDetails = createDomibusConnectorMessageDetails();

        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent("<xmlContent/>".getBytes());

        DetachedSignature detachedSignature = new DetachedSignature(
                "detachedSignature".getBytes(),
                "signaturename",
                DetachedSignatureMimeType.BINARY
        );

        DomibusConnectorMessageDocument messageDocument = new DomibusConnectorMessageDocument(
                connectorBigDataReferenceFromDataSource("documentbytes"),
                "Document1.pdf",
                detachedSignature
        );

        messageContent.setDocument(messageDocument);

        DomibusConnectorMessage msg = new DomibusConnectorMessage(messageDetails, messageContent);
        msg.addTransportedMessageConfirmation(createMessageDeliveryConfirmation());
        msg.addAttachment(createSimpleMessageAttachment());
        msg.addError(createMessageError());
        return msg;
    }

    public static DomibusConnectorMessage createEpoMessage() {
        DomibusConnectorMessageDetails messageDetails = createDomibusConnectorEpoMessageDetails();

        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent("<xmlContent/>".getBytes());

        DetachedSignature detachedSignature = new DetachedSignature(
                "detachedSignature".getBytes(),
                "signaturename",
                DetachedSignatureMimeType.BINARY
        );

        DomibusConnectorMessageDocument messageDocument = new DomibusConnectorMessageDocument(
                connectorBigDataReferenceFromDataSource("documentbytes"),
                "Document1.pdf",
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

    public static DomibusConnectorMessage createEpoMessageFormAFromGwdomibusRed() {
        DomibusConnectorMessageDetails messageDetails = createDomibusConnectorEpoMessageFormAFromGWdomibusRed();
        // TODO: should be a asic container
        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent("<xmlContent/>".getBytes());

        DetachedSignature detachedSignature = new DetachedSignature(
                "detachedSignature".getBytes(),
                "signaturename",
                DetachedSignatureMimeType.BINARY
        );

        DomibusConnectorMessageDocument messageDocument = new DomibusConnectorMessageDocument(
                connectorBigDataReferenceFromDataSource("documentbytes"),
                "Document1.pdf",
                detachedSignature
        );
        messageContent.setDocument(messageDocument);

        DomibusConnectorMessage message = DomibusConnectorMessageBuilder
                .createBuilder()
                .addAttachment(createSimpleMessageAttachment())
                .setMessageDetails(messageDetails)
                .setMessageContent(messageContent)
                .build();
        message.addTransportedMessageConfirmation(createMessageSubmissionAcceptanceConfirmation());

        return message;
    }

    public static DomibusConnectorMessage createRelayRemmdAcceptanceEvidenceForMessage(DomibusConnectorMessage message) {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        BeanUtils.copyProperties(message.getMessageDetails(), messageDetails);

        // messageDetails.setConversationId(null);      //first message no conversation set yet!
        messageDetails.setEbmsMessageId(null); // message from backend
        messageDetails.setBackendMessageId(null);   // has not been processed by the backend yet
        messageDetails.setFinalRecipient(null);
        messageDetails.setOriginalSender(null);
        // reference the previous message
        messageDetails.setRefToMessageId(message.getMessageDetails().getEbmsMessageId());

        messageDetails.setAction(createActionForm_A());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(message.getMessageDetails().getFromParty());
        messageDetails.setFromParty(message.getMessageDetails().getToParty());

        DomibusConnectorMessageConfirmation messageDeliveryConfirmation =
                createMessageRelayRemmdAcceptanceConfirmation();

        // TODO: load correct xml
        // messageDeliveryConfirmation.setEvidence("<xml></xml>".getBytes());

        return DomibusConnectorMessageBuilder
                .createBuilder()
                .setMessageDetails(messageDetails)
                .addTransportedConfirmations(messageDeliveryConfirmation)
                .build();
    }

    public static DomibusConnectorMessage creatEvidenceMsgForMessage(
            DomibusConnectorMessage message,
            DomibusConnectorMessageConfirmation confirmation) {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        BeanUtils.copyProperties(message.getMessageDetails(), messageDetails);

        messageDetails.setConversationId(message.getMessageDetails().getConversationId());
        messageDetails.setEbmsMessageId(null);
        messageDetails.setBackendMessageId(null);
        messageDetails.setFinalRecipient(message.getMessageDetails().getOriginalSender());
        messageDetails.setOriginalSender(message.getMessageDetails().getFinalRecipient());
        messageDetails.setRefToMessageId(message.getMessageDetails()
                                                .getEbmsMessageId()); // reference the previous message

        messageDetails.setAction(createActionForm_A());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(message.getMessageDetails().getFromParty());
        messageDetails.setFromParty(message.getMessageDetails().getToParty());

        // DomibusConnectorMessageConfirmation messageDeliveryConfirmation =
        // createMessageRelayRemmdAcceptanceConfirmation();
        // messageDeliveryConfirmation.setEvidence("<xml></xml>".getBytes()); //TODO: load correct xml

        return DomibusConnectorMessageBuilder
                .createBuilder()
                .setMessageDetails(messageDetails)
                .addTransportedConfirmations(confirmation)
                .build();
    }

    public static DomibusConnectorMessage createDeliveryEvidenceForMessage(DomibusConnectorMessage message) {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        BeanUtils.copyProperties(message.getMessageDetails(), messageDetails);
        // messageDetails.setConversationId(null);      //first message no conversation set yet!
        messageDetails.setEbmsMessageId(null); // message from backend
        messageDetails.setBackendMessageId(null);   // has not been processed by the backend yet
        messageDetails.setFinalRecipient(null);
        messageDetails.setOriginalSender(null);
        // reference the previous message
        messageDetails.setRefToMessageId(message.getMessageDetails().getEbmsMessageId());

        messageDetails.setAction(createActionForm_A());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(createPartyATasInitiator());
        messageDetails.setFromParty(createPartyDE());

        DomibusConnectorMessageConfirmation messageDeliveryConfirmation = createMessageDeliveryConfirmation();

        // messageDeliveryConfirmation.setEvidence("<xml></xml>".getBytes()); //TODO: load correct xml

        return DomibusConnectorMessageBuilder
                .createBuilder()
                .setMessageDetails(messageDetails)
                .addTransportedConfirmations(messageDeliveryConfirmation)
                .build();
    }

    public static DomibusConnectorMessageDetails createDomibusConnectorEpoMessageDetails() {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId(null);      // first message no conversation set yet!
        messageDetails.setEbmsMessageId("ebms1");
        messageDetails.setBackendMessageId(null);   // has not been processed by the backend yet
        messageDetails.setFinalRecipient("finalRecipient");
        messageDetails.setOriginalSender("originalSender");
        messageDetails.setRefToMessageId(null);     // is the first message

        messageDetails.setAction(createActionForm_A());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(createPartyATasInitiator());
        messageDetails.setFromParty(createPartyDE());

        return messageDetails;
    }

    public static DomibusConnectorMessageDetails createDomibusConnectorEpoMessageFormAFromGWdomibusRed() {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId("conv567");      // first message no conversation set yet!
        messageDetails.setEbmsMessageId("ebms5123");
        messageDetails.setBackendMessageId(null);   // has not been processed by the backend yet
        messageDetails.setFinalRecipient("finalRecipient");
        messageDetails.setOriginalSender("originalSender");
        messageDetails.setRefToMessageId(null);     // is the first message

        messageDetails.setAction(createActionForm_A());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(createPartyDomibusBlue());
        messageDetails.setFromParty(createPartyDomibusRed());

        return messageDetails;
    }

    public static DomibusConnectorMessageDetails createDomibusConnectorMessageDetails() {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId("conversation1");
        messageDetails.setEbmsMessageId("ebms1");
        messageDetails.setBackendMessageId("national1");
        messageDetails.setFinalRecipient("finalRecipient");
        messageDetails.setOriginalSender("originalSender");
        messageDetails.setRefToMessageId("refToMessageId");

        messageDetails.setDirection(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
        messageDetails.setDeliveredToGateway(parseDateTime("2018-01-01 12:12:12"));
        messageDetails.setDeliveredToBackend(parseDateTime("2018-01-01 12:12:12"));

        messageDetails.setAction(createActionForm_A());
        messageDetails.setService(createServiceEPO());
        messageDetails.setToParty(createPartyATasInitiator());
        messageDetails.setFromParty(createPartyDE());

        return messageDetails;
    }

    /**
     * creates a error message with
     * #createSimpleDomibusConnectorMessage as message
     * "error detail message" as details
     * "error message" as text
     * "error source" as error source
     *
     * @return the MessageError
     */
    public static DomibusConnectorMessageError createMessageError() {
        // DomibusConnectorMessageError error = new DomibusConnectorMessageError("error message", "error detail
        // message", "error source");
        // return error;
        return DomibusConnectorMessageErrorBuilder
                .createBuilder()
                .setSource(Object.class)
                .setDetails("error detail message")
                .setText("error message")
                .build();
    }

    public static DomibusConnectorMessageAttachment createMessageAttachment() {
        return DomibusConnectorMessageAttachmentBuilder
                .createBuilder()
                .setAttachment(connectorBigDataReferenceFromDataSource("attachment"))
                .setIdentifier("identifier")
                .build();
    }

    public static DomibusConnectorMessageDocument createDocumentWithNoSignature() {
        ClassPathResource pdf = new ClassPathResource("/pdf/ExamplePdf.pdf");
        return DomibusConnectorMessageDocumentBuilder
                .createBuilder()
                .setName("name")
                .setContent(connectorBigDataReferenceFromDataSource(pdf))
                .build();
    }

    public static DomibusConnectorMessageDocument createDocumentWithSignature() {
        ClassPathResource pdf = new ClassPathResource("/pdf/ExamplePdfSigned.pdf");
        return DomibusConnectorMessageDocumentBuilder
                .createBuilder()
                .setName("name")
                .setContent(connectorBigDataReferenceFromDataSource(pdf))
                .build();
    }

    public static DomibusConnectorMessageContent createMessageContentWithDocumentWithNoSignature() {
        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent("<xmlContent/>".getBytes(StandardCharsets.UTF_8));
        messageContent.setDocument(createDocumentWithNoSignature());

        return messageContent;
    }

    public static DomibusConnectorMessageContent createMessageContentWithDocumentWithNoPdfDocument() {
        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent("<xmlContent/>".getBytes(StandardCharsets.UTF_8));
        messageContent.setDocument(null);

        return messageContent;
    }

    public static Date parseDateTime(String date) {
        try {
            return new SimpleDateFormat(dateFormat).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("date parse error!", e);
        }
    }
}
