package eu.domibus.connector.link.service;


import eu.domibus.connectorplugins.link.gwwspullplugin.DCGatewayPullPlugin;
import eu.domibus.connectorplugins.link.gwwspushplugin.WsGatewayPlugin;
import eu.domibus.connectorplugins.link.wsbackendplugin.WsBackendPlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.Validator;


@Configuration
@EnableConfigurationProperties(DCLinkPluginConfigurationProperties.class)
@Import(DomibusConnectorLinkCreatorConfigurationService.class)
public class DCLinkPluginConfiguration {
    public static final String LINK_PLUGIN_PROFILE_NAME = "linkplugins";

    @Bean
    @ConditionalOnProperty(
            prefix = DCLinkPluginConfigurationProperties.PREFIX + ".plugin-" + DCGatewayPullPlugin.IMPL_NAME,
            value = "enabled", havingValue = "true", matchIfMissing = true
    )
    public DCGatewayPullPlugin dcGatewayPullPlugin() {
        return new DCGatewayPullPlugin();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = DCLinkPluginConfigurationProperties.PREFIX + ".plugin-" + WsGatewayPlugin.IMPL_NAME,
            value = "enabled", havingValue = "true", matchIfMissing = true
    )
    public WsGatewayPlugin wsGatewayPlugin() {
        return new WsGatewayPlugin();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = DCLinkPluginConfigurationProperties.PREFIX + ".plugin-" + WsBackendPlugin.IMPL_NAME,
            value = "enabled", havingValue = "true", matchIfMissing = true
    )
    public WsBackendPlugin wsBackendPlugin(ConfigurableApplicationContext applicationContext, Validator validator) {
        return new WsBackendPlugin(applicationContext, validator);
    }
}
