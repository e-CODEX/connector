package eu.domibus.connector.controller.exception.handling;

import eu.domibus.connector.controller.exception.DomibusConnectorMessageException;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import eu.domibus.connector.persistence.service.DomibusConnectorMessageErrorPersistenceService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Aspect
@Component
public class StoreMessageExceptionIntoDatabaseAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreMessageExceptionIntoDatabaseAspect.class);

    @Autowired
    DomibusConnectorMessageErrorPersistenceService messageErrorPersistenceService;

    @Around(
            value = "@annotation(eu.domibus.connector.controller.exception.handling" +
                    ".StoreMessageExceptionIntoDatabase) " +
                    "&& @annotation(annot)", argNames = "annot"
    )
    public void handleException(ProceedingJoinPoint pjp, StoreMessageExceptionIntoDatabase annot) throws Throwable {
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
                LOGGER.debug("pass exception is [{}] so don't throw exception again", passException);
            }
        }
    }

    private void storeExceptionToDb(DomibusConnectorMessageException exception, String text) {
        LOGGER.trace("storeExceptionIntoDatabase with exception [{}]", exception);
        if (exception == null) {
            throw new IllegalArgumentException("Cannot take null as exception here!");
        }

        DomibusConnectorMessage message = exception.getDomibusConnectorMessage();
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
            messageErrorPersistenceService
                    .persistMessageError(message.getConnectorMessageId().getConnectorMessageId(), messageError);
        } else {
            String connectorId = message == null ? null : message.getConnectorMessageIdAsString();
            LOGGER.error("Cannot store exception into database either message [{}] or connectoMessageId [{}] is null!",
                         message, connectorId
            );
        }
    }

    private String getStackTraceAsString(DomibusConnectorMessageException exception) {
        String stackTrace = "";
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            stackTrace = stackTrace + "\n" + stackTraceElement.toString();
        }
        return stackTrace;
    }
}
