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

import eu.ecodex.connector.controller.routing.DCMessageRoutingConfigurationProperties;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This step looks up the correct backend name.
 */
@Component
public class LookupGatewayNameStep implements MessageProcessStep {
    private final DCMessageRoutingConfigurationProperties dcMessageRoutingConfigurationProperties;

    public LookupGatewayNameStep(
        DCMessageRoutingConfigurationProperties dcMessageRoutingConfigurationProperties) {
        this.dcMessageRoutingConfigurationProperties = dcMessageRoutingConfigurationProperties;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
        value = "LookupGatewayNameStep"
    )
    public boolean executeStep(DomibusConnectorMessage message) {
        if (StringUtils.hasLength(message.getMessageDetails().getGatewayName())) {
            // return when already set
            return true;
        }
        message.getMessageDetails()
            .setGatewayName(dcMessageRoutingConfigurationProperties.getDefaultGatewayName());
        return true;
    }
}
