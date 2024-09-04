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
import eu.ecodex.connector.security.DomibusConnectorSecurityToolkit;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class represents a component that builds an e-Codex container using
 * the DomibusConnectorSecurityToolkit.
 */
@Component
public class BuildEcodexContainerStep implements MessageProcessStep {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildEcodexContainerStep.class);
    private final DomibusConnectorSecurityToolkit securityToolkit;

    public BuildEcodexContainerStep(DomibusConnectorSecurityToolkit securityToolkit) {
        this.securityToolkit = securityToolkit;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
        value = "BuildECodexContainerStep"
    )
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        securityToolkit.buildContainer(domibusConnectorMessage);
        LOGGER.info(LoggingMarker.BUSINESS_LOG, "Successfully crated e-Codex Container");
        return true;
    }
}
