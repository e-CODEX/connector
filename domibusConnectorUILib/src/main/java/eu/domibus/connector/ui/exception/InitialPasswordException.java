package eu.domibus.connector.ui.exception;

import org.springframework.security.core.AuthenticationException;


public class InitialPasswordException extends AuthenticationException {
    public InitialPasswordException(String explanation) {
        super(explanation);
    }

    public InitialPasswordException(String msg, Throwable t) {
        super(msg, t);
    }
}
