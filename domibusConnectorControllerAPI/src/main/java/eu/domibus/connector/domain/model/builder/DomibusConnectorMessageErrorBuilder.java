/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The DomibusConnectorMessageErrorBuilder class is used to build instances of
 * DomibusConnectorMessageError. It provides methods to set the error's text, details, source, step,
 * and processor. After setting the necessary properties, the build() method can be called to create
 * a DomibusConnectorMessageError object.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorMessageErrorBuilder {
    private String text = "";
    private String details = "";
    private String source = "";
    private String step = "";
    private String processor = "";

    private DomibusConnectorMessageErrorBuilder() {
    }

    public static DomibusConnectorMessageErrorBuilder createBuilder() {
        return new DomibusConnectorMessageErrorBuilder();
    }

    /**
     * Sets the text of the error message.
     *
     * @param text the text of the error message
     * @return the DomibusConnectorMessageErrorBuilder instance
     */
    public DomibusConnectorMessageErrorBuilder setText(@NotNull @NotBlank String text) {
        this.text = text;
        return this;
    }

    /**
     * Sets the details of the error message.
     *
     * @param details the details of the error message
     * @return the DomibusConnectorMessageErrorBuilder instance
     */
    public DomibusConnectorMessageErrorBuilder setDetails(@NotNull String details) {
        this.details = details;
        return this;
    }

    /**
     * Sets the details of the error message by creating a stack trace string from the given
     * exception.
     *
     * @param ex the exception providing the details of the error message
     * @return the DomibusConnectorMessageErrorBuilder instance
     */
    public DomibusConnectorMessageErrorBuilder setDetails(Exception ex) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        this.details = sw.toString();
        return this;
    }

    public DomibusConnectorMessageErrorBuilder setStep(@NotNull String step) {
        this.step = step;
        return this;
    }

    public DomibusConnectorMessageErrorBuilder setProcessor(@NotNull String processor) {
        this.processor = processor;
        return this;
    }

    /**
     * Builds a {@link DomibusConnectorMessageError} object with the provided error message
     * details.
     *
     * @return the built DomibusConnectorMessageError object
     * @throws RuntimeException if the text is null
     */
    public DomibusConnectorMessageError build() {
        if (text == null) {
            throw new RuntimeException("Text cannot be null!");
        }
        return new DomibusConnectorMessageError(text, details, source, step, processor);
    }

    /**
     * Sets the source of the error message.
     *
     * @param source Name of the component where the error occurred
     * @return the DomibusConnectorMessageErrorBuilder instance
     */
    public DomibusConnectorMessageErrorBuilder setSource(@NotNull String source) {
        this.source = source;
        return this;
    }

    /**
     * Sets the source of the error message.
     *
     * @param clazz Name of the component where the error occurred
     * @return the DomibusConnectorMessageErrorBuilder instance
     */
    public DomibusConnectorMessageErrorBuilder setSource(Class clazz) {
        this.source = clazz.getName();
        return this;
    }
}
