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
 * The PersistenceException class is a subclass of the RuntimeException class. It is thrown when
 * there is an error in the persistence of data.
 */
public class PersistenceException extends RuntimeException {
    private static final long serialVersionUID = -5879716562932550680L;

    public PersistenceException() {
    }

    public PersistenceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public PersistenceException(String arg0) {
        super(arg0);
    }

    public PersistenceException(Throwable arg0) {
        super(arg0);
    }
}
