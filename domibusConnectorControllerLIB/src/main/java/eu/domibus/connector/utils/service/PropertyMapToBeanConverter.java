/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.utils.service;

import eu.domibus.connector.common.annotations.ConnectorConversationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

/**
 * The PropertyMapToBeanConverter class provides methods to convert a map of properties to a Java
 * bean object.
 */
@Component
public class PropertyMapToBeanConverter {
    private static final Logger LOGGER = LogManager.getLogger(PropertyMapToBeanConverter.class);
    private final ConversionService conversionService;

    public PropertyMapToBeanConverter(
        @ConnectorConversationService ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Loads a configuration object of type T only from a map of properties.
     *
     * @param map    The map of properties.
     * @param clazz  The class of the configuration object.
     * @param prefix The prefix for the properties.
     * @param <T>    The type of the configuration object.
     * @return The loaded configuration object of type T.
     * @throws IllegalArgumentException If clazz or prefix is null.
     */
    public <T> T loadConfigurationOnlyFromMap(Map<String, String> map, Class<T> clazz,
                                              String prefix) {
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz is not allowed to be null!");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix is not allowed to be null!");
        }
        LOGGER.debug("Loading property class [{}]", clazz);

        var mapConfigurationPropertySource = new MapConfigurationPropertySource(map);

        List<ConfigurationPropertySource> configSources = new ArrayList<>();
        configSources.add(mapConfigurationPropertySource);

        var binder = new Binder(configSources, null, conversionService, null);

        Bindable<T> bindable = Bindable.of(clazz);
        return binder.bindOrCreate(prefix, bindable);
    }
}
