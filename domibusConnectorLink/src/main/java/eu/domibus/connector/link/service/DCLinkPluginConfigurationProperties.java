package eu.domibus.connector.link.service;


import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@ConfigurationProperties(prefix = DCLinkPluginConfigurationProperties.PREFIX)
@Data
public class DCLinkPluginConfigurationProperties {

    public static final String PREFIX = "connector.link";

    @ConfigurationLabel("Link Autostart")
    @ConfigurationDescription("Should the links be autostarted on connector start?, default is true")
    private boolean autostart = true;

    /**
     * switch if link config should be read from database
     */
    private boolean loadDbConfig = true;


    /**
     * switch if link config should be read from spring environment
     */
    private boolean loadEnvConfig = true;

    /**
     * start the connector if any link plugin init
     * fails, by default start connector always
     */
    private boolean failOnLinkPluginError = false;


    /**
     * If set the gateway link configuration is loaded from here
     */
    @NestedConfigurationProperty
    @Valid
    private DCLnkPropertyConfig gateway = null;

    @NestedConfigurationProperty
    private List<@Valid DCLnkPropertyConfig> backend = new ArrayList<>();

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
