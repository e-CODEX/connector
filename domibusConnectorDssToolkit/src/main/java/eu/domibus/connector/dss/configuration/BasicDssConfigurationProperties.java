/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.dss.configuration;

import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    private Path tlCacheLocation = Paths.get("./tlcache");

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
