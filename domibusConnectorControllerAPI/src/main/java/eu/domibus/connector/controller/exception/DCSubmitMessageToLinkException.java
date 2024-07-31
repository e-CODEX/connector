/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.exception;

/**
 * DCSubmitMessageToLinkException is a specific exception class that extends
 * DomibusConnectorControllerException.
 * It is thrown when there is an error submitting a message to a link in the Domibus Connector
 * controller.
 */
public final class DCSubmitMessageToLinkException extends DomibusConnectorControllerException {
    public DCSubmitMessageToLinkException() {
    }

    public DCSubmitMessageToLinkException(String arg0) {
        super(arg0);
    }

    public DCSubmitMessageToLinkException(Throwable arg0) {
        super(arg0);
    }

    public DCSubmitMessageToLinkException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
