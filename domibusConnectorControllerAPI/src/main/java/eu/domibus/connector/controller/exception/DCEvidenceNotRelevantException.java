package eu.domibus.connector.controller.exception;

import eu.domibus.connector.persistence.service.exceptions.DuplicateEvidencePersistenceException;


public class DCEvidenceNotRelevantException extends DCEvidenceProcessingException {
    public DCEvidenceNotRelevantException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DCEvidenceNotRelevantException(ErrorCode errorCode, DuplicateEvidencePersistenceException e) {
        super(errorCode, e);
    }
}
