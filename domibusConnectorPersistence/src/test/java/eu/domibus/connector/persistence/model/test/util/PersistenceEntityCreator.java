package eu.domibus.connector.persistence.model.test.util;

import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.persistence.model.*;
import eu.domibus.connector.persistence.model.enums.EvidenceType;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;


/**
 * creates other persistence entities
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class PersistenceEntityCreator {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static PDomibusConnectorAction createAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("action1");
        //        domibusConnectorAction.setDocumentRequired(true);
        return domibusConnectorAction;
    }

    public static PDomibusConnectorAction createRelayREMMDAcceptanceRejectionAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("RelayREMMDAcceptanceRejection");
        //        domibusConnectorAction.setDocumentRequired(false);
        return domibusConnectorAction;
    }

    public static PDomibusConnectorAction createDeliveryNonDeliveryToRecipientAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("DeliveryNonDeliveryToRecipient");
        //        domibusConnectorAction.setDocumentRequired(false);
        return domibusConnectorAction;
    }

    public static PDomibusConnectorAction createRetrievalNonRetrievalToRecipientAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("RetrievalNonRetrievalToRecipient");
        //        domibusConnectorAction.setDocumentRequired(false);
        return domibusConnectorAction;
    }

    public static PDomibusConnectorAction createRelayREMMDFailureAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("RelayREMMDFailure");
        //        domibusConnectorAction.setDocumentRequired(false);
        return domibusConnectorAction;
    }

    public static PDomibusConnectorService createServiceEPO() {
        PDomibusConnectorService service = new PDomibusConnectorService();
        service.setService("EPO");
        service.setServiceType("urn:e-codex:services:");
        return service;
    }

    public static PDomibusConnectorService createServicePing() {
        PDomibusConnectorService service = new PDomibusConnectorService();
        service.setService("Ping");
        service.setServiceType("urn:e-codex:services:");
        return service;
    }

    public static PDomibusConnectorParty createPartyAT() {
        PDomibusConnectorParty at = new PDomibusConnectorParty();
        at.setPartyId("AT");
        at.setRole("GW");
        at.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        return at;
    }

    public static PDomibusConnectorParty createPartyDomibusBLUE() {
        PDomibusConnectorParty at = new PDomibusConnectorParty();
        at.setPartyId("domibus-blue");
        at.setRole("GW");
        at.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        return at;
    }

    //    public static PDomibusConnectorPartyPK createPartyPKforPartyAT() {
    //        return new PDomibusConnectorPartyPK("AT", "GW");
    //    }

    public static PDomibusConnectorEvidence createDeliveryEvidence() {
        PDomibusConnectorEvidence evidence = new PDomibusConnectorEvidence();
        evidence.setBusinessMessage(createSimpleDomibusConnectorMessage());
        evidence.setType(EvidenceType.DELIVERY);
        evidence.setId(13L);
        return evidence;
    }

    public static PDomibusConnectorEvidence createNonDeliveryEvidence() {
        PDomibusConnectorEvidence evidence = new PDomibusConnectorEvidence();
        evidence.setBusinessMessage(createSimpleDomibusConnectorMessage());
        evidence.setType(EvidenceType.NON_DELIVERY);
        evidence.setId(14L);
        return evidence;
    }

    /**
     * creates a error message with
     * #createSimpleDomibusConnectorMessage as message
     * "error detail message" as detailed text
     * "error message" as error message
     * "error source" as error source
     *
     * @return the MessageError
     */
    public static PDomibusConnectorMessageError createMessageError() {
        PDomibusConnectorMessageError error = new PDomibusConnectorMessageError();
        error.setDetailedText("error detail message");
        error.setErrorMessage("error message");
        error.setErrorSource("error source");
        error.setMessage(createSimpleDomibusConnectorMessage());
        return error;
    }

    /**
     * Creates a default PDomibusConnectorMessage, for testing purposes
     * it is a message with message content! and NO evidences
     *
     * @return - the message
     */
    public static PDomibusConnectorMessage createSimpleDomibusConnectorMessage() {
        PDomibusConnectorMessage msg = new PDomibusConnectorMessage();
        msg.setBackendMessageId("national1");
        msg.setEbmsMessageId("ebms1");
        msg.setConfirmed(LocalDateTime.parse("2017-12-23T23:45:23").atZone(ZoneId.systemDefault()));
        msg.setConversationId("conversation1");
        msg.setHashValue("hashvalue");
        msg.setConnectorMessageId("messagestamp");
        msg.setId(47L);
        msg.setRelatedEvidences(new HashSet<>());
        msg.setDirectionSource(MessageTargetSource.BACKEND);
        msg.setDirectionTarget(MessageTargetSource.GATEWAY);
        return msg;
    }

    public static PDomibusConnectorMessageInfo createSimpleMessageInfo() {
        PDomibusConnectorMessageInfo messageInfo = new PDomibusConnectorMessageInfo();
        messageInfo.setTo(createPartyAT());
        messageInfo.setFrom(createPartyDomibusBLUE());
        messageInfo.setService(createServiceEPO());
        messageInfo.setAction(createRelayREMMDAcceptanceRejectionAction());
        messageInfo.setCreated(new Date());
        messageInfo.setFinalRecipient("finalRecipient");
        messageInfo.setOriginalSender("originalSender");
        messageInfo.setUpdated(new Date());
        return messageInfo;
    }
}
