package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.controller.routing.DCRoutingRulesManagerImpl;
import eu.domibus.connector.controller.routing.RoutingRule;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * The routing occurs in the following order:
 * <p>
 * 1. refToMessageId: If the transported message relates to an EBMS id of an already processed business message.
 * The backend_name of this message is used.
 * <p>
 * 2. ConversationId: If the transported message contains a conversationId where a business message has already been
 * processed. The backend_name of this message is used.
 * <p>
 * 3. Routing Rule: If the backend_name is still empty, the routing rules are processed.
 * <p>
 * 4. Default Backend: If the backend_name is still empty the default backend will be used.
 */
@Component
public class LookupBackendNameStep implements MessageProcessStep {
    private static final Logger LOGGER = LogManager.getLogger(LookupBackendNameStep.class);
    private final DCRoutingRulesManagerImpl dcRoutingConfigManager;
    private final DCMessagePersistenceService dcMessagePersistenceService;

    public LookupBackendNameStep(
            DCRoutingRulesManagerImpl dcMessageRoutingConfigurationProperties,
            DCMessagePersistenceService dcMessagePersistenceService,
            ConfigurationPropertyManagerService configurationPropertyLoaderService) {
        this.dcRoutingConfigManager = dcMessageRoutingConfigurationProperties;
        this.dcMessagePersistenceService = dcMessagePersistenceService;
    }

    @Override
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "LookupBackendNameStep")
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        if (!StringUtils.isEmpty(domibusConnectorMessage.getMessageDetails().getConnectorBackendClientName())) {
            // return when already set
            return true;
        }
        String backendName = null;

        // Lookup backend by conversation id
        String conversationId = domibusConnectorMessage.getMessageDetails().getConversationId();
        if (!StringUtils.isEmpty(conversationId)) {
            List<DomibusConnectorMessage> messagesByConversationId = dcMessagePersistenceService
                    .findMessagesByConversationId(conversationId);
            backendName = messagesByConversationId.stream()
                                                  .map(m -> m.getMessageDetails()
                                                             .getConnectorBackendClientName())
                                                  .filter(s -> !StringUtils.isEmpty(s))
                                                  .findAny().orElse(null);
        }
        if (backendName != null) {
            LOGGER.info(
                    LoggingMarker.Log4jMarker.BUSINESS_LOG,
                    "ConversationId [{}] is used to set backend to [{}]",
                    conversationId, backendName
            );
            domibusConnectorMessage.getMessageDetails().setConnectorBackendClientName(backendName);
            return true;
        }

        // lookup backend by rules and default backend
        String defaultBackendName = dcRoutingConfigManager
                .getDefaultBackendName(domibusConnectorMessage.getMessageLaneId());
        if (dcRoutingConfigManager.isBackendRoutingEnabled(domibusConnectorMessage.getMessageLaneId())) {
            LOGGER.debug("Backend routing is enabled");
            domibusConnectorMessage.getMessageDetails().setConnectorBackendClientName(
                    dcRoutingConfigManager.getBackendRoutingRules(domibusConnectorMessage.getMessageLaneId())
                                          .values()
                                          .stream()
                                          .sorted(RoutingRule.getComparator())
                                          .filter(r -> r.getMatchClause().matches(domibusConnectorMessage))
                                          .map(RoutingRule::getLinkName)
                                          .findFirst()
                                          .map(bName -> {
                                              LOGGER.info(
                                                      LoggingMarker.Log4jMarker.BUSINESS_LOG,
                                                      "Looked up backend name [{}] for message", bName
                                              );
                                              return bName;
                                          })
                                          .orElseGet(() -> {
                                              LOGGER.warn(
                                                      LoggingMarker.Log4jMarker.BUSINESS_LOG,
                                                      "No backend rule pattern has matched! Applying default backend " +
                                                              "name [{}]!"
                                              );
                                              return defaultBackendName;
                                          })
            );
        } else {
            LOGGER.debug(
                    "Backend routing is disabled, applying default backend name [{}]!",
                    dcRoutingConfigManager.getDefaultBackendName(domibusConnectorMessage.getMessageLaneId())
            );
            domibusConnectorMessage.getMessageDetails().setConnectorBackendClientName(defaultBackendName);
        }
        return true;
    }
}
