package eu.domibus.connector.controller.exception;

public class DomibusConnectorBackendException extends RuntimeException {
    public DomibusConnectorBackendException() {
    }

    public DomibusConnectorBackendException(String message) {
        super(message);
    }

    public DomibusConnectorBackendException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomibusConnectorBackendException(Throwable cause) {
        super(cause);
    }

    public DomibusConnectorBackendException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
