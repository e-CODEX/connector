package eu.domibus.connector.controller.routing;

import eu.domibus.connector.common.DomibusConnectorDefaults;
import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BusinessDomainScoped
//@Component
@ConfigurationProperties(prefix = DCMessageRoutingConfigurationProperties.ROUTING_CONFIG_PREFIX)
public class DCMessageRoutingConfigurationProperties {

    public static final String ROUTING_CONFIG_PREFIX = "connector.routing";

    private boolean enabled = true;

    private Map<String, RoutingRule> backendRules = new HashMap<>();

    private Map<String, RoutingRule> gatewayRules = new HashMap<>();

    @NotBlank
    private String defaultBackendName = DomibusConnectorDefaults.DEFAULT_BACKEND_NAME;

    @NotBlank
    private String defaultGatewayName = DomibusConnectorDefaults.DEFAULT_GATEWAY_NAME;

    /**
     * Backend name of the connector itself,
     * is used for connector2connector tests,
     * when the connector itself acts as a backend
     *
     */
    @NotBlank
    private String connectorBackendName = "connectorBackend";

    /**
     * Gateway name of the connector itself,
     * is used for backend2backend tests,
     * when the connector itself acts as a gateway
     *
     *
     *
     * NOT IMPLEMENTED YET!
     */
    @NotBlank
    private String connectorGatewayName = "connectorGateway";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, RoutingRule> getBackendRules() {
        return backendRules;
    }

    public void setBackendRules(Map<String, RoutingRule> backendRules) {
        this.backendRules = backendRules;
    }

    public Map<String, RoutingRule> getGatewayRules() {
        return gatewayRules;
    }

    public void setGatewayRules(Map<String, RoutingRule> gatewayRules) {
        this.gatewayRules = gatewayRules;
    }

    public String getDefaultBackendName() {
        return defaultBackendName;
    }

    public void setDefaultBackendName(String defaultBackendName) {
        this.defaultBackendName = defaultBackendName;
    }

    public String getDefaultGatewayName() {
        return defaultGatewayName;
    }

    public void setDefaultGatewayName(String defaultGatewayName) {
        this.defaultGatewayName = defaultGatewayName;
    }

    public String getConnectorBackendName() {
        return connectorBackendName;
    }

    public void setConnectorBackendName(String connectorBackendName) {
        this.connectorBackendName = connectorBackendName;
    }

    public String getConnectorGatewayName() {
        return connectorGatewayName;
    }

    public void setConnectorGatewayName(String connectorGatewayName) {
        this.connectorGatewayName = connectorGatewayName;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        //align routing rule id to key
        backendRules.forEach((key, value) -> value.setRoutingRuleId(key));
        gatewayRules.forEach((key, value) -> value.setRoutingRuleId(key));
    }

}
