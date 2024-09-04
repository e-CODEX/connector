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

import eu.ecodex.connector.common.service.ConfigurationPropertyManagerService;
import eu.ecodex.connector.controller.spring.ConnectorMessageProcessingProperties;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This processing step generates a EBMS id for the message
 * -) the EBMSID is only created if the EBMSID of the by the backend provided message is empty.
 */
@Component
public class GenerateEbmsIdStep implements MessageProcessStep {
    private static final Logger LOGGER = LogManager.getLogger(GenerateEbmsIdStep.class);
    private final ConfigurationPropertyManagerService configurationPropertyLoaderService;

    public GenerateEbmsIdStep(
        ConfigurationPropertyManagerService configurationPropertyLoaderService) {
        this.configurationPropertyLoaderService = configurationPropertyLoaderService;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
        value = "GenerateEbmsIdStep"
    )
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        ConnectorMessageProcessingProperties connectorMessageProcessingProperties =
            configurationPropertyLoaderService.loadConfiguration(
                domibusConnectorMessage.getMessageLaneId(),
                ConnectorMessageProcessingProperties.class
            );
        if (connectorMessageProcessingProperties.isEbmsIdGeneratorEnabled()) {
            LOGGER.debug("Setting EBMS id within connector is enabled");
            if (!StringUtils.hasLength(
                domibusConnectorMessage.getMessageDetails().getEbmsMessageId())) {
                String ebmsId = UUID.randomUUID().toString() + "@"
                    + connectorMessageProcessingProperties.getEbmsIdSuffix();
                domibusConnectorMessage.getMessageDetails().setEbmsMessageId(ebmsId);
                LOGGER.info("Setting EBMS id to [{}]", ebmsId);
            }
        }
        return true;
    }
}
