/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.exception;

import lombok.Getter;

/**
 * The DCEvidenceProcessingException class represents an exception that is thrown when there
 * is an error during the processing of evidence in a DC (Data Center) system.
 */
@Getter
public class DCEvidenceProcessingException extends RuntimeException {
    private final ErrorCode errorCode;

    public DCEvidenceProcessingException(ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    public DCEvidenceProcessingException(ErrorCode errorCode, Throwable e) {
        super(errorCode.toString(), e);
        this.errorCode = errorCode;
    }
}
