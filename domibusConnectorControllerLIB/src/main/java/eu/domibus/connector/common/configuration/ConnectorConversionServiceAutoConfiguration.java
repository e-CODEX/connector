package eu.domibus.connector.common.configuration;

import eu.domibus.connector.common.annotations.ConnectorConversationService;
import eu.domibus.connector.common.annotations.ConnectorPropertyConverter;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Configuration
public class ConnectorConversionServiceAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorConversionServiceAutoConfiguration.class);

    @Autowired(required = false)
    @Qualifier(ConfigurationPropertiesBinding.VALUE)
    private Set<Converter<?, ?>> converters = new HashSet<>();

    @Autowired(required = false)
    @Qualifier(ConnectorPropertyConverter.VALUE)
    private Set<Converter<?, ?>> connectorConverters = new HashSet<>();

    @Bean
    @ConnectorConversationService
    public ConversionService connectorConversionService() {
        LOGGER.debug(
                LoggingMarker.CONFIG,
                "Creating connectorConversionService with connectorConverters [{}] and " +
                        "springConfigurationConverters [{}]",
                connectorConverters,
                converters
        );
        Set<Converter<?, ?>> mergedConverters = Stream
                .of(connectorConverters.stream(), converters.stream())
                .flatMap(Function.identity())
                .distinct()
                .collect(Collectors.toSet());

        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters(mergedConverters);
        bean.afterPropertiesSet();
        ConversionService object = bean.getObject();
        return object;
    }
}

