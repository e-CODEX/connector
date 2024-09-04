/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.processor.steps;

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.persistence.service.DCMessagePersistenceService;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import eu.ecodex.connector.tools.logging.LoggingMarker;
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
