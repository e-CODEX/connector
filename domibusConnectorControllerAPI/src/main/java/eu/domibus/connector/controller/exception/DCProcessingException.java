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

/**
 * The DCProcessingException class represents an exception that occurs during the processing of DC
 * (Data Center) operation.
 * It is a subclass of the RuntimeException class, which means it is an unchecked exception.
 */
public class DCProcessingException extends RuntimeException {
    public DCProcessingException(String message) {
        super(message);
    }
}
