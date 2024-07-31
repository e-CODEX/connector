/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
