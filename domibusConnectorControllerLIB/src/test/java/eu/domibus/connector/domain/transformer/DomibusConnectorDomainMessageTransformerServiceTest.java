package eu.domibus.connector.domain.transformer;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService.CannotBeMappedToTransitionException;
import eu.domibus.connector.domain.transition.*;
import eu.domibus.connector.persistence.service.testutil.LargeFilePersistenceServicePassthroughImpl;
import eu.domibus.connector.testdata.TransitionCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 *
 *
 */
public class DomibusConnectorDomainMessageTransformerServiceTest {

    public DomibusConnectorDomainMessageTransformerServiceTest() {
    }


    DomibusConnectorDomainMessageTransformerService transformerService;
    LargeFilePersistenceServicePassthroughImpl mockedLargeFilePersistenceService;

    @BeforeEach
    public void init() {
        mockedLargeFilePersistenceService = new LargeFilePersistenceServicePassthroughImpl();

        transformerService = new DomibusConnectorDomainMessageTransformerService(mockedLargeFilePersistenceService);
        transformerService.messageIdThreadLocal.set(new DomibusConnectorMessageId("id1"));
    }

    @AfterEach
    public void afterEach() {
        transformerService.messageIdThreadLocal.remove();
    }


    @Test
    public void testTransformDomainToTransition() {
        DomibusConnectorMessage domainMessage = DomainEntityCreator.createMessage();

        DomibusConnectorMessageType messageType = transformerService.transformDomainToTransition(domainMessage);

        assertThat(messageType).as("transformed object is not allowed to be null").isNotNull();

        assertThat(messageType.getMessageDetails()).as("message details are not allowed to be null!").isNotNull();
        assertThat(messageType.getMessageContent()).as("message content is set in test entity!").isNotNull();
        assertThat(messageType.getMessageConfirmations()).as("must have 1 confirmation").hasSize(1);
        assertThat(messageType.getMessageAttachments()).as("must have 1 message attachment").hasSize(1);
        assertThat(messageType.getMessageErrors()).as("must have 1 message error!").hasSize(1);
    }


    @Test
    public void testTransformDomainToTransition_noBusinessDoc() {
        DomibusConnectorMessage domainMessage = DomainEntityCreator.createMessage();
        domainMessage.getMessageContent().setDocument(null);

        DomibusConnectorMessageType messageType = transformerService.transformDomainToTransition(domainMessage);

        assertThat(messageType).as("transformed object is not allowed to be null").isNotNull();

        assertThat(messageType.getMessageDetails()).as("message details are not allowed to be null!").isNotNull();
        assertThat(messageType.getMessageContent()).as("message content is set in test entity!").isNotNull();
        assertThat(messageType.getMessageConfirmations()).as("must have 1 confirmation").hasSize(1);
        assertThat(messageType.getMessageAttachments()).as("must have 1 message attachment").hasSize(1);
        assertThat(messageType.getMessageErrors()).as("must have 1 message error!").hasSize(1);
    }


    @Test
    public void testTransformDomainToTransition_finalRecipientIsNull_shouldThrowException() {
        Assertions.assertThrows(DomibusConnectorDomainMessageTransformerService.CannotBeMappedToTransitionException.class, () -> {
            DomibusConnectorMessage domainMessage = DomainEntityCreator.createMessage();
            domainMessage.getMessageDetails().setFinalRecipient(null);
            DomibusConnectorMessageType messageType = transformerService.transformDomainToTransition(domainMessage);
        });
    }

    @Test
    public void testTransformDomainToTransition_originalSenderIsNull_shouldThrowException() {
        Assertions.assertThrows(DomibusConnectorDomainMessageTransformerService.CannotBeMappedToTransitionException.class, () -> {
            DomibusConnectorMessage domainMessage = DomainEntityCreator.createMessage();
            domainMessage.getMessageDetails().setOriginalSender(null);
            DomibusConnectorMessageType messageType = transformerService.transformDomainToTransition(domainMessage);
        });
    }



