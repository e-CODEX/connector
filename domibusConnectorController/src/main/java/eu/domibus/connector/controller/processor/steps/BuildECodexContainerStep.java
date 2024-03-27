package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.security.DomibusConnectorSecurityToolkit;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class BuildECodexContainerStep implements MessageProcessStep {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildECodexContainerStep.class);
    private final DomibusConnectorSecurityToolkit securityToolkit;

    public BuildECodexContainerStep(DomibusConnectorSecurityToolkit securityToolkit) {
        this.securityToolkit = securityToolkit;
    }

    @Override
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "BuildECodexContainerStep")
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        securityToolkit.buildContainer(domibusConnectorMessage);
        LOGGER.info(LoggingMarker.BUSINESS_LOG, "Successfully crated e-Codex Container");
        return true;
    }
}
