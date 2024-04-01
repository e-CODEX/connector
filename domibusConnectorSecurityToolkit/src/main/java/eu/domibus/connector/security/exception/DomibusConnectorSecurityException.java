package eu.domibus.connector.security.exception;

public class DomibusConnectorSecurityException extends RuntimeException {
    private static final long serialVersionUID = 8837848390850284261L;

    public DomibusConnectorSecurityException() {
    }

    public DomibusConnectorSecurityException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorSecurityException(Throwable arg0) {
        super(arg0);
    }

    public DomibusConnectorSecurityException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
