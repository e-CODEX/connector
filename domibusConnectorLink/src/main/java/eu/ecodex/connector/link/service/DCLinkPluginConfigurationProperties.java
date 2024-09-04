/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.service;

import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * The DCLinkPluginConfigurationProperties class represents the configuration properties for the
 * DCLink Plugin in Domibus.
 *
 * <p>The configuration properties include: - autostart: Determines if the links should be
 * autostarted on connector start. Default value is true. - loadDbConfig: Switch if link config
 * should be read from database. - loadEnvConfig: Switch if link config should be read from spring
 * environment. - failOnLinkPluginError: Determines if the connector should start even if any link
 * plugin init fails. Default value is false. - gateway: Represents the gateway link configuration.
 * - backend: Represents a list of backend link configurations.
 *
 * <p>The DCLnkPropertyConfig class represents a link configuration and its associated link
 * partners.
 *
 * <p>The DomibusConnectorLinkConfiguration class represents the configuration for a link connector
 * in Domibus. It includes properties like configName, linkImpl, properties, and
 * configurationSource.
 *
 * <p>The DomibusConnectorLinkPartner class represents a link partner in the Domibus Connector. It
 * includes properties like linkPartnerName, description, enabled, rcvLinkMode, sendLinkMode,
 * linkType, pullInterval, properties, linkConfiguration, and configurationSource.
 *
 * @see DomibusConnectorLinkConfiguration
 * @see DomibusConnectorLinkPartner
 */
@ConfigurationProperties(prefix = DCLinkPluginConfigurationProperties.PREFIX)
@Data
public class DCLinkPluginConfigurationProperties {
    public static final String PREFIX = "connector.link";
    @ConfigurationLabel("Link Autostart")
    @ConfigurationDescription(
        "Should the links be autostarted on connector start?, default is true"
    )
    private boolean autostart = true;
    /**
     * Switch if link config should be read from database.
     */
    private boolean loadDbConfig = true;
    /**
     * Switch if link config should be read from spring environment.
     */
    private boolean loadEnvConfig = true;
    /**
     * Start the connector if any link plugin init fails, by default start connector always.
     */
    private boolean failOnLinkPluginError = false;
    /**
     * If set the gateway link configuration is loaded from here.
     */
    @NestedConfigurationProperty
    @Valid
    private DCLnkPropertyConfig gateway = null;
    @NestedConfigurationProperty
    private List<@Valid DCLnkPropertyConfig> backend = new ArrayList<>();

    /**
     * The DCLnkPropertyConfig class represents a link configuration and its associated link
     * partners.
     */
    @Data
    public static class DCLnkPropertyConfig {
        @Valid
        @NestedConfigurationProperty
        private DomibusConnectorLinkConfiguration linkConfig;
        @Valid
        @NestedConfigurationProperty
        private List<DomibusConnectorLinkPartner> linkPartners = new ArrayList<>();
    }
}
