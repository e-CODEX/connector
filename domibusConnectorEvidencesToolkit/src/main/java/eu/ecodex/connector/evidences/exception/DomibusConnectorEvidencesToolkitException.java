/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.evidences.exception;

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
