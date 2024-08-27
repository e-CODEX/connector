/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
