package eu.domibus.connector.c2ctests.config;

import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * This configuration properties are for defining the connector to connector
 * test action and service
 *
 * If the connector receives a message with the configured service and action
 * the message will not be delivered to the gateway
 *
 *
 */
@ConfigurationProperties(prefix=ConnectorTestConfigurationProperties.PREFIX)
@Component
public class ConnectorTestConfigurationProperties {

    public static final String PREFIX = "c2ctests";

    private boolean enabled = true;

    /**
     * should a delivery evidence sent
     */
    private boolean respondWithDeliveryEvidence = true;

    @NotNull
    private Resource defaultBusinessXml = new ClassPathResource("/eu/domibus/connector/config/c2ctests/testbusinessxml.xml");

    @NotNull
    private Resource defaultBusinessPdf = new ClassPathResource("/eu/domibus/connector/config/c2ctests/testbusinesspdf.pdf");;

    @Valid
    private EvidenceActionServiceConfigurationProperties.AS4Service service = new EvidenceActionServiceConfigurationProperties.AS4Service("Connector-TEST", "urn:e-codex:services:");

    @Valid
    @NotNull
    private EvidenceActionServiceConfigurationProperties.AS4Action action = new EvidenceActionServiceConfigurationProperties.AS4Action("ConTest_Form");

    public EvidenceActionServiceConfigurationProperties.AS4Service getService() {
        return service;
    }

    public void setService(EvidenceActionServiceConfigurationProperties.AS4Service service) {
        this.service = service;
    }

    public EvidenceActionServiceConfigurationProperties.AS4Action getAction() {
        return action;
    }

    public void setAction(EvidenceActionServiceConfigurationProperties.AS4Action action) {
        this.action = action;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRespondWithDeliveryEvidence() {
        return respondWithDeliveryEvidence;
    }

    public void setRespondWithDeliveryEvidence(boolean respondWithDeliveryEvidence) {
        this.respondWithDeliveryEvidence = respondWithDeliveryEvidence;
    }

    public Resource getDefaultBusinessXml() {
        return defaultBusinessXml;
    }

    public void setDefaultBusinessXml(Resource defaultBusinessXml) {
        this.defaultBusinessXml = defaultBusinessXml;
    }

    public Resource getDefaultBusinessPdf() {
        return defaultBusinessPdf;
    }

    public void setDefaultBusinessPdf(Resource defaultBusinessPdf) {
        this.defaultBusinessPdf = defaultBusinessPdf;
    }
}
