/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * The UserLoginException class is a custom exception that is thrown when there is an error during
 * the user login process. This exception extends the AuthenticationException class.
 *
 * @see AuthenticationException
 */
public class UserLoginException extends AuthenticationException {
    public UserLoginException(String explanation) {
        super(explanation);
    }

    public UserLoginException(String msg, Throwable t) {
        super(msg, t);
    }
}
