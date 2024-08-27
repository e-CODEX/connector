/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import jakarta.annotation.Nonnull;
import java.util.List;

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

    default void persistMessageError(
        DomibusConnectorMessageId id,
        DomibusConnectorMessageError messageError) {
        this.persistMessageError(id.getConnectorMessageId(), messageError);
    }
}
