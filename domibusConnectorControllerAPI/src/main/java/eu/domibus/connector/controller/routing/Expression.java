package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;


public abstract class Expression {
    abstract boolean evaluate(DomibusConnectorMessage message);
}
