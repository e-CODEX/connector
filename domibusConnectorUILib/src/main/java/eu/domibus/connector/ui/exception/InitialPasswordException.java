/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * The InitialPasswordException class is a custom exception that is thrown when a user attempts to
 * log in with an initial password that has not been changed. This exception extends the
 * AuthenticationException class.
 *
 * @see AuthenticationException
 */
public class InitialPasswordException extends AuthenticationException {
    public InitialPasswordException(String explanation) {
        super(explanation);
    }

    public InitialPasswordException(String msg, Throwable t) {
        super(msg, t);
    }
}
