/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.testdata;

import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageErrorType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.transform.stream.StreamSource;
import lombok.experimental.UtilityClass;
import org.apache.cxf.attachment.ByteDataSource;

/**
 * Helper class to create TransitionModel Objects for testing.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@UtilityClass
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class TransitionCreator {
    public static final String APPLICATION_OCTET_STREAM_MIME_TYPE = "application/octet-stream";

    /**
     * Creates a new instance of DomibusConnectorMessagesType and adds a single message to it.
     *
     * @return the created DomibusConnectorMessagesType object with the added message
     */
    public static DomibusConnectorMessagesType createMessages() {
        var messages = new DomibusConnectorMessagesType();
        messages.getMessages().add(createMessage());
        return messages;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageType and initializes its properties.
     *
     * @return the created DomibusConnectorMessageType object
     */
    public static DomibusConnectorMessageType createEpoMessage() {
        var message = new DomibusConnectorMessageType();
        message.setMessageDetails(createEpoMessageDetails());
        message.setMessageContent(createMessageContent());
        message.getMessageAttachments().add(createMessageAttachment());

        return message;
    }

    /**
     * Creates a new instance of {@link DomibusConnectorMessageType} and initializes its
     * properties.
     *
     * @return the created DomibusConnectorMessageType object
     */
    public static DomibusConnectorMessageType createMessage() {
        var message = new DomibusConnectorMessageType();
        message.setMessageDetails(createMessageDetails());
        message.setMessageContent(createMessageContent());
        message.getMessageConfirmations().add(createMessageConfirmationType_DELIVERY());
        message.getMessageErrors().add(createMessageError());
        message.getMessageAttachments().add(createMessageAttachment());

        return message;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageType with the necessary properties
     * initialized to create an evidence non-delivery message.
     *
     * @return the created DomibusConnectorMessageType object for evidence non-delivery message
     */
    public static DomibusConnectorMessageType createEvidenceNonDeliveryMessage() {
        var message = new DomibusConnectorMessageType();
        message.setMessageDetails(createMessageDetails());
        message.getMessageConfirmations().add(createMessageConfirmationType_NON_DELIVERY());

        return message;
    }

    /**
     * Creates a new instance of {@link DomibusConnectorMessageContentType} and initializes its
     * properties. The xmlContent property is set with a {@link StreamSource} object containing the
     * XML content. The document property is set with a new instance of
     * {@link DomibusConnectorMessageDocumentType}. The detachedSignature property of the document
     * object is set with a new instance of {@link DomibusConnectorDetachedSignatureType}. The
     * detachedSignature property of the detachedSignature object is set with a byte array
     * representing the detached signature. The detachedSignatureName property of the
     * detachedSignature object is set with a string representing the name of the detached
     * signature. The mimeType property of the detachedSignature object is set with
     * {@link DomibusConnectorDetachedSignatureMimeType#PKCS_7}.
     *
     * @return the created DomibusConnectorMessageContentType object
     */
    public static DomibusConnectorMessageContentType createMessageContent() {
        var messageContent = new DomibusConnectorMessageContentType();
        messageContent.setXmlContent(
            new StreamSource(new ByteArrayInputStream("<xmlContent></xmlContent>".getBytes()))
        );

        var document = new DomibusConnectorMessageDocumentType();

        var dataHandler = new DataHandler(new MyDataSource("document"));
        document.setDocument(dataHandler);
        document.setDocumentName("documentName");

        messageContent.setDocument(document);

        var detachedSignatureType = new DomibusConnectorDetachedSignatureType();
        detachedSignatureType.setDetachedSignature("detachedSignature".getBytes());
        detachedSignatureType.setDetachedSignatureName("detachedSignatureName");
        detachedSignatureType.setMimeType(DomibusConnectorDetachedSignatureMimeType.PKCS_7);

        document.setDetachedSignature(detachedSignatureType);

        return messageContent;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageConfirmationType with the confirmation set
     * to "DELIVERY" and the confirmation type set to
     * DomibusConnectorConfirmationType.DELIVERY.
     *
     * @return the created DomibusConnectorMessageConfirmationType object
     */
    public static DomibusConnectorMessageConfirmationType createMessageConfirmationType_DELIVERY() {
        var confirmation = new DomibusConnectorMessageConfirmationType();
        confirmation.setConfirmation(
            new StreamSource(new ByteArrayInputStream("<DELIVERY></DELIVERY>".getBytes()))
        );
        confirmation.setConfirmationType(DomibusConnectorConfirmationType.DELIVERY);
        return confirmation;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageConfirmationType with the confirmation set
     * to "NON_DELIVERY>" and the confirmation type set to
     * DomibusConnectorConfirmationType.NON_DELIVERY.
     *
     * @return the created DomibusConnectorMessageConfirmationType object
     */
    public static DomibusConnectorMessageConfirmationType
    createMessageConfirmationType_NON_DELIVERY() {
        var confirmation = new DomibusConnectorMessageConfirmationType();
        confirmation.setConfirmation(
            new StreamSource(new ByteArrayInputStream("<NON_DELIVERY></NON_DELIVERY>".getBytes()))
        );
        confirmation.setConfirmationType(DomibusConnectorConfirmationType.NON_DELIVERY);
        return confirmation;
    }

    /**
     * Creates a new instance of {@link DomibusConnectorMessageErrorType} and sets the error
     * details, error message, and error source properties.
     *
     * @return the created DomibusConnectorMessageErrorType object
     */
    public static DomibusConnectorMessageErrorType createMessageError() {
        var error = new DomibusConnectorMessageErrorType();
        error.setErrorDetails("error details");
        error.setErrorMessage("error message");
        error.setErrorSource("error source");
        return error;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageAttachmentType and initializes its
     * properties. The identifier, attachment, name, mimeType, and description properties are set
     * with default values.
     *
     * @return the created DomibusConnectorMessageAttachmentType object
     */
    public static DomibusConnectorMessageAttachmentType createMessageAttachment() {
        var attachment = new DomibusConnectorMessageAttachmentType();

        var dataHandler = new DataHandler(new ByteDataSource(
            "attachment".getBytes(), APPLICATION_OCTET_STREAM_MIME_TYPE)
        );

        attachment.setAttachment(dataHandler);
        attachment.setDescription("description");
        attachment.setIdentifier("identifier");
        attachment.setMimeType(APPLICATION_OCTET_STREAM_MIME_TYPE);
        attachment.setName("name");
        return attachment;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageDetailsType and initializes its properties.
     *
     * @return the created DomibusConnectorMessageDetailsType object with the initialized properties
     */
    public static DomibusConnectorMessageDetailsType createMessageDetails() {
        var messageDetails = new DomibusConnectorMessageDetailsType();

        messageDetails.setBackendMessageId("backendMessageId");
        messageDetails.setConversationId("conversationId");
        messageDetails.setFinalRecipient("finalRecipient");
        messageDetails.setOriginalSender("originalSender");
        messageDetails.setRefToMessageId("refToMessageId");

        messageDetails.setAction(createAction());
        messageDetails.setService(createService());
        messageDetails.setFromParty(createPartyAT());
        messageDetails.setToParty(createPartyDE());

        return messageDetails;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageDetailsType and initializes its properties.
     *
     * @return the created DomibusConnectorMessageDetailsType object with the initialized properties
     */
    public static DomibusConnectorMessageDetailsType createEpoMessageDetails() {
        var messageDetails = new DomibusConnectorMessageDetailsType();
        messageDetails.setConversationId("conversation21");
        messageDetails.setFinalRecipient("finalRecipient");
        messageDetails.setOriginalSender("originalSender");
        messageDetails.setRefToMessageId("refToMessageId");

        messageDetails.setAction(createActionFormA());
        messageDetails.setService(createServiceEpo());
        messageDetails.setFromParty(createPartyDE());
        messageDetails.setToParty(createPartyAT());

        return messageDetails;
    }

    /**
     * Creates a new instance of DomibusConnectorActionType with the action set to "Form_A".
     *
     * @return the created DomibusConnectorActionType object
     */
    public static DomibusConnectorActionType createActionFormA() {
        var action = new DomibusConnectorActionType();
        action.setAction("Form_A");
        return action;
    }

    /**
     * Creates a new instance of DomibusConnectorActionType with the action set to "action".
     *
     * @return the created DomibusConnectorActionType object
     */
    public static DomibusConnectorActionType createAction() {
        var action = new DomibusConnectorActionType();
        action.setAction("action");
        return action;
    }

    /**
     * Creates a new instance of DomibusConnectorServiceType and initializes its properties.
     *
     * @return the created DomibusConnectorServiceType object with the initialized properties
     */
    public static DomibusConnectorServiceType createService() {
        var service = new DomibusConnectorServiceType();
        service.setService("service");
        service.setServiceType("serviceType");
        return service;
    }

    /**
     * Creates a new instance of DomibusConnectorServiceType and initializes its properties. The
     * service property is set to "EPO" and the serviceType property is set to
     * "urn:e-codex:services:".
     *
     * @return the created DomibusConnectorServiceType object with the initialized properties
     */
    public static DomibusConnectorServiceType createServiceEpo() {
        var service = new DomibusConnectorServiceType();
        service.setService("EPO");
        service.setServiceType("urn:e-codex:services:");
        return service;
    }

    /**
     * Creates a new instance of {@link DomibusConnectorPartyType} with the following properties.
     *
     * <p>- partyId: "AT"
     * - partyIdType: "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1"
     * - role: "GW"
     *
     * @return the created DomibusConnectorPartyType object
     */
    public static DomibusConnectorPartyType createPartyAT() {
        var party = new DomibusConnectorPartyType();
        party.setPartyId("AT");
        party.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        party.setRole("GW");
        return party;
    }

    /**
     * Creates a new instance of {@link DomibusConnectorPartyType} with the following properties.
     *
     * <p>- partyId: "DE"
     * - partyIdType: "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1"
     * - role: "GW"
     *
     * @return the created DomibusConnectorPartyType object
     */
    public static DomibusConnectorPartyType createPartyDE() {
        var party = new DomibusConnectorPartyType();
        party.setPartyId("DE");
        party.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        party.setRole("GW");
        return party;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageResponseType. Sets the result to true and
     * assigns "EBMS1234" as the message ID.
     *
     * @return the created DomibusConnectorMessageResponseType object
     */
    public static DomibusConnectorMessageResponseType createResponse() {
        var responseType = new DomibusConnectorMessageResponseType();
        responseType.setResult(true);
        responseType.setAssignedMessageId("EBMS1234");
        return responseType;
    }

    private static class MyDataSource implements DataSource {
        private final String data;

        public MyDataSource(String string) {
            this.data = string;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException(
                "Not supported yet."
            ); // To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getContentType() {
            return APPLICATION_OCTET_STREAM_MIME_TYPE;
        }

        @Override
        public String getName() {
            return "aName";
        }
    }
}
