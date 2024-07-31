/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.exception;

/**
 * The DomibusConnectorGatewaySubmissionException class is an exception that is thrown when a
 * submission to the gateway fails in the DomibusConnectorGateway class.
 * It extends the Exception class and includes constructors for creating an exception object with
 * a custom message, with a cause, or with both a custom message and a cause.
 *
 * @see Exception
 */
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

    public DomibusConnectorGatewaySubmissionException(
        String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