    @Test
    public void testTransformDomainToTransition_messageContentIsNull_shouldThrowException() {
            Assertions.assertThrows(DomibusConnectorDomainMessageTransformerService.CannotBeMappedToTransitionException.class, () -> {

                DomibusConnectorMessageConfirmation createMessageDeliveryConfirmation = DomainEntityCreator.createMessageDeliveryConfirmation();
                DomibusConnectorMessage domainMessage = new DomibusConnectorMessage(null, createMessageDeliveryConfirmation);
                transformerService.transformDomainToTransition(domainMessage);
            });
    }

    @Test
    public void testTransformMessageConfirmationDomainToTransition() {
        DomibusConnectorMessageConfirmation messageDeliveryConfirmation = DomainEntityCreator.createMessageDeliveryConfirmation();
        DomibusConnectorMessageConfirmationType messageConfirmationTO =
                transformerService.transformMessageConfirmationDomainToTransition(messageDeliveryConfirmation);

        //assertThat(messageConfirmationTO.getConfirmation()).isEqualTo("EVIDENCE1_DELIVERY".getBytes());
        assertThat(messageConfirmationTO.getConfirmation()).isNotNull(); //TODO: better check!
        assertThat(messageConfirmationTO.getConfirmationType().name()).isEqualTo(DomibusConnectorEvidenceType.DELIVERY.name());

    }

    @Test
    public void testTransformMessageConfirmationDomainToTransition_getEvidenceIsNull_shouldThrowIllegalArgumentException() {
        Assertions.assertThrows(DomibusConnectorDomainMessageTransformerService.CannotBeMappedToTransitionException.class, () -> {
            DomibusConnectorMessageConfirmation messageDeliveryConfirmation = DomainEntityCreator.createMessageDeliveryConfirmation();
            messageDeliveryConfirmation.setEvidence(null); //set evidence to null to provoke exception


            DomibusConnectorMessageConfirmationType messageConfirmationTO =
                    transformerService.transformMessageConfirmationDomainToTransition(messageDeliveryConfirmation);
        });
    }

    @Test
    public void testTransformMessageAttachmentDomainToTransition() throws IOException {
        DomibusConnectorMessageAttachment messageAttachment = DomainEntityCreator.createSimpleMessageAttachment();

        DomibusConnectorMessageAttachmentType attachmentTO =
                transformerService.transformMessageAttachmentDomainToTransition(messageAttachment);

        assertThat(attachmentTO.getAttachment()).isNotNull(); //TODO: better check!
        assertThat(attachmentTO.getIdentifier()).isEqualTo("identifier");
        compareDataHandlerContent(attachmentTO.getAttachment(), "attachment");

        assertThat(attachmentTO.getMimeType()).isEqualTo("application/garbage");
        assertThat(attachmentTO.getName()).isEqualTo("name");
        assertThat(attachmentTO.getDescription()).isEqualTo("a description");

    }

