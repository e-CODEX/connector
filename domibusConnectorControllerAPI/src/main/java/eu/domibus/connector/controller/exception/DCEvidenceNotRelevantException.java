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
