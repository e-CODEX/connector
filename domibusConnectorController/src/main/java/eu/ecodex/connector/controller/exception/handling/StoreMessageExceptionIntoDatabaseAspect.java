/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.exception.handling;

import eu.ecodex.connector.controller.exception.DomibusConnectorMessageException;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageError;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import eu.ecodex.connector.persistence.service.DomibusConnectorMessageErrorPersistenceService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AOP aspect for storing exceptions into the database.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Aspect
@Component
public class StoreMessageExceptionIntoDatabaseAspect {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(StoreMessageExceptionIntoDatabaseAspect.class);
    @Autowired
    DomibusConnectorMessageErrorPersistenceService messageErrorPersistenceService;

    /**
     * Handles exceptions and stores them into the database.
     *
     * @param pjp   The ProceedingJoinPoint representing the method being intercepted.
     * @param annot The StoreMessageExceptionIntoDatabase annotation applied to the method.
     * @throws Throwable if any exception occurs.
     */
    @Around(
        value = "@annotation(eu.ecodex.connector.controller.exception.handling"
            + ".StoreMessageExceptionIntoDatabase) && @annotation(annot)",
        argNames = "annot"
    )
    public void handleException(ProceedingJoinPoint pjp, StoreMessageExceptionIntoDatabase annot)
        throws Throwable {
        boolean passException = annot.passException();
        String text = annot.infoText();
        LOGGER.trace("executing Aspect StoreMessageExceptionIntoDatabaseAspect");

        try {
            pjp.proceed();
        } catch (DomibusConnectorMessageException exception) {

            storeExceptionToDb(exception, text);

            if (passException) {
                LOGGER.debug("pass exception is [{}] so passing exception on", passException);
                throw exception;
            } else {
                LOGGER.debug(
                    "pass exception is [{}] so don't throw exception again", passException);
            }
        }
    }

    private void storeExceptionToDb(DomibusConnectorMessageException exception, String text) {
        LOGGER.trace("storeExceptionIntoDatabase with exception [{}]", exception);
        if (exception == null) {
            throw new IllegalArgumentException("Cannot take null as exception here!");
        }

        var message = exception.getDomibusConnectorMessage();
        if (message != null && message.getConnectorMessageIdAsString() != null) {

            DomibusConnectorMessageErrorBuilder messageErrorBuilder =
                DomibusConnectorMessageErrorBuilder.createBuilder()
                    .setText(exception.getMessage())
                    .setDetails(getStackTraceAsString(exception));
            if (exception.getMessage() == null) {
                messageErrorBuilder.setText("");
            }

            if (exception.getCause() != null) {
                messageErrorBuilder.setSource(exception.getCause().getClass().getName());
            }

            DomibusConnectorMessageError messageError = messageErrorBuilder.build();

            messageErrorPersistenceService.persistMessageError(
                message.getConnectorMessageId().getConnectorMessageId(), messageError);
        } else {
            String connectorId = message == null ? null : message.getConnectorMessageIdAsString();
            String errorMessage = "Cannot store exception into database either message [{}] "
                + "Cor connectorMessageId [{}] is null!";
            LOGGER.error(
                errorMessage,
                message,
                connectorId
            );
        }
    }

    private String getStackTraceAsString(DomibusConnectorMessageException exception) {
        var stackTrace = new StringBuilder();
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            stackTrace.append("\n").append(stackTraceElement.toString());
        }
        return stackTrace.toString();
    }
}
