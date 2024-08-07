/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.service;

import eu.domibus.connector.common.annotations.ConnectorConversationService;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import eu.domibus.connector.utils.service.PropertyMapToBeanConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * This class is responsible for loading and managing configuration properties.
 */
@Service
public class ConfigurationPropertyLoaderServiceImpl implements ConfigurationPropertyManagerService {
    private static final Logger LOGGER =
        LogManager.getLogger(ConfigurationPropertyLoaderServiceImpl.class);
    public static final String PROPERTY_CLASS_LOADING_MESSAGE = "Loading property class [{}]";
    private final ApplicationContext ctx;
    private final ConversionService conversionService;
    private final DCBusinessDomainManager businessDomainManager;
    private final Validator validator;
    private final BeanToPropertyMapConverter beanToPropertyMapConverter;
    private final PropertyMapToBeanConverter propertyMapToBeanConverter;

    /**
     * This class is responsible for loading and managing configuration properties.
     */
    public ConfigurationPropertyLoaderServiceImpl(
        ApplicationContext ctx,
        @ConnectorConversationService ConversionService conversionService,
        DCBusinessDomainManager businessDomainManager,
        PropertyMapToBeanConverter propertyMapToBeanConverter,
        Validator validator,
        BeanToPropertyMapConverter beanToPropertyMapConverter) {
        this.ctx = ctx;
        this.conversionService = conversionService;
        this.businessDomainManager = businessDomainManager;
        this.validator = validator;
        this.propertyMapToBeanConverter = propertyMapToBeanConverter;
        this.beanToPropertyMapConverter = beanToPropertyMapConverter;
    }

    private String getPrefixFromAnnotation(Class<?> clazz) {
        if (!AnnotatedElementUtils.hasAnnotation(clazz, ConfigurationProperties.class)) {
            throw new IllegalArgumentException(
                "clazz must be annotated with " + ConfigurationProperties.class);
        }
        LOGGER.debug(PROPERTY_CLASS_LOADING_MESSAGE, clazz);

        ConfigurationProperties annotation = clazz.getAnnotation(ConfigurationProperties.class);
        return annotation.prefix();
    }

    /**
     * Binds a class to the configuration properties loaded from the message lane and the spring
     * environment.
     *
     * @param laneId - the lane id
     * @param clazz  - the clazz to init
     * @param prefix - the prefix for the properties
     * @param <T>    a class
     * @return the configuration object
     */
    public <T> T loadConfiguration(
        @Nullable DomibusConnectorBusinessDomain.BusinessDomainId laneId, Class<T> clazz,
        String prefix) {
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz is not allowed to be null!");
        }
        if (!StringUtils.hasLength(prefix)) {
            throw new IllegalArgumentException("Prefix is not allowed to be null!");
        }
        LOGGER.debug(PROPERTY_CLASS_LOADING_MESSAGE, clazz);

        MapConfigurationPropertySource messageLaneSource = loadLaneProperties(laneId);

        var environment = ctx.getEnvironment();
        Iterable<ConfigurationPropertySource> environmentSource =
            ConfigurationPropertySources.get(environment);
        List<ConfigurationPropertySource> configSources = new ArrayList<>();
        configSources.add(messageLaneSource);
        environmentSource.forEach(configSources::add);

        var placeholdersResolver = new PropertySourcesPlaceholdersResolver(environment);

        var binder = new Binder(configSources, placeholdersResolver, conversionService, null);

        Bindable<T> bindable = Bindable.of(clazz);

        return binder.bindOrCreate(prefix, bindable);
    }

    @Override
    public <T> T loadConfiguration(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId, Class<T> clazz) {
        String prefix = getPrefixFromAnnotation(clazz);

        return this.loadConfiguration(laneId, clazz, prefix);
    }

    @Override
    public <T> T loadConfigurationOnlyFromMap(
        Map<String, String> map, Class<T> clazz, String prefix) {
        return propertyMapToBeanConverter.loadConfigurationOnlyFromMap(map, clazz, prefix);
    }

    private MapConfigurationPropertySource loadLaneProperties(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId) {
        Optional<DomibusConnectorBusinessDomain> businessDomain =
            businessDomainManager.getBusinessDomain(laneId);
        if (businessDomain.isPresent()) {
            return new MapConfigurationPropertySource(
                businessDomain.get().getMessageLaneProperties()
            );
        } else {
            throw new IllegalArgumentException(
                    "No active business domain for id [%s]".formatted(laneId));
        }
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateConfiguration(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId, T updatedConfigClazz) {
        if (laneId == null) {
            throw new IllegalArgumentException("LaneId is not allowed to be null!");
        }
        return validator.validate(updatedConfigClazz);
    }

    /**
     * A {@link BusinessDomainConfigurationChange} event is fired with the changed properties and
     * affected BusinessDomain So factories, Scopes can react to this event and refresh the
     * settings.
     *
     * @param laneId             the laneId, if null defaultLaneId is used
     * @param updatedConfigClazz - the configurationClazz which has been altered, updated only the
     *                           changed properties are updated at the configuration source
     */
    @Override
    public void updateConfiguration(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId, Object updatedConfigClazz) {
        Map<String, String> diffProps = getUpdatedConfiguration(laneId, updatedConfigClazz);
        updateConfiguration(laneId, updatedConfigClazz.getClass(), diffProps);
    }

    @Override
    public void updateConfiguration(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId, Class<?> updatedConfigClazz,
        Map<String, String> diffProps) {
        LOGGER.debug(
            "Updating of [{}] the following properties [{}]", updatedConfigClazz, diffProps);

        businessDomainManager.updateConfig(laneId, diffProps);
        ctx.publishEvent(new BusinessDomainConfigurationChange(this, laneId, diffProps));
    }

    @Override
    public Map<String, String> getUpdatedConfiguration(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId, Object updatedConfigClazz) {
        if (laneId == null) {
            throw new IllegalArgumentException("LaneId is not allowed to be null!");
        }

        Object currentConfig = this.loadConfiguration(laneId, updatedConfigClazz.getClass());
        Map<String, String> previousProps =
            createPropertyMap(currentConfig); // collect current active properties
        Map<String, String> props =
            createPropertyMap(updatedConfigClazz); // collect updated properties

        // only collect differences
        Map<String, String> diffProps = new HashMap<>();
        props.entrySet().stream()
             .filter(entry -> !Objects.equals(previousProps.get(entry.getKey()), entry.getValue()))
             .forEach(e -> diffProps.put(e.getKey(), e.getValue()));
        return diffProps;
    }

    Map<String, String> createPropertyMap(Object configurationClazz) {
        String prefix = getPrefixFromAnnotation(configurationClazz.getClass());
        return beanToPropertyMapConverter.readBeanPropertiesToMap(configurationClazz, prefix);
    }
}
