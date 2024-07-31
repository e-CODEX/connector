/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;

/**
 * Service for generating a for the connector unique message id
 * if multiple instances are running the generated id must be unique over all
 * instances.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface DomibusConnectorMessageIdGenerator {
    /**
     * Generates a unique Message Id for a received message
     * the maximum string length for this id is 255.
     *
     * @return the message id
     */
    DomibusConnectorMessageId generateDomibusConnectorMessageId();
}
