/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.evidences.exception;

import lombok.NoArgsConstructor;

/**
 * Exception class for errors encountered in the Domibus connector evidences toolkit.
 */
@NoArgsConstructor
public class DomibusConnectorEvidencesToolkitException extends RuntimeException {
    private static final long serialVersionUID = 6341995681547056010L;

    public DomibusConnectorEvidencesToolkitException(String message) {
        super(message);
    }

    public DomibusConnectorEvidencesToolkitException(Throwable cause) {
        super(cause);
    }

    public DomibusConnectorEvidencesToolkitException(String message, Throwable cause) {
        super(message, cause);
    }
}
