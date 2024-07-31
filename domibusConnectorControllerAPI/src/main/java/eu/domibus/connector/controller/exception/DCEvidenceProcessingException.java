/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
