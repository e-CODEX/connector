package eu.domibus.connector.controller.test.util;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.DetachedSignature;
import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import eu.domibus.connector.domain.testutil.LargeFileReferenceGetSetBased;

import java.io.ByteArrayInputStream;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class ConnectorControllerTestDomainCreator {

    public static DomibusConnectorParty createPartyAT() {
        DomibusConnectorParty p = new DomibusConnectorParty("AT", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", "GW");
        return p;
    }
    
    public static DomibusConnectorParty createPartyDE() {
        DomibusConnectorParty p = new DomibusConnectorParty("DE", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1", "GW");        
        return p;
    }
    
    public static DomibusConnectorAction createActionForm_A() {
        DomibusConnectorAction a = new DomibusConnectorAction("Form_A");
//        DomibusConnectorAction a = new DomibusConnectorAction("Form_A", true);
        return a;                
    }
    
    public static DomibusConnectorAction createActionRelayREMMDAcceptanceRejection() {
        DomibusConnectorAction a = new DomibusConnectorAction("RelayREMMDAcceptanceRejection");     
//        DomibusConnectorAction a = new DomibusConnectorAction("RelayREMMDAcceptanceRejection", true); 
        return a;
    }
    
    public static DomibusConnectorService createServiceEPO() {
        DomibusConnectorService s = new DomibusConnectorService("EPO", "urn:e-codex:services:");
        return s;
    }
    
    public static DomibusConnectorMessageConfirmation createMessageDeliveryConfirmation() {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("EVIDENCE1_DELIVERY".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.DELIVERY);
        return confirmation;
    }
    
    public static DomibusConnectorMessageConfirmation createMessageNonDeliveryConfirmation() {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence("EVIDENCE1_NON_DELIVERY".getBytes());
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.NON_DELIVERY);
        return confirmation;
    }
    
    public static DomibusConnectorMessageAttachment createSimpleMessageAttachment() {

        LargeFileReferenceGetSetBased inMemory = new LargeFileReferenceGetSetBased();
        inMemory.setBytes("attachment".getBytes());
        
        DomibusConnectorMessageAttachment attachment = new DomibusConnectorMessageAttachment(inMemory, "identifier");       
        attachment.setName("name");
        attachment.setMimeType("application/garbage");
        return attachment;
    }
    

    public static DomibusConnectorMessage createSimpleTestMessage() {
        
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        messageDetails.setConversationId("conversation1");
        messageDetails.setEbmsMessageId("ebms1");
        
        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        DomibusConnectorMessage msg = new DomibusConnectorMessage(messageDetails, messageContent);
        //msg.setDbMessageId(78L);
        //msg.getMessageDetails().
        return msg;
    }
    
    
    
    public static DomibusConnectorMessage createMessage() {
        DomibusConnectorMessageDetails messageDetails = createDomibusConnectorMessageDetails();
        
        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent("xmlContent".getBytes());
        
        DetachedSignature detachedSignature = new DetachedSignature("detachedSignature".getBytes(), "signaturename", DetachedSignatureMimeType.BINARY);

        LargeFileReferenceGetSetBased inMemory = new LargeFileReferenceGetSetBased();
        inMemory.setBytes("documentbytes".getBytes());
        inMemory.setReadable(true);
               
        DomibusConnectorMessageDocument messageDocument = new DomibusConnectorMessageDocument(inMemory, "Document1.pdf", detachedSignature);
                        
        messageContent.setDocument(messageDocument);
        
        DomibusConnectorMessage msg = new DomibusConnectorMessage(messageDetails, messageContent);
        msg.addTransportedMessageConfirmation(createMessageDeliveryConfirmation());
        msg.addAttachment(createSimpleMessageAttachment());
        msg.addError(createMessageError());
        return msg;
    }
    
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
     * creates a error message with
     * #createSimpleDomibusConnectorMessage as message
     * "error detail message" as details
     * "error message" as text
     * "error source" as error source
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
