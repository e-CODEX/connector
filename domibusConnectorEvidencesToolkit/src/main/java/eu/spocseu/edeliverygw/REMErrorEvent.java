package eu.spocseu.edeliverygw;

import eu.spocseu.common.SpocsConstants.Evidences;
import org.etsi.uri._02640.v2.EventReasonsType;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;


public enum REMErrorEvent {
    SOAP_FAULT(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.etsi.org/REM/EventReason#TechnicalMalfunction",
            null,
            SOAPConstants.SOAP_RECEIVER_FAULT
    ),
    SAML_TOKEN_VALIDATION(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.etsi.org/REM/EventReason#PolicyViolation",
            null,
            SOAPConstants.SOAP_SENDER_FAULT
    ),
    WS_ADDRESSING_FAULT(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.etsi.org/REM/EventReason#PolicyViolation",
            "Invalid action URI",
            SOAPConstants.SOAP_SENDER_FAULT
    ),
    UNKNOWN_ORIGINATOR_ADDRESS(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified",
            "Originator not known",
            SOAPConstants.SOAP_SENDER_FAULT
    ),
    UNKNOWN_RECIPIENT_ADDRESS(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.etsi.org/REM/EventReason#UnknownRecipient",
            "Recipient not known",
            SOAPConstants.SOAP_SENDER_FAULT
    ),
    UNSPECIFIC_PROCESSING_ERROR(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.etsi.org/REM/EventReason#TechnicalMalfunction",
            null,
            SOAPConstants.SOAP_RECEIVER_FAULT
    ),
    WRONG_INPUT_DATA(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.etsi.org/REM/EventReason#InvalidMessageFormat",
            null,
            SOAPConstants.SOAP_SENDER_FAULT
    ),
    DUPLICATE_MSG_ID(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.eu-spocs.eu/edelivery/v1#DuplicateMsgID",
            null,
            SOAPConstants.SOAP_SENDER_FAULT
    ),
    OTHER(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.etsi.org/REM/EventReason#Other",
            null,
            SOAPConstants.SOAP_RECEIVER_FAULT
    ),
    UNREACHABLE(
            Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
            "http:uri.etsi.org/REM/EventReason#R_REMMD_Unreachable",
            null,
            SOAPConstants.SOAP_SENDER_FAULT
    );

    // SOAP_FAULT(
    // Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION, "SoapFaultException",
    // "http:uri.etsi.org/REM/EventReason#R-REMMD_Malfunction", null),
    // SAML_TOKEN_VALIDATION(
    // Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION, null,
    // "http:uri.etsi.org/REM/EventReason#PolicyViolation", null),
    // WS_ADDRESSING_FAULT(
    // Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
    // "http:uri.etsi.org/REM/EventReason#InvalidMessageFormat",
    // "http://www.w3.org/2005/08/addressing/fault", "Invalid action URI"),
    // UNKNOWN_ORIGINATOR_ADDRESS(
    // Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
    // "http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified",
    // "http://www.w3.org/2005/08/addressing/soap/fault",
    // "Originator not known"),
    // UNKNOWN_RECIPIENT_ADDRESS(
    // Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
    // "http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified",
    // "http://www.w3.org/2005/08/addressing/soap/fault",
    // "Recipient not known"),
    // UNSPECIFIC_PROCESSING_ERROR(
    // Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
    // "Unspecific processing error", "TechnicalMalfunction", null),
    // WRONG_INPUT_DATA(
    // Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
    // "Wrong input data causes exception", "WrongInputData", null);
    private Evidences evidence;
    private String eventCode;
    private String eventDetails;
    private QName actor;

    REMErrorEvent(
            Evidences _evidence, String _eventCode, String _eventDetails, QName actor) {

        evidence = _evidence;
        eventCode = _eventCode;
        eventDetails = _eventDetails;
    }

    public Evidences getEvidence() {
        return evidence;
    }

    public String getEventCode() {
        return eventCode;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public QName getActor() {
        return actor;
    }

    public static REMErrorEvent getRemErrorEventForJaxB(EventReasonsType jaxb) {
        if (jaxb.getEventReason() == null) return null;

        String code = jaxb.getEventReason().get(0).getCode();

        if (code.equals(SOAP_FAULT.getEventCode())) return SOAP_FAULT;
        else if (code.equals(SAML_TOKEN_VALIDATION.getEventCode())) return SAML_TOKEN_VALIDATION;
        else if (code.equals(WS_ADDRESSING_FAULT.getEventCode())) return WS_ADDRESSING_FAULT;
        else if (code.equals(UNKNOWN_ORIGINATOR_ADDRESS.getEventCode())) return UNKNOWN_ORIGINATOR_ADDRESS;
        else if (code.equals(UNKNOWN_RECIPIENT_ADDRESS.getEventCode())) return UNKNOWN_RECIPIENT_ADDRESS;
        else if (code.equals(UNSPECIFIC_PROCESSING_ERROR.getEventCode())) return UNSPECIFIC_PROCESSING_ERROR;
        else if (code.equals(WRONG_INPUT_DATA.getEventCode())) return WRONG_INPUT_DATA;
        else if (code.equals(OTHER.getEventCode())) return OTHER;
        else if (code.equals(DUPLICATE_MSG_ID.getEventCode())) return DUPLICATE_MSG_ID;
        else if (code.equals(UNREACHABLE.getEventCode())) return UNREACHABLE;
        else return null;
    }
}
