/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.security.DomibusConnectorSecurityToolkit;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * The ResolveECodexContainerStep class is a {@link MessageProcessStep} implementation that
 * is responsible for resolving the eCodex container of a {@link DomibusConnectorMessage}.
 * It uses an instance of {@link DomibusConnectorSecurityToolkit}
 * to validate the container.
 */
@Component
public class ResolveEcodexContainerStep implements MessageProcessStep {
    private static final Logger LOGGER = LogManager.getLogger(ResolveEcodexContainerStep.class);
    private final DomibusConnectorSecurityToolkit securityToolkit;

    public ResolveEcodexContainerStep(DomibusConnectorSecurityToolkit securityToolkit) {
        this.securityToolkit = securityToolkit;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
        value = "ResolveECodexContainerStep"
    )
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        securityToolkit.validateContainer(domibusConnectorMessage);
        LOGGER.info(
            LoggingMarker.Log4jMarker.BUSINESS_LOG, "Successfully resolved eCodexContainer");
        return true;
    }
}
