package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.security.DomibusConnectorSecurityToolkit;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


@Component
public class ResolveECodexContainerStep implements MessageProcessStep {
    private static final Logger LOGGER = LogManager.getLogger(ResolveECodexContainerStep.class);
    private final DomibusConnectorSecurityToolkit securityToolkit;

    public ResolveECodexContainerStep(DomibusConnectorSecurityToolkit securityToolkit) {
        this.securityToolkit = securityToolkit;
    }

    @Override
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "ResolveECodexContainerStep")
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        securityToolkit.validateContainer(domibusConnectorMessage);
        LOGGER.info(LoggingMarker.Log4jMarker.BUSINESS_LOG, "Successfully resolved eCodexContainer");
        return true;
    }
}
