/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.routing;

import eu.ecodex.connector.common.DomibusConnectorDefaults;
import eu.ecodex.connector.common.annotations.BusinessDomainScoped;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class represents the configuration properties for DCMessageRouting. It contains properties
 * related to message routing configuration.
 */
@Data
@BusinessDomainScoped
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
     * Backend name of the connector itself is used for connector2connector tests, when the
     * connector itself acts as a backend.
     */
    @NotBlank
    private String connectorBackendName = "connectorBackend";
    /**
     * Gateway name of the connector itself, is used for backend2backend tests, when the connector
     * itself acts as a gateway. NOT IMPLEMENTED YET!
     */
    @NotBlank
    private String connectorGatewayName = "connectorGateway";

    /**
     * This method is called after all the bean properties have been set by Spring. It aligns the
     * routing rule id to the key for both backendRules and gatewayRules. The routing rule id is set
     * to the key value.
     */
    @PostConstruct
    public void afterPropertiesSet() {
        // align routing rule id to key
        backendRules.forEach((key, value) -> value.setRoutingRuleId(key));
        gatewayRules.forEach((key, value) -> value.setRoutingRuleId(key));
    }
}
