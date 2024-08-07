/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.configuration;

import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The ConnectorConfigurationProperties class represents the configuration properties for the
 * connector module. It contains properties related to the connector's behavior, such as instance
 * name, stage configuration, business domains, and allowed configuration sources.
 */
@Validated
@Valid
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = ConnectorConfigurationProperties.PREFIX)
public class ConnectorConfigurationProperties {
    public static final String PREFIX = "connector";
    @NotNull
    private ConfigurationSource[] allowedConfigurationSources =
        {ConfigurationSource.DB, ConfigurationSource.ENV};
    /**
     * A random instance name is the default.
     */
    @NotNull
    @NotBlank
    private String instanceName = UUID.randomUUID().toString().substring(0, 6);
    @NotNull
    @Valid
    private StageConfigurationProperties stage = new StageConfigurationProperties();
    @NotNull
    DomibusConnectorBusinessDomain.BusinessDomainId defaultBusinessDomainId =
        DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
    /**
     * Should the business domains be loaded from Database.
     *
     *  <p>{@link eu.domibus.connector.common.service.DCBusinessDomainManager}
     */
    boolean loadBusinessDomainsFromDb = true;
    @NotNull
    Map<DomibusConnectorBusinessDomain.BusinessDomainId, @Valid BusinessDomainConfig>
        businessDomain = new HashMap<>();

    /**
     * Represents the configuration for a business domain in Domibus.
     *
     * <p>A business domain is a logical grouping of messaging configurations and properties.
     *
     * <p>The BusinessDomainConfig class has the following properties:
     * - enabled: A flag indicating whether the business domain is enabled or not.
     * - description: A description of the business domain.
     * - properties: A map of additional properties for the business domain.
     */
    @Validated
    @Data
    public static class BusinessDomainConfig {
        @NotNull
        private boolean enabled = true;
        private String description;
        private Map<String, String> properties = new HashMap<>();
    }
}
