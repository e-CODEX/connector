package eu.domibus.connector.security.aes;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;


public interface DCAuthenticationBasedTechnicalValidationServiceFactory {
    ECodexTechnicalValidationService createTechnicalValidationService(
            DomibusConnectorMessage message,
            DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties config);
}
