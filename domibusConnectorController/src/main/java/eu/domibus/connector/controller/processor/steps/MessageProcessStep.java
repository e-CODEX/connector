package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

public interface MessageProcessStep {

    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage);

}
