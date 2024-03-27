package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;


/**
 * Takes a message and persists all big data of this message into
 * storage
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface DCMessageContentManager {
    // void saveMessagePayloads(@Nonnull DomibusConnectorMessage message) throws PersistenceException;

    /**
     * this method should remove all messages from storage which is related to this
     * message
     *
     * @param message - the message
     */
    void cleanForMessage(DomibusConnectorMessage message);
}
