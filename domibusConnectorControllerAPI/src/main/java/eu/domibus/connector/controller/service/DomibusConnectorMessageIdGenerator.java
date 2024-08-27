/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
