package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;

import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface DomibusConnectorMessageErrorPersistenceService {

    /**
     * finds all errors related to the message
     * @param message the message
     * @return the error list
     * @throws PersistenceException if there are errors accessing or reading from persistence layer
     */
    @Nonnull
    List<DomibusConnectorMessageError> getMessageErrors(@Nonnull DomibusConnectorMessage message) throws PersistenceException;

    /**
     * persists an error to the message
     * @param connectorMessageId the connectorMessageId
     * @param messageError the message error
     */
    void persistMessageError(String connectorMessageId, DomibusConnectorMessageError messageError);

    default void persistMessageError(DomibusConnectorMessageId id, DomibusConnectorMessageError messageError) {
        this.persistMessageError(id.getConnectorMessageId(), messageError);
    }

//    /**
//     * creates a MessageError from an exception and persists this message error
//     * @param message - the message the exception is related to
//     * @param ex - the exception
//     * @param source - class/service where the exception occured
//     * @deprecated  use {@link #persistMessageError(java.lang.String, eu.domibus.connector.domain.model.DomibusConnectorMessageError) } instead:
//     * create a DomibusConnectorMessageError before and then call #persistMessageError
//     */
//    @Deprecated
//    void persistMessageErrorFromException(DomibusConnectorMessage message, Throwable ex, Class<?> source);

}
