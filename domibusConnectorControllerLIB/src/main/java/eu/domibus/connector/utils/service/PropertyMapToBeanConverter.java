package eu.domibus.connector.utils.service;

import eu.domibus.connector.common.annotations.ConnectorConversationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class PropertyMapToBeanConverter {
    private static final Logger LOGGER = LogManager.getLogger(PropertyMapToBeanConverter.class);

    private final ConversionService conversionService;

    public PropertyMapToBeanConverter(@ConnectorConversationService ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public <T> T loadConfigurationOnlyFromMap(Map<String, String> map, Class<T> clazz, String prefix) {
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz is not allowed to be null!");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix is not allowed to be null!");
        }
        LOGGER.debug("Loading property class [{}]", clazz);

        MapConfigurationPropertySource mapConfigurationPropertySource = new MapConfigurationPropertySource(map);

        List<ConfigurationPropertySource> configSources = new ArrayList<>();
        configSources.add(mapConfigurationPropertySource);

        Binder binder = new Binder(configSources, null, conversionService, null);

        Bindable<T> bindable = Bindable.of(clazz);
        T t = binder.bindOrCreate(prefix, bindable);

        return t;
    }
}
