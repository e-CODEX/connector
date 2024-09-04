/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.exception;

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
