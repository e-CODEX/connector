/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.exception;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import java.io.Serial;
import lombok.Getter;

/**
 * Custom exception class for Domibus connector messages.
 * Represents an exception that occurred while working with a DomibusConnectorMessage.
 */
@Getter
public class DomibusConnectorMessageException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2899706995862182574L;
   
    private DomibusConnectorMessage domibusConnectorMessage;
    private Class<?> source;

    public DomibusConnectorMessageException() {
    }

    /**
     * Creates a new instance of the DomibusConnectorMessageException class with
     * the specified DomibusConnectorMessage, source and class.
     *
     * @param message The DomibusConnectorMessage associated with the exception.
     * @param source  The Class representing the source class where the exception occurred.
     */
    public DomibusConnectorMessageException(DomibusConnectorMessage message, Class<?> source) {
        super();
        this.domibusConnectorMessage = message;
        this.source = source;
    }

    /**
     * Creates a new instance of the DomibusConnectorMessageException class with
     * the specified DomibusConnectorMessage, source class, and cause.
     *
     * @param message The DomibusConnectorMessage associated with the exception.
     * @param source  The Class representing the source class where the exception occurred.
     * @param cause   The Throwable that caused the exception.
     */
    public DomibusConnectorMessageException(DomibusConnectorMessage message, Class<?> source,
                                            Throwable cause) {
        super(cause);
        this.domibusConnectorMessage = message;
        this.source = source;
        this.setStackTrace(cause.getStackTrace());
    }

    /**
     * Custom exception class for Domibus connector messages.
     * Represents an exception that occurred while working with a DomibusConnectorMessage.
     *
     * @param message The DomibusConnectorMessage associated with the exception.
     * @param source  The Class representing the source class where the exception occurred.
     * @param text    The text description of the exception.
     */
    public DomibusConnectorMessageException(DomibusConnectorMessage message, Class<?> source,
                                            String text) {
        super(text);
        this.domibusConnectorMessage = message;
        this.source = source;
    }

    /**
     * Creates a new instance of the DomibusConnectorMessageException class with
     * the specified DomibusConnectorMessage, source class, cause, and text description.
     *
     * @param message The DomibusConnectorMessage associated with the exception.
     * @param source  The Class representing the source class where the exception occurred.
     * @param cause   The Throwable that caused the exception.
     * @param text    The text description of the exception.
     */
    public DomibusConnectorMessageException(DomibusConnectorMessage message, Class<?> source,
                                            Throwable cause, String text) {
        super(text, cause);
        this.domibusConnectorMessage = message;
        this.source = source;
        this.setStackTrace(cause.getStackTrace());
    }
}
