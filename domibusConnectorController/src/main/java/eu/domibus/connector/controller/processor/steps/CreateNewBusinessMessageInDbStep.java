/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class representing a step in the message processing pipeline that creates
 * a new business message in the database.
 */
@Component
public class CreateNewBusinessMessageInDbStep implements MessageProcessStep {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(CreateNewBusinessMessageInDbStep.class);
    private final DCMessagePersistenceService messagePersistenceService;

    public CreateNewBusinessMessageInDbStep(DCMessagePersistenceService messagePersistenceService) {
        this.messagePersistenceService = messagePersistenceService;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
        value = "CreateNewBusinessMessageInDBStep"
    )
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        messagePersistenceService.persistBusinessMessageIntoDatabase(domibusConnectorMessage);
        LOGGER.debug(
            LoggingMarker.BUSINESS_LOG,
            "Successfully created (uncommitted) new business message in database"
        );
        return true;
    }
}
