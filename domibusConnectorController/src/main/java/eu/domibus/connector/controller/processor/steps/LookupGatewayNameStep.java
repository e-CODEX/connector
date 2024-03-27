package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.controller.routing.DCMessageRoutingConfigurationProperties;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * This step looks up the correct backend name
 */
@Component
public class LookupGatewayNameStep implements MessageProcessStep {
    private final DCMessageRoutingConfigurationProperties dcMessageRoutingConfigurationProperties;

    public LookupGatewayNameStep(DCMessageRoutingConfigurationProperties dcMessageRoutingConfigurationProperties) {
        this.dcMessageRoutingConfigurationProperties = dcMessageRoutingConfigurationProperties;
    }

    @Override
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "LookupGatewayNameStep")
    public boolean executeStep(DomibusConnectorMessage message) {
        if (!StringUtils.isEmpty(message.getMessageDetails().getGatewayName())) {
            // return when already set
            return true;
        }
        message.getMessageDetails().setGatewayName(dcMessageRoutingConfigurationProperties.getDefaultGatewayName());
        return true;
    }
}