    private void compareDataHandlerContent(DataHandler dh, String content) {
        try {
            InputStream is = dh.getInputStream();
            byte[] attachmentBytes = StreamUtils.copyToByteArray(is);
            assertThat(new String(attachmentBytes, "UTF-8")).isEqualTo(content);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    @Test
    public void testTransformMessageConfirmationDomainToTransition_getEvidenceTypeIsNull_shouldThrowIllegalArgumentException() {
        Assertions.assertThrows(CannotBeMappedToTransitionException.class, () -> {
         DomibusConnectorMessageConfirmation messageDeliveryConfirmation = DomainEntityCreator.createMessageDeliveryConfirmation();
         messageDeliveryConfirmation.setEvidenceType(null); //set evidence to null to provoke exception

         DomibusConnectorMessageConfirmationType messageConfirmationTO =
                 transformerService.transformMessageConfirmationDomainToTransition(messageDeliveryConfirmation);
        });
    }

    @Test
    public void testTransformMessageContentDomainToTransition() {
        DomibusConnectorMessage domainMessage = DomainEntityCreator.createMessage();
        DomibusConnectorMessageContent messageContent = domainMessage.getMessageContent();

        DomibusConnectorMessageContentType messageContentTO = transformerService.transformMessageContentDomainToTransition(messageContent);

        assertThat(messageContentTO).as("message content is set in test entity!").isNotNull();
        //assertThat(messageContentTO.getXmlContent()).isEqualTo(domainMessage.getMessageContent().getXmlContent());
        assertThat(messageContentTO.getXmlContent()).isNotNull(); //TODO better check?

        assertThat(messageContentTO.getDocument()).as("document of messageContent must be mapped!").isNotNull();
    }

    @Test
    public void testTransformMessageContentDomainToTransition_testMapDocument() {
        DomibusConnectorMessage domainMessage = DomainEntityCreator.createMessage();


        DomibusConnectorMessageContentType messageContentTO =
                transformerService.transformMessageContentDomainToTransition(domainMessage.getMessageContent());
        DomibusConnectorMessageDocumentType document = messageContentTO.getDocument();

        assertThat(document.getDocument()).isNotNull();
        compareDataHandlerContent(document.getDocument(), "documentbytes");
        //assertThat(document.getDocument()).isEqualTo("documentbytes".getBytes());
        assertThat(document.getDocumentName()).isEqualTo("Document1.pdf");
        assertThat(document.getDetachedSignature()).as("detached signature must not be null!").isNotNull();

        DomibusConnectorDetachedSignatureType detachedSignature = document.getDetachedSignature();
        //DetachedSignature detachedSignature = new DetachedSignature("detachedSignature".getBytes(), "signaturename", DetachedSignatureMimeType.BINARY);
        assertThat(detachedSignature.getDetachedSignature()).isEqualTo("detachedSignature".getBytes());
        assertThat(detachedSignature.getDetachedSignatureName()).isEqualTo("signaturename");
        assertThat(detachedSignature.getMimeType().name()).isEqualTo(DetachedSignatureMimeType.BINARY.name());
    }

    @Test
    public void testTransformMessageContentDomainToTransition_noDetachedSignature() {
        DomibusConnectorMessageContent messageContent = DomainEntityCreator.createMessageContentWithDocumentWithNoSignature();

        DomibusConnectorMessageContentType messageContentTO = transformerService.transformMessageContentDomainToTransition(messageContent);

        assertThat(messageContentTO).as("message content is set in test entity!").isNotNull();
        //assertThat(messageContentTO.getXmlContent()).isEqualTo(domainMessage.getMessageContent().getXmlContent());
        assertThat(messageContentTO.getXmlContent()).isNotNull(); //TODO better check?

        assertThat(messageContentTO.getDocument()).as("document of messageContent must be mapped!").isNotNull();
    }

    @Test
    public void testTransformMessageContentDomainToTransition_noPdfDocument() {
        DomibusConnectorMessageContent messageContent = DomainEntityCreator.createMessageContentWithDocumentWithNoPdfDocument();

        DomibusConnectorMessageContentType messageContentTO = transformerService.transformMessageContentDomainToTransition(messageContent);

        assertThat(messageContentTO).as("message content is set in test entity!").isNotNull();
        //assertThat(messageContentTO.getXmlContent()).isEqualTo(domainMessage.getMessageContent().getXmlContent());
        assertThat(messageContentTO.getXmlContent()).isNotNull(); //TODO better check?

        assertThat(messageContentTO.getDocument()).as("document of messageContent must be null!").isNull();
    }


    @Test
    public void testTransformMessageDetailsDomainToTransition() {
        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        DomibusConnectorMessageDetails messageDetails = DomainEntityCreator.createDomibusConnectorMessageDetails();
        message.setMessageDetails(messageDetails);


        DomibusConnectorMessageDetailsType messageDetailsType = transformerService.transformMessageDetailsDomainToTransition(message);

        assertThat(messageDetailsType.getBackendMessageId()).as("backendMessageId must be mapped").isEqualTo("national1");
        assertThat(messageDetailsType.getEbmsMessageId()).as("ebmsMessageId must be mapped").isEqualTo("ebms1");
        assertThat(messageDetailsType.getConversationId()).as("conversationId must be mapped!").isEqualTo("conversation1");
        assertThat(messageDetailsType.getFinalRecipient()).as("finalRecipient must be mapped!").isEqualTo("finalRecipient");
        assertThat(messageDetailsType.getOriginalSender()).as("originalSender must be mapped!").isEqualTo("originalSender");
        assertThat(messageDetailsType.getRefToMessageId()).as("RefToMessageId must be mapped!").isEqualTo("refToMessageId");
        assertThat(messageDetailsType.getAction()).as("Action must be mapped!").isNotNull();
        assertThat(messageDetailsType.getFromParty()).as("FromParty must be mapped!").isNotNull();
        assertThat(messageDetailsType.getToParty()).as("toParty must be mapped!").isNotNull();
        assertThat(messageDetailsType.getService()).as("service must be mappted!").isNotNull();
    }

    @Test
    public void testTransformMessageDetailsDomainToTransition_asConfirmationMessageToBackend() {
        DomibusConnectorMessage confirmationMessage = DomainEntityCreator.createEvidenceNonDeliveryMessage();
        DomibusConnectorMessageDetails messageDetails = DomainEntityCreator.createDomibusConnectorMessageDetails();
        messageDetails.setRefToBackendMessageId("refToBackendId");
        confirmationMessage.setMessageDetails(messageDetails);
        messageDetails.setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);

        DomibusConnectorMessageDetailsType messageDetailsType = transformerService.transformMessageDetailsDomainToTransition(confirmationMessage);

        assertThat(messageDetailsType.getBackendMessageId())
                .as("backendMessageId must be mapped from refToBackendMessageId of messageDetails")
                .isEqualTo("refToBackendId");

        assertThat(messageDetailsType.getRefToMessageId())
                .as("refToMessageId must be mapped from refToBackendMessageId of messageDetails if transport is to backend and it is not null")
                .isEqualTo("refToBackendId");

    }

    @Test
    public void testTransformMessageDetailsDomainToTransition_asConfirmationMessageToBackend_backendIdNull() {
        DomibusConnectorMessage confirmationMessage = DomainEntityCreator.createEvidenceNonDeliveryMessage();
        DomibusConnectorMessageDetails messageDetails = DomainEntityCreator.createDomibusConnectorMessageDetails();
        messageDetails.setRefToBackendMessageId(null);
        messageDetails.setRefToMessageId("refToId");
        messageDetails.setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
        confirmationMessage.setMessageDetails(messageDetails);

        DomibusConnectorMessageDetailsType messageDetailsType = transformerService.transformMessageDetailsDomainToTransition(confirmationMessage);

        assertThat(messageDetailsType.getRefToMessageId())
                .as("if backendMessageId of original message is null, then the ebms id must be used for refToMessageId")
                .isEqualTo("refToId");

    }

    @Test
    public void testTransformMessageDetailsDomainToTransition_asConfirmationToGw() {
        DomibusConnectorMessage confirmationMessage = DomainEntityCreator.createEvidenceNonDeliveryMessage();
        DomibusConnectorMessageDetails messageDetails = DomainEntityCreator.createDomibusConnectorMessageDetails();
        messageDetails.setRefToBackendMessageId("refToBackendId");
        messageDetails.setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
        confirmationMessage.setMessageDetails(messageDetails);


        DomibusConnectorMessageDetailsType messageDetailsType = transformerService.transformMessageDetailsDomainToTransition(confirmationMessage);

        assertThat(messageDetailsType.getBackendMessageId())
                .as("backendMessageId must be mapped from refToBackendMessageId of messageDetails")
                .isEqualTo("refToBackendId");
        assertThat(messageDetailsType.getRefToMessageId())
                .as("refToMessageId must be mapped from refToBackendMessageId of messageDetails")
                .isEqualTo("refToBackendId");

    }



    @Test
    public void testTransformTransitionToDomain() {
        DomibusConnectorMessage domainMessage = transformerService.transformTransitionToDomain(TransitionCreator.createMessage(), new DomibusConnectorMessageId("id1"));

        assertThat(domainMessage).as("converted domainMessage must not be null!").isNotNull();
        assertThat(domainMessage.getMessageDetails()).as("message details must not be null!").isNotNull();
        assertThat(domainMessage.getMessageContent()).as("message content must not be null!").isNotNull();
        assertThat(domainMessage.getTransportedMessageConfirmations()).as("message confirmations contains 1!").hasSize(1);
        assertThat(domainMessage.getMessageProcessErrors()).as("message errors contains 1!").hasSize(1);
        assertThat(domainMessage.getMessageAttachments()).as("message attachments contains 1!").hasSize(1);
    }

    @Test
    public void testTransformTransitionToDomain_NoBusinessDoc() {
        DomibusConnectorMessageType message = TransitionCreator.createMessage();
        message.getMessageContent().setDocument(null);
        DomibusConnectorMessage domainMessage = transformerService.transformTransitionToDomain(message, new DomibusConnectorMessageId("abc"));

        assertThat(domainMessage).as("converted domainMessage must not be null!").isNotNull();
        assertThat(domainMessage.getMessageDetails()).as("message details must not be null!").isNotNull();
        assertThat(domainMessage.getMessageContent()).as("message content must not be null!").isNotNull();
        assertThat(domainMessage.getTransportedMessageConfirmations()).as("message confirmations contains 1!").hasSize(1);
        assertThat(domainMessage.getMessageProcessErrors()).as("message errors contains 1!").hasSize(1);
        assertThat(domainMessage.getMessageAttachments()).as("message attachments contains 1!").hasSize(1);
    }



    @Test
    public void testTransformTransitionToDomain_evidenceTriggerMessage() {
        DomibusConnectorMessageType msg = new DomibusConnectorMessageType();
        DomibusConnectorMessageDetailsType details = new DomibusConnectorMessageDetailsType();
        details.setRefToMessageId("refmsg1");

        //TODO: make it possible that the following empty action, service, parties are not required for evidence
        //trigger message! if refToMessageId is set!
        details.setAction(new DomibusConnectorActionType());
        details.setService(new DomibusConnectorServiceType());
        details.setFromParty(new DomibusConnectorPartyType());
        details.setToParty(new DomibusConnectorPartyType());

        msg.setMessageDetails(details);

        DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
        confirmation.setConfirmationType(DomibusConnectorConfirmationType.RELAY_REMMD_FAILURE);

        msg.getMessageConfirmations().add(confirmation);

        transformerService.transformTransitionToDomain(msg, new DomibusConnectorMessageId("id2"));
    }

    @Test
    public void testTransformTransitionToDomain_withMessageContentNull() {
        DomibusConnectorMessageType transitionMessage = TransitionCreator.createMessage();
        transitionMessage.setMessageContent(null);

        DomibusConnectorMessage domainMessage = transformerService.transformTransitionToDomain(transitionMessage, new DomibusConnectorMessageId("id3"));

        assertThat(domainMessage).as("converted domainMessage must not be null!").isNotNull();
        assertThat(domainMessage.getMessageDetails()).as("message details must not be null!").isNotNull();
        assertThat(domainMessage.getMessageContent()).as("message content must be null!").isNull();
        assertThat(domainMessage.getTransportedMessageConfirmations()).as("message confirmations contains 1!").hasSize(1);
        assertThat(domainMessage.getMessageProcessErrors()).as("message errors contains 1!").hasSize(1);
        assertThat(domainMessage.getMessageAttachments()).as("message attachments contains 1!").hasSize(1);
    }

    @Test
    public void testTransformMessageContentTransitionToDomain() {
        DomibusConnectorMessageContentType messageContentTO = TransitionCreator.createMessageContent();

        DomibusConnectorMessageContent messageContent = transformerService.transformMessageContentTransitionToDomain(messageContentTO);

        assertThat(messageContent).isNotNull();
        assertThat(messageContent.getXmlContent()).isNotNull(); //TODO compare byte[]
        assertThat(messageContent.getDocument()).isNotNull();

        DomibusConnectorMessageDocument document = messageContent.getDocument();
        assertThat(document.getDocument()).isNotNull();
        assertThat(document.getDetachedSignature()).isNotNull();
    }

    @Test
    public void testTransformMessageContentTransitionToDomain_withDocumentNull() {
        DomibusConnectorMessageContentType messageContentTO = TransitionCreator.createMessageContent();
        messageContentTO.setDocument(null);

        DomibusConnectorMessageContent messageContent = transformerService.transformMessageContentTransitionToDomain(messageContentTO);

        assertThat(messageContent).isNotNull();
        assertThat(messageContent.getXmlContent()).isNotNull(); //TODO compare byte[]
        assertThat(messageContent.getDocument()).isNull();
    }

    @Test
    public void testTransformMessageContentTransitionToDomain_withDocumentDetachedSignatureNull() {
        DomibusConnectorMessageContentType messageContentTO = TransitionCreator.createMessageContent();
        messageContentTO.getDocument().setDetachedSignature(null);

        DomibusConnectorMessageContent messageContent = transformerService.transformMessageContentTransitionToDomain(messageContentTO);

        assertThat(messageContent).isNotNull();
        assertThat(messageContent.getXmlContent()).isNotNull();
        assertThat(messageContent.getDocument().getDetachedSignature()).isNull();
    }


    @Test
    public void testTransformMessageDetailsTransitionToDomain() {
        DomibusConnectorMessageDetailsType messageDetailsTO = TransitionCreator.createMessageDetails();

        DomibusConnectorMessageDetails messageDetails = transformerService.transformMessageDetailsTransitionToDomain(messageDetailsTO);

        assertThat(messageDetails.getBackendMessageId()).as("backend message id must match").isEqualTo("backendMessageId");
        assertThat(messageDetails.getConversationId()).as("conversation id must match").isEqualTo("conversationId");
        assertThat(messageDetails.getFinalRecipient()).as("final recipient must match").isEqualTo("finalRecipient");
        assertThat(messageDetails.getOriginalSender()).as("original sender must match").isEqualTo("originalSender");
        assertThat(messageDetails.getRefToMessageId()).as("refToMessageid").isEqualTo("refToMessageId");

        assertThat(messageDetails.getAction().getAction()).isEqualTo("action");
//        assertThat(messageDetails.getAction().isDocumentRequired()).isTrue();

        assertThat(messageDetails.getService().getService()).isEqualTo("service");
        assertThat(messageDetails.getService().getServiceType()).isEqualTo("serviceType");

        assertThat(messageDetails.getFromParty().getPartyId()).isEqualTo("AT");
        assertThat(messageDetails.getFromParty().getPartyIdType()).isEqualTo("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        assertThat(messageDetails.getFromParty().getRole()).isEqualTo("GW");

        assertThat(messageDetails.getToParty().getPartyId()).isEqualTo("DE");
        assertThat(messageDetails.getToParty().getPartyIdType()).isEqualTo("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        assertThat(messageDetails.getToParty().getRole()).isEqualTo("GW");

    }

    @Test
    public void testTransformMessageAttachmentTransitionToDomain() throws IOException {
        DomibusConnectorMessageAttachmentType messageAttachmentTO = TransitionCreator.createMessageAttachment();
        DomibusConnectorMessageAttachment attachment =
                transformerService.transformMessageAttachmentTransitionToDomain(messageAttachmentTO);

        byte[] attachmentBytes = StreamUtils.copyToByteArray(attachment.getAttachment().getInputStream());

        //for this test assertion, the LargeFileProvider mock must be replaced by real impl
        //assertThat(attachmentBytes).isEqualTo("attachment".getBytes());
        assertThat(attachment.getDescription()).isEqualTo("description");
        assertThat(attachment.getIdentifier()).isEqualTo("identifier");
        assertThat(attachment.getMimeType()).isEqualTo("application/octet-stream");
        assertThat(attachment.getName()).isEqualTo("name");
    }

    @Test
    public void testTransformMessageErrorTransitionToDomain() {
        DomibusConnectorMessageErrorType messageErrorTO = TransitionCreator.createMessageError();
        DomibusConnectorMessageError error =
                transformerService.transformMessageErrorTransitionToDomain(messageErrorTO);

        assertThat(error.getDetails()).isEqualTo("error details");
        assertThat(error.getText()).isEqualTo("error message");
        assertThat(error.getSource()).isEqualTo("error source");
    }

    @Test
    public void testTransformMessageConfirmationTransitionToDomain() throws UnsupportedEncodingException {
        DomibusConnectorMessageConfirmationType messageConfirmationTO = TransitionCreator.createMessageConfirmationType_DELIVERY();
        DomibusConnectorMessageConfirmation confirmation =
                transformerService.transformMessageConfirmationTransitionToDomain(messageConfirmationTO);
        
        //TODO: repair check!
        //assertThat(new String(confirmation.getEvidence(), "UTF-8")).isEqualTo("<DELIVERY></DELIVERY>");
        assertThat(confirmation.getEvidence()).isNotEmpty();
        assertThat(confirmation.getEvidenceType()).isEqualTo(DomibusConnectorEvidenceType.DELIVERY);

    }
}
