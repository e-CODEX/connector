/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.exception;

/**
 * The DomibusConnectorControllerException class is a custom exception that extends
 * the RuntimeException class.
 * It is thrown when there is an error in the Domibus Connector controller.
 */
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
