/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

/**
 * Represents an expression.
 */
public abstract class Expression {
    abstract boolean evaluate(DomibusConnectorMessage message);
}
