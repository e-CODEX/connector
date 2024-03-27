package eu.domibus.connector.controller.exception;

public class DomibusConnectorControllerException extends RuntimeException {
    private static final long serialVersionUID = -2144504174829687755L;

    public DomibusConnectorControllerException() {
    }

    public DomibusConnectorControllerException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorControllerException(Throwable arg0) {
        super(arg0);
    }

    public DomibusConnectorControllerException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
