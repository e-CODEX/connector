/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.exceptions;

/**
 * This exception is thrown when there is an error in the persistence of evidence. It extends the
 * PersistenceException class.
 */
public class EvidencePersistenceException extends PersistenceException {
    public EvidencePersistenceException() {
    }

    public EvidencePersistenceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EvidencePersistenceException(String arg0) {
        super(arg0);
    }

    public EvidencePersistenceException(Throwable arg0) {
        super(arg0);
    }
}
