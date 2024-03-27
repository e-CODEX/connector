package eu.domibus.connector.controller.exception;

public class DomibusConnectorGatewaySubmissionException extends Exception {
    private static final long serialVersionUID = 2128626223756418080L;

    public DomibusConnectorGatewaySubmissionException() {
    }

    public DomibusConnectorGatewaySubmissionException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorGatewaySubmissionException(Throwable arg0) {
        super(arg0);
    }

    public DomibusConnectorGatewaySubmissionException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DomibusConnectorGatewaySubmissionException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
