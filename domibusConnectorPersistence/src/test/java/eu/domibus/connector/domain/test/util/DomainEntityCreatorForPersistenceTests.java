
package eu.domibus.connector.domain.test.util;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
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
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import eu.domibus.connector.domain.transformer.util.LargeFileReferenceMemoryBacked;
import lombok.experimental.UtilityClass;

/**
 * A utility class for creating domain entities frequently used in persistence tests.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@UtilityClass
public class DomainEntityCreatorForPersistenceTests {
    /**
     * Creates a DomibusConnectorParty object representing a party in the Domibus connector.
     *
     * @return a DomibusConnectorParty object with the following properties:
     *      <ul>
     *          <li>partyId: "AT"
     *          <li>partyIdType: "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1"
     *          <li>role: "GW"
     *      </ul>
     */
    public static DomibusConnectorParty createPartyAT() {
        return new DomibusConnectorParty("AT", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1",
                                         "GW"
        );
    }

    /**
     * Creates a DomibusConnectorParty object representing a party in the Domibus connector.
     *
     * @return a DomibusConnectorParty object with the following properties:
     *      <ul>
     *          <li>partyId: "DE"
     *          <li>partyIdType: "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1"
     *          <li>role: "GW"
     *      </ul>
     */
    public static DomibusConnectorParty createPartyDE() {
        return new DomibusConnectorParty("DE", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1",
                                         "GW"
        );
    }

    public static DomibusConnectorAction createActionForm_A() {
        return new DomibusConnectorAction("Form_A");
    }

    public static DomibusConnectorAction createActionRelayREMMDAcceptanceRejection() {
        return new DomibusConnectorAction("RelayREMMDAcceptanceRejection");
    }

    public static DomibusConnectorService createServiceEPO() {
        return new DomibusConnectorService("EPO", "urn:e-codex:services:");
    }

    public static DomibusConnectorService createServiceUnknown() {
        return new DomibusConnectorService("UNKNOWN", "UNKNOWN!!!urn:e-codex:services:");
    }

    /**
     * Creates a {@link DomibusConnectorMessageConfirmation} object representing a message delivery
     * confirmation in the Domibus Connector.
     * The confirmation contains the evidence as a byte array and the evidence type.
     *
     * @return A {@link DomibusConnectorMessageConfirmation} object with the following properties:
     *         - evidenceType: The type of evidence for the message confirmation
     *         - evidence: The evidence for the message confirmation as a byte array
     */
    public static DomibusConnectorMessageConfirmation createMessageDeliveryConfirmation() {
        DomibusConnectorMessageConfirmation confirmation =
            new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("EVIDENCE1_DELIVERY".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.DELIVERY);
        return confirmation;
    }

    /**
     * Creates a non-delivery message confirmation in the Domibus Connector.
     *
     * @return A {@link DomibusConnectorMessageConfirmation} object representing the non-delivery
     *      message confirmation.
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
     * @return A DomibusConnectorMessageAttachment object with the following properties:
     *         - identifier: The attachment identifier
     *         - attachment: The attachment data
     *         - name: The name of the attachment
     *         - mimeType: The MIME type of the attachment
     *         - description: Optional description of the attachment
     */
    public static DomibusConnectorMessageAttachment createSimpleMessageAttachment() {
        LargeFileReferenceMemoryBacked domibusConnectorBigDataReferenceMemoryBacked =
            new LargeFileReferenceMemoryBacked("attachment".getBytes());
        DomibusConnectorMessageAttachment attachment =
            new DomibusConnectorMessageAttachment(
                domibusConnectorBigDataReferenceMemoryBacked, "identifier");
        attachment.setName("name");
        attachment.setMimeType("application/garbage");
        return attachment;
    }

    /**
     * Creates a simple test confirmation message in the Domibus Connector.
     *
     * @return A DomibusConnectorMessage object representing the test confirmation message.
     */
    public static DomibusConnectorMessage createSimpleTestConfirmationMessage() {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId("conversation1");
        messageDetails.setEbmsMessageId("ebms1_123412");
        messageDetails.setRefToMessageId("ebms1");

        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        return new DomibusConnectorMessage(
            "msgid",
            messageDetails,
            createMessageDeliveryConfirmation()
        );
    }

    /**
     * Creates a simple test message in the Domibus Connector.
     *
     * @return A DomibusConnectorMessage object representing the simple test message.
     */
    public static DomibusConnectorMessage createSimpleTestMessage() {

        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId("conversation1");
        messageDetails.setEbmsMessageId("ebms1");

        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        return new DomibusConnectorMessage("msgid", messageDetails, messageContent);
    }

    /**
     * Creates a DomibusConnectorMessage object with a default ID.
     *
     * @return A DomibusConnectorMessage object.
     */
    public static DomibusConnectorMessage createMessage() {
        return createMessage("defaultid");
    }

    /**
     * Creates a DomibusConnectorMessage object with the given message ID.
     *
     * @param msgId The ID of the message.
     * @return A DomibusConnectorMessage object.
     */
    public static DomibusConnectorMessage createMessage(String msgId) {
        DomibusConnectorMessageDetails messageDetails = createDomibusConnectorMessageDetails();

        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent("xmlContent".getBytes());

        DetachedSignature detachedSignature =
            new DetachedSignature(
                "detachedSignature".getBytes(), "signaturename", DetachedSignatureMimeType.BINARY);

        LargeFileReferenceMemoryBacked docRef =
            new LargeFileReferenceMemoryBacked("documentbytes".getBytes());

        DomibusConnectorMessageDocument messageDocument =
            new DomibusConnectorMessageDocument(docRef, "Document1.pdf", detachedSignature);

        messageContent.setDocument(messageDocument);

        DomibusConnectorMessage msg =
            new DomibusConnectorMessage(msgId, messageDetails, messageContent);
        msg.addTransportedMessageConfirmation(createMessageDeliveryConfirmation());
        msg.addAttachment(createSimpleMessageAttachment());
        msg.addError(createMessageError());
        return msg;
    }

    /**
     * Creates a {@link DomibusConnectorMessageDetails} object with pre-defined values.
     *
     * @return The created {@link DomibusConnectorMessageDetails} object.
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
        messageDetails.getToParty().setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        messageDetails.setFromParty(createPartyDE());
        messageDetails.getFromParty().setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);

        return messageDetails;
    }

    /**
     * Creates an error message with #createSimpleDomibusConnectorMessage as message "error detail
     * message" as details "error message" as text "error source" as error source.
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
