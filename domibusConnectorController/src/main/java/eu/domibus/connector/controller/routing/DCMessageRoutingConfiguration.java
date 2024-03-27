package eu.domibus.connector.controller.routing;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;


/*
@ConditionalOnProperty(prefix = DCMessageRoutingConfigurationProperties.ROUTING_CONFIG_PREFIX,
name = "enabled", havingValue = "true", matchIfMissing = true)
*/
@Configuration
@EnableConfigurationProperties(DCMessageRoutingConfigurationProperties.class)
public class DCMessageRoutingConfiguration {
    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, RoutingRulePattern> routingRulePatternConverter() {
        return new RoutingRulePatternConverter();
    }
}
