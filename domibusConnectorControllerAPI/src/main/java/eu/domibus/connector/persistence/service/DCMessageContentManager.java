/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

/**
 * Takes a message and persists all big data of this message into storage.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface DCMessageContentManager {

    /**
     * this method should remove all messages from storage which is related to this message.
     *
     * @param message - the message
     */
    void cleanForMessage(DomibusConnectorMessage message);
}
