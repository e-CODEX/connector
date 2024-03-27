package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CreateNewBusinessMessageInDBStep implements MessageProcessStep {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateNewBusinessMessageInDBStep.class);
    private final DCMessagePersistenceService messagePersistenceService;

    public CreateNewBusinessMessageInDBStep(DCMessagePersistenceService messagePersistenceService) {
        this.messagePersistenceService = messagePersistenceService;
    }

    @Override
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "CreateNewBusinessMessageInDBStep")
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        messagePersistenceService.persistBusinessMessageIntoDatabase(domibusConnectorMessage);
        LOGGER.debug(LoggingMarker.BUSINESS_LOG, "Successfully created (uncommitted) new business message in database");
        return true;
    }
}
