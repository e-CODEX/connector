package eu.domibus.connector.persistence.model.test.util;

import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageError;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageInfo;
import eu.domibus.connector.persistence.model.PDomibusConnectorParty;
import eu.domibus.connector.persistence.model.PDomibusConnectorService;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import lombok.experimental.UtilityClass;

/**
 * Creates other persistence entities.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@UtilityClass
public class PersistenceEntityCreator {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates a new instance of PDomibusConnectorAction initialized with default values.
     *
     * @return A new instance of PDomibusConnectorAction.
     */
    public static PDomibusConnectorAction createAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("action1");
        return domibusConnectorAction;
    }

    /**
     * Creates a new instance of PDomibusConnectorAction initialized with the action value
     * "RelayREMMDAcceptanceRejection".
     *
     * @return A new instance of PDomibusConnectorAction.
     */
    public static PDomibusConnectorAction createRelayREMMDAcceptanceRejectionAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("RelayREMMDAcceptanceRejection");
        return domibusConnectorAction;
    }

    /**
     * Creates a new instance of PDomibusConnectorAction with the action set to
     * "DeliveryNonDeliveryToRecipient".
     *
     * @return A new instance of PDomibusConnectorAction.
     */
    public static PDomibusConnectorAction createDeliveryNonDeliveryToRecipientAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("DeliveryNonDeliveryToRecipient");
        return domibusConnectorAction;
    }

    /**
     * Creates a new instance of PDomibusConnectorAction with the action set to
     * "RetrievalNonRetrievalToRecipient".
     *
     * @return A new instance of PDomibusConnectorAction.
     */
    public static PDomibusConnectorAction createRetrievalNonRetrievalToRecipientAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("RetrievalNonRetrievalToRecipient");
        return domibusConnectorAction;
    }

    /**
     * Creates a new instance of PDomibusConnectorAction with the action set to
     * "RelayREMMDFailure".
     *
     * @return A new instance of PDomibusConnectorAction.
     */
    public static PDomibusConnectorAction createRelayREMMDFailureAction() {
        PDomibusConnectorAction domibusConnectorAction = new PDomibusConnectorAction();
        domibusConnectorAction.setAction("RelayREMMDFailure");
        return domibusConnectorAction;
    }

    /**
     * Creates a new instance of PDomibusConnectorService with the service set to "EPO" and the
     * service type set to "urn:e-codex:services:".
     *
     * @return A new instance of PDomibusConnectorService.
     */
    public static PDomibusConnectorService createServiceEPO() {
        PDomibusConnectorService service = new PDomibusConnectorService();
        service.setService("EPO");
        service.setServiceType("urn:e-codex:services:");
        return service;
    }

    /**
     * This method creates a new instance of PDomibusConnectorService initialized with the default
     * values.
     *
     * @return A new instance of PDomibusConnectorService
     */
    public static PDomibusConnectorService createServicePing() {
        PDomibusConnectorService service = new PDomibusConnectorService();
        service.setService("Ping");
        service.setServiceType("urn:e-codex:services:");
        return service;
    }

    /**
     * Creates a new instance of PDomibusConnectorParty initialized with default values.
     *
     * @return A new instance of PDomibusConnectorParty.
     */
    public static PDomibusConnectorParty createPartyAT() {
        PDomibusConnectorParty at = new PDomibusConnectorParty();
        at.setPartyId("AT");
        at.setRole("GW");
        at.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        return at;
    }

    /**
     * Creates a new instance of PDomibusConnectorParty initialized with default values.
     *
     * @return A new instance of PDomibusConnectorParty.
     */
    public static PDomibusConnectorParty createPartyDomibusBLUE() {
        PDomibusConnectorParty at = new PDomibusConnectorParty();
        at.setPartyId("domibus-blue");
        at.setRole("GW");
        at.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        return at;
    }

    /**
     * Creates a new instance of PDomibusConnectorEvidence representing evidence related to a
     * message in the DomibusConnector system. The evidence can be used to track the delivery of the
     * message to the backend or gateway.
     *
     * @return A new instance of PDomibusConnectorEvidence.
     */
    public static PDomibusConnectorEvidence createDeliveryEvidence() {
        PDomibusConnectorEvidence evidence = new PDomibusConnectorEvidence();
        evidence.setBusinessMessage(createSimpleDomibusConnectorMessage());
        evidence.setType(EvidenceType.DELIVERY);
        evidence.setId(13L);
        return evidence;
    }

    /**
     * Creates a new instance of PDomibusConnectorEvidence representing evidence related to a
     * message in the DomibusConnector system. The evidence can be used to track the delivery of the
     * message to the backend or gateway.
     *
     * @return A new instance of PDomibusConnectorEvidence.
     */
    public static PDomibusConnectorEvidence createNonDeliveryEvidence() {
        PDomibusConnectorEvidence evidence = new PDomibusConnectorEvidence();
        evidence.setBusinessMessage(createSimpleDomibusConnectorMessage());
        evidence.setType(EvidenceType.NON_DELIVERY);
        evidence.setId(14L);
        return evidence;
    }

    /**
     * Creates an error message with #createSimpleDomibusConnectorMessage as message "error detail
     * message" as detailed text "error message" as error message "error source" as error source.
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
     * Creates a default PDomibusConnectorMessage, for testing purposes it is a message with message
     * content! and NO evidences.
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

    /**
     * Creates a new instance of PDomibusConnectorMessageInfo with default values initialized. Sets
     * the 'to', 'from', 'service', 'action', 'created', 'finalRecipient', 'originalSender', and
     * 'updated' properties.
     *
     * @return A new instance of PDomibusConnectorMessageInfo.
     */
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
