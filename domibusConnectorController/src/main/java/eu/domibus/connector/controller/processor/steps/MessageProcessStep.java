/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

/**
 * Interface representing a step in the message processing pipeline.
 */
public interface MessageProcessStep {
    boolean executeStep(DomibusConnectorMessage domibusConnectorMessage);
}
