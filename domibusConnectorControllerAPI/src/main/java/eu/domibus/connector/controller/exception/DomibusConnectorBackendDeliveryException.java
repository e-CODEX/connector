package eu.domibus.connector.controller.exception;

public class DomibusConnectorBackendDeliveryException extends DomibusConnectorBackendException {

    public DomibusConnectorBackendDeliveryException() {
    }

    public DomibusConnectorBackendDeliveryException(String message) {
        super(message);
    }

    public DomibusConnectorBackendDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomibusConnectorBackendDeliveryException(Throwable cause) {
        super(cause);
    }

    public DomibusConnectorBackendDeliveryException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
