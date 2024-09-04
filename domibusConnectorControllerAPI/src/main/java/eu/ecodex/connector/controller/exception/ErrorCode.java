/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.exception;

import java.io.Serializable;
import lombok.Getter;

/**
 * The ErrorCode class represents an error code and its corresponding description.
 */
@Getter
public class ErrorCode implements Serializable {
    public static final ErrorCode EVIDENCE_IGNORED_MESSAGE_ALREADY_REJECTED = new ErrorCode(
        "E101",
        "The processed evidence is ignored, because the business message is already "
            + "in rejected state"
    );
    public static final ErrorCode EVIDENCE_IGNORED_DUE_DUPLICATE = new ErrorCode(
        "E102",
        "The processed evidence is ignored, because max occurrence number of evidence type exceeded"
    );
    public static final ErrorCode EVIDENCE_IGNORED_DUE_HIGHER_PRIORITY = new ErrorCode(
        "E103",
        "The processed evidence is not relevant due another evidence with higher priority"
    );
    public static final ErrorCode LINK_PARTNER_NOT_FOUND =
        new ErrorCode("L104", "The requested LinkPartner is not configured");
    public static final ErrorCode LINK_PARTNER_NOT_ACTIVE =
        new ErrorCode("L101", "The requested LinkPartner is not active");
    private final String code;
    private final String description;

    public ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String toString() {
        return this.code + ": " + this.description;
    }
}
