/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.exception;

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A builder class for creating instances of DomibusConnectorMessageException.
 * It provides methods to set the attributes of the exception before building and throwing it.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }.
 */
public class DomibusConnectorMessageExceptionBuilder {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorMessageExceptionBuilder.class);
    private DomibusConnectorMessage message;
    private DomibusConnectorMessageId messageId;
    private Class<?> source;
    private Throwable cause;
    private String text;
    private boolean logBeforeThrow = true;

    private DomibusConnectorMessageExceptionBuilder() {
    }

    public static DomibusConnectorMessageExceptionBuilder createBuilder() {
        return new DomibusConnectorMessageExceptionBuilder();
    }

    public DomibusConnectorMessageExceptionBuilder setMessage(DomibusConnectorMessage message) {
        this.message = message;
        return this;
    }

    public DomibusConnectorMessageExceptionBuilder setMessage(DomibusConnectorMessageId message) {
        this.messageId = message;
        return this;
    }

    /**
     * Can be called to set the source of the exception or call setSourceObject.
     *
     * @param source sets the source component of the exception
     * @return the builder
     */
    public DomibusConnectorMessageExceptionBuilder setSource(Class<?> source) {
        this.source = source;
        return this;
    }

    /**
     * Can be called to set the source of the exception or call setSource.
     *
     * @param object - sets the source object of the exception (calls object.getClass())
     * @return the builder
     */
    public DomibusConnectorMessageExceptionBuilder setSourceObject(@Nonnull Object object) {
        this.source = object.getClass();
        return this;
    }

    public DomibusConnectorMessageExceptionBuilder setCause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public DomibusConnectorMessageExceptionBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public DomibusConnectorMessageExceptionBuilder setLogBeforeThrow(boolean logBeforeThrow) {
        this.logBeforeThrow = logBeforeThrow;
        return this;
    }

    /**
     * Builds and returns a DomibusConnectorMessageException object based on
     * the provided parameters.
     *
     * @return A DomibusConnectorMessageException object.
     * @throws IllegalArgumentException if the message parameter is null.
     */
    public DomibusConnectorMessageException build() {
        if (message == null) {
            throw new IllegalArgumentException("Cannot create Exception without message set!");
        }

        DomibusConnectorMessageException exception;
        if (text != null && cause != null) {
            exception = new DomibusConnectorMessageException(message, source, cause, text);
        } else if (text != null) {
            exception = new DomibusConnectorMessageException(message, source, text);
        } else if (cause != null) {
            exception = new DomibusConnectorMessageException(message, source, cause);
        } else {
            exception = new DomibusConnectorMessageException(message, source);
        }
        return exception;
    }

    /**
     * Builds and throws a DomibusConnectorMessageException object based on the provided parameters.
     *
     * @throws DomibusConnectorMessageException if the message parameter is null or if
     *                                          an error occurs while generating the exception.
     */
    public void buildAndThrow() throws DomibusConnectorMessageException {
        DomibusConnectorMessageException build = build();
        if (logBeforeThrow && this.source != null) {
            LoggerFactory.getLogger(this.source)
                .debug("Throwing exception with MessageExceptionBuilder", build);
        } else if (logBeforeThrow) {
            LOGGER.debug("Throwing exception with MessageExceptionBuilder", build);
        }
        throw build;
    }
}
