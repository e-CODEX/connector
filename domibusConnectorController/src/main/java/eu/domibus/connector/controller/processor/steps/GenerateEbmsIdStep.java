package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.controller.spring.ConnectorMessageProcessingProperties;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;


/**
 * This processing step generates a EBMS id for the message
 * -) the EBMSID is only created if the EBMSID of the by the backend provided message is empty
 */
@Component
public class GenerateEbmsIdStep implements MessageProcessStep {
    private static final Logger LOGGER = LogManager.getLogger(GenerateEbmsIdStep.class);
    private final ConfigurationPropertyManagerService configurationPropertyLoaderService;

    public GenerateEbmsIdStep(ConfigurationPropertyManagerService configurationPropertyLoaderService) {
        this.configurationPropertyLoaderService = configurationPropertyLoaderService;
    }

    @Override
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "GenerateEbmsIdStep")
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        ConnectorMessageProcessingProperties connectorMessageProcessingProperties = configurationPropertyLoaderService
                .loadConfiguration(
                        domibusConnectorMessage.getMessageLaneId(), ConnectorMessageProcessingProperties.class
                );
        if (connectorMessageProcessingProperties.isEbmsIdGeneratorEnabled()) {
            LOGGER.debug("Setting EBMS id within connector is enabled");
            if (StringUtils.isEmpty(domibusConnectorMessage.getMessageDetails().getEbmsMessageId())) {
                String ebmsId = UUID.randomUUID() + "@" + connectorMessageProcessingProperties
                        .getEbmsIdSuffix();
                domibusConnectorMessage.getMessageDetails().setEbmsMessageId(ebmsId);
                LOGGER.info("Setting EBMS id to [{}]", ebmsId);
            }
        }
        return true;
    }
}
