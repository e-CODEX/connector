/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.spocseu.edeliverygw;

import eu.spocseu.common.SpocsConstants.Evidences;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import lombok.Getter;
import org.etsi.uri._02640.v2.EventReasonsType;

/**
 * An enumeration of possible REM error events.
 */
@SuppressWarnings("squid:S1135")
@Getter
public enum REMErrorEvent {
    SOAP_FAULT(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#TechnicalMalfunction", null,
        SOAPConstants.SOAP_RECEIVER_FAULT
    ),
    SAML_TOKEN_VALIDATION(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#PolicyViolation", null,
        SOAPConstants.SOAP_SENDER_FAULT
    ),
    WS_ADDRESSING_FAULT(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#PolicyViolation",
        "Invalid action URI", SOAPConstants.SOAP_SENDER_FAULT
    ),
    UNKNOWN_ORIGINATOR_ADDRESS(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified",
        "Originator not known", SOAPConstants.SOAP_SENDER_FAULT
    ),
    UNKNOWN_RECIPIENT_ADDRESS(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#UnknownRecipient",
        "Recipient not known", SOAPConstants.SOAP_SENDER_FAULT
    ),
    UNSPECIFIC_PROCESSING_ERROR(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#TechnicalMalfunction", null,
        SOAPConstants.SOAP_RECEIVER_FAULT
    ),
    WRONG_INPUT_DATA(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#InvalidMessageFormat", null,
        SOAPConstants.SOAP_SENDER_FAULT
    ),
    DUPLICATE_MSG_ID(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.eu-spocs.eu/edelivery/v1#DuplicateMsgID", null,
        SOAPConstants.SOAP_SENDER_FAULT
    ),
    OTHER(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#Other", null,
        SOAPConstants.SOAP_RECEIVER_FAULT
    ),
    UNREACHABLE(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#R_REMMD_Unreachable", null,
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
    private final Evidences evidence;
    private final String eventCode;
    private final String eventDetails;
    private QName actor;

    REMErrorEvent(
        Evidences evidence, String eventCode,
        String eventDetails, QName actor) {

        this.evidence = evidence;
        this.eventCode = eventCode;
        this.eventDetails = eventDetails;
        // TODO init actor.
    }

    /**
     * Retrieves the corresponding REMErrorEvent based on the given JAXB EventReasonsType.
     *
     * @param jaxb The JAXB EventReasonsType object
     * @return The REMErrorEvent based on the given JAXB EventReasonsType, or null if no matching
     *      code is found
     */
    public static REMErrorEvent getRemErrorEventForJaxB(EventReasonsType jaxb) {
        if (jaxb.getEventReason() == null) {
            return null;
        }

        String code = jaxb.getEventReason().getFirst().getCode();

        if (code.equals(SOAP_FAULT.getEventCode())) {
            return SOAP_FAULT;
        } else if (code.equals(SAML_TOKEN_VALIDATION.getEventCode())) {
            return SAML_TOKEN_VALIDATION;
        } else if (code.equals(WS_ADDRESSING_FAULT.getEventCode())) {
            return WS_ADDRESSING_FAULT;
        } else if (code.equals(UNKNOWN_ORIGINATOR_ADDRESS.getEventCode())) {
            return UNKNOWN_ORIGINATOR_ADDRESS;
        } else if (code.equals(UNKNOWN_RECIPIENT_ADDRESS.getEventCode())) {
            return UNKNOWN_RECIPIENT_ADDRESS;
        } else if (code.equals(UNSPECIFIC_PROCESSING_ERROR.getEventCode())) {
            return UNSPECIFIC_PROCESSING_ERROR;
        } else if (code.equals(WRONG_INPUT_DATA.getEventCode())) {
            return WRONG_INPUT_DATA;
        } else if (code.equals(OTHER.getEventCode())) {
            return OTHER;
        } else if (code.equals(DUPLICATE_MSG_ID.getEventCode())) {
            return DUPLICATE_MSG_ID;
        } else if (code.equals(UNREACHABLE.getEventCode())) {
            return UNREACHABLE;
        } else {
            return null;
        }
    }
}
