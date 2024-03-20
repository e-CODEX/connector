package eu.domibus.connector.testdata;

import eu.domibus.connector.domain.transition.*;
import org.apache.cxf.attachment.ByteDataSource;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helper class to create TransitionModel Objects for testing 
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class TransitionCreator {

    public static String APPLICATION_OCTET_STREAM_MIME_TYPE = "application/octet-stream";
    
    public static DomibusConnectorMessagesType createMessages() {
        DomibusConnectorMessagesType messages = new DomibusConnectorMessagesType();
        messages.getMessages().add(createMessage());
        return messages;
    }
    
    public static DomibusConnectorMessageType createEpoMessage() {
        DomibusConnectorMessageType message = new DomibusConnectorMessageType();
        message.setMessageDetails(createEpoMessageDetails());
        message.setMessageContent(createMessageContent()); 
        message.getMessageAttachments().add(createMessageAttachment());
                
        return message;
    }
            
    public static DomibusConnectorMessageType createMessage() {
        DomibusConnectorMessageType message = new DomibusConnectorMessageType();        
        message.setMessageDetails(createMessageDetails());
        message.setMessageContent(createMessageContent());        
        message.getMessageConfirmations().add(createMessageConfirmationType_DELIVERY());
        message.getMessageErrors().add(createMessageError());
        message.getMessageAttachments().add(createMessageAttachment());
                
        return message;
    }
    
    public static DomibusConnectorMessageType createEvidenceNonDeliveryMessage() {
        DomibusConnectorMessageType message = new DomibusConnectorMessageType();        
        message.setMessageDetails(createMessageDetails());
        message.getMessageConfirmations().add(createMessageConfirmationType_NON_DELIVERY());
        
        return message;
    }
    

    public static DomibusConnectorMessageContentType createMessageContent() {
        DomibusConnectorMessageContentType messageContent = new DomibusConnectorMessageContentType();
        //messageContent.setXmlContent("xmlContent".getBytes());
        messageContent.setXmlContent(new StreamSource(new ByteArrayInputStream("<xmlContent></xmlContent>".getBytes())));
        
        DomibusConnectorMessageDocumentType document = new DomibusConnectorMessageDocumentType();
        
        //DataHandler dataHandler = new DataHandler(new ByteDataSource("document".getBytes(), "application/octet-stream"));        
        DataHandler dataHandler = new DataHandler(new MyDataSource("document"));
        document.setDocument(dataHandler);
        //document.setDocument(value);
        document.setDocumentName("documentName");
        
        messageContent.setDocument(document);        
        
        DomibusConnectorDetachedSignatureType sig = new DomibusConnectorDetachedSignatureType();
        sig.setDetachedSignature("detachedSignature".getBytes());
        sig.setDetachedSignatureName("detachedSignatureName");
        sig.setMimeType(DomibusConnectorDetachedSignatureMimeType.PKCS_7);
        
        document.setDetachedSignature(sig);
                                        
        return messageContent;
    }
    
    public static DomibusConnectorMessageConfirmationType createMessageConfirmationType_DELIVERY() {
        DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
        confirmation.setConfirmation(new StreamSource(new ByteArrayInputStream("<DELIVERY></DELIVERY>".getBytes())));
        confirmation.setConfirmationType(DomibusConnectorConfirmationType.DELIVERY);        
        return confirmation;
    }
    
    public static DomibusConnectorMessageConfirmationType createMessageConfirmationType_NON_DELIVERY() {
        DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
        confirmation.setConfirmation(new StreamSource(new ByteArrayInputStream("<NON_DELIVERY></NON_DELIVERY>".getBytes())));
        confirmation.setConfirmationType(DomibusConnectorConfirmationType.NON_DELIVERY);
        return confirmation;
    }
    
    public static DomibusConnectorMessageErrorType createMessageError() {
        DomibusConnectorMessageErrorType error = new DomibusConnectorMessageErrorType();
        error.setErrorDetails("error details");
        error.setErrorMessage("error message");
        error.setErrorSource("error source");
        return error;
    }
    
    public static DomibusConnectorMessageAttachmentType createMessageAttachment() {
        DomibusConnectorMessageAttachmentType attachment = new DomibusConnectorMessageAttachmentType();
             
        DataHandler dataHandler = new DataHandler(new ByteDataSource("attachment".getBytes(), "application/octet-stream"));   
        
        attachment.setAttachment(dataHandler);
        attachment.setDescription("description");
        attachment.setIdentifier("identifier");
        attachment.setMimeType(APPLICATION_OCTET_STREAM_MIME_TYPE);
        attachment.setName("name");
        return attachment;
    }
    
    public static DomibusConnectorMessageDetailsType createMessageDetails() {
        DomibusConnectorMessageDetailsType messageDetails = new DomibusConnectorMessageDetailsType();
        
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
    
    public static DomibusConnectorMessageDetailsType createEpoMessageDetails() {
        DomibusConnectorMessageDetailsType messageDetails = new DomibusConnectorMessageDetailsType();
        
//        messageDetails.setBackendMessageId("backendMessageId");
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
    
    public static DomibusConnectorActionType createActionFormA() {
        DomibusConnectorActionType action = new DomibusConnectorActionType();
        action.setAction("Form_A");        
        return action;
    }
    
    public static DomibusConnectorActionType createAction() {
        DomibusConnectorActionType action = new DomibusConnectorActionType();
        action.setAction("action");        
        return action;
    }
    
    public static DomibusConnectorServiceType createService() {
        DomibusConnectorServiceType service = new DomibusConnectorServiceType();
        service.setService("service");
        service.setServiceType("serviceType");
        return service;        
    }
    
    public static DomibusConnectorServiceType createServiceEpo() {
        DomibusConnectorServiceType service = new DomibusConnectorServiceType();
        service.setService("EPO");
        service.setServiceType("urn:e-codex:services:");
        return service;        
    }
    
    public static DomibusConnectorPartyType createPartyAT() {
        DomibusConnectorPartyType party = new DomibusConnectorPartyType();
        party.setPartyId("AT");
        party.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        party.setRole("GW");
        return party;
    }
    
    public static DomibusConnectorPartyType createPartyDE() {
        DomibusConnectorPartyType party = new DomibusConnectorPartyType();
        party.setPartyId("DE");
        party.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        party.setRole("GW");
        return party;
    }

    public static DomibusConnectorMessageResponseType createResponse() {
        DomibusConnectorMessageResponseType responseType = new DomibusConnectorMessageResponseType();
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
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data.getBytes("UTF-8"));
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public String getName() {
            return "aName";
        }
        
    }
}