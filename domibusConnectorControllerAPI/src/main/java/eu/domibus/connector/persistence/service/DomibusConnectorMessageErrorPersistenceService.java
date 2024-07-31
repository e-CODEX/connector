/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * The DomibusConnectorMessageErrorPersistenceService interface provides methods to interact with
 * the persistence layer for managing errors related to a DomibusConnectorMessage.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface DomibusConnectorMessageErrorPersistenceService {
    /**
     * Finds all errors related to the message.
     *
     * @param message the message
     * @return the error list
     * @throws PersistenceException if there are errors accessing or reading from persistence layer
     */
    @Nonnull
    List<DomibusConnectorMessageError> getMessageErrors(@Nonnull DomibusConnectorMessage message)
        throws PersistenceException;

    /**
     * Persists an error to the message.
     *
     * @param connectorMessageId the connectorMessageId
     * @param messageError       the message error
     */
    void persistMessageError(String connectorMessageId, DomibusConnectorMessageError messageError);

    default void persistMessageError(DomibusConnectorMessageId id,
                                     DomibusConnectorMessageError messageError) {
        this.persistMessageError(id.getConnectorMessageId(), messageError);
    }
}
