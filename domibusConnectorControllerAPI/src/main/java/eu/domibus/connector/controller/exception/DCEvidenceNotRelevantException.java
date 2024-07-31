/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.exception;

import eu.domibus.connector.persistence.service.exceptions.DuplicateEvidencePersistenceException;

/**
 * This exception is thrown when the evidence is not relevant for processing.
 * It extends the DCEvidenceProcessingException class.
 */
public class DCEvidenceNotRelevantException extends DCEvidenceProcessingException {
    public DCEvidenceNotRelevantException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DCEvidenceNotRelevantException(ErrorCode errorCode,
                                          DuplicateEvidencePersistenceException e) {
        super(errorCode, e);
    }
}
