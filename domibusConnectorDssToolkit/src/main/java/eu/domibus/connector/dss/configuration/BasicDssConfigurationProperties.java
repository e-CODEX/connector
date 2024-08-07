/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.dss.configuration;

import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the BasicDss connector.
 */
@ConfigurationProperties(prefix = BasicDssConfigurationProperties.PREFIX)
@Validated
@Getter
@Setter
public class BasicDssConfigurationProperties {
    public static final String PREFIX = "connector.dss";
    @NestedConfigurationProperty
    private ProxyProperties httpsProxy;
    @NestedConfigurationProperty
    private ProxyProperties httpProxy;
    private Map<String, @Valid Tsp> timeStampServers = new HashMap<>();
    private Map<String, @Valid TrustListSourceConfigurationProperties> trustListSources =
        new HashMap<>();
    @NotNull
    private Duration tlCacheExpiration = Duration.ofDays(1);
    @NotNull
    private Path tlCacheLocation = Path.of("./tlcache");

    /**
     * This class represents a TSP (Time Stamping Protocol) configuration.
     */
    @Valid
    @Getter
    @Setter
    public static class Tsp {
        //@Pattern(regexp = "^(https|http):\\/\\/", message = "Only http or https urls are allowed")
        @NotBlank
        private String url;
        private String policyOid;
    }
}
