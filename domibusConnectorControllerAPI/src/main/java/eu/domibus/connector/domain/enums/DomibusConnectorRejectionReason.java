/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.enums;

import lombok.Getter;

/**
 * Enum representing the possible reasons for rejection of a submission in Domibus Connector.
 */
@Getter
public enum DomibusConnectorRejectionReason {
    //    SOAP_FAULT("E100", ""),
    //    SAML_TOKEN_VALIDATION("E101", ""),
    //    WS_ADDRESSING_FAULT(""),
    //    UNKNOWN_ORIGINATOR_ADDRESS(""),
    //    UNKNOWN_RECIPIENT_ADDRESS(""),
    UNSPECIFIC_PROCESSING_ERROR("E100", ""),
    //    WRONG_INPUT_DATA(""),
    //    DUPLICATE_MSG_ID(""),
    OTHER("E100", ""),
    BACKEND_REJECTION(
        "E200",
        "The connector backend or backend application rejected the message"
    ),
    GW_REJECTION("E201", "The gateway or gateway application rejected the message"),
    RELAY_REMMD_TIMEOUT(
        "E301",
        "The maximum wait time for a RELAY_REMMD_ACCEPTANCE/RELAY_REMMD_REJECTION "
            + "evidence has been reached!"
    ),
    DELIVERY_EVIDENCE_TIMEOUT(
        "E300",
        "The maximum wait time for a NON_DELIVERY/DELIVERY evidence has been reached!"
    ),
    UNREACHABLE("E404", "");
    private final String reasonText;
    private final String errorCode;

    DomibusConnectorRejectionReason(String errorCode, String reasonText) {
        this.errorCode = errorCode;
        this.reasonText = reasonText;
    }
}
