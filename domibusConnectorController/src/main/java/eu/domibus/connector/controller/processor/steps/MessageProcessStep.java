package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;


public interface MessageProcessStep {
    boolean executeStep(DomibusConnectorMessage domibusConnectorMessage);
}
