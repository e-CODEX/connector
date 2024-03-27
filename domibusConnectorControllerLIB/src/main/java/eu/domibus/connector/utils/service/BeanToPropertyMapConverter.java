package eu.domibus.connector.utils.service;

import com.google.common.base.CaseFormat;
import eu.domibus.connector.common.annotations.ConnectorConversationService;
import eu.domibus.connector.common.annotations.MapNested;
import eu.domibus.connector.common.annotations.UseConverter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Converts a Bean to a map of String properties
 * in a way so the properties can be loaded again
 * into the Bean as configuration properties
 */
@Component
public class BeanToPropertyMapConverter {
    private static final Logger LOGGER = LogManager.getLogger(BeanToPropertyMapConverter.class);

    private final ConversionService conversionService;

    public BeanToPropertyMapConverter(@ConnectorConversationService ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Map<String, String> readBeanPropertiesToMap(Object configurationBean, String prefix) {
        ToPropertyConverter converter = new ToPropertyConverter();

        ConfigurationPropertyName configurationPropertyName = ConfigurationPropertyName.of(prefix);
        converter.convertToProperties(configurationBean, configurationPropertyName);
        return converter.getProperties();
    }

    private class ToPropertyConverter {
        HashMap<String, String> properties = new HashMap<>();

        void convertToProperties(Object bean, ConfigurationPropertyName prefix) {
            convertToProperties(bean, prefix, true, false);
        }

        void convertToProperties(Object bean, ConfigurationPropertyName prefix, boolean nested, boolean useConverter) {
            if (bean == null) {
                return; // do nothing if null
            }
            Class<?> beanType = bean.getClass();

            nested = nested || beanType.getAnnotation(MapNested.class) != null;
            if (useConverter) {
                LOGGER.debug("use converter is true, so using conversion service directly to convert bean to string");
                properties.put(prefix.toString(), conversionService.convert(bean, String.class));
            } else if (Collection.class.isAssignableFrom(bean.getClass())) {
                // is collection
                convertCollectionToProperties((Collection<?>) bean, prefix, nested, false);
            } else if (Map.class.isAssignableFrom(bean.getClass())) {
                convertMapToProperties((Map<?, ?>) bean, prefix, nested, false);
            } else if (!nested && conversionService.canConvert(beanType, String.class)) {
                properties.put(prefix.toString(), conversionService.convert(bean, String.class));
            } else if (!nested) {
                // map basic types?
                properties.put(prefix.toString(), bean.toString());
            } else {
                // nested bean...
                convertNestedToProperties(bean, prefix);
            }
        }

        private void convertNestedToProperties(Object bean, ConfigurationPropertyName prefix) {
            Class<?> clazz = bean.getClass();

            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(bean);

            for (PropertyDescriptor pd : propertyDescriptors) {
                try {
                    boolean nested = false;
                    boolean useConverter = false;
                    String propName = pd.getName();

                    Field declaredField = FieldUtils.getField(clazz, propName, true);
                    if (declaredField != null) {
                        useConverter = declaredField.getAnnotation(UseConverter.class) != null;
                        NestedConfigurationProperty annotation = declaredField
                                .getAnnotation(NestedConfigurationProperty.class);
                        nested = annotation != null;

                        AnnotatedType annotatedType = declaredField.getAnnotatedType();
                        if (annotatedType instanceof AnnotatedParameterizedType) {
                            AnnotatedParameterizedType apt = (AnnotatedParameterizedType) annotatedType;
                            AnnotatedType[] annotatedActualTypeArguments = apt.getAnnotatedActualTypeArguments();
                            for (AnnotatedType at : annotatedActualTypeArguments) {
                                nested = nested || at.getAnnotation(MapNested.class) != null;
                            }
                        }
                    } else {
                        // declared field is null?
                        continue;
                    }

                    Object property = PropertyUtils.getProperty(bean, propName);

                    String p = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, propName);

                    ConfigurationPropertyName newPrefix = prefix.append(p);
                    convertToProperties(property, newPrefix, nested, useConverter);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    LOGGER.trace("Exception", e);
                }
            }
        }

        private void convertCollectionToProperties(
                Collection<?> collection,
                ConfigurationPropertyName prefix,
                boolean nested,
                boolean useConverter) {
            int i = 0;
            for (Object o : collection) {
                convertToProperties(o, prefix.append("[" + i + "]"), nested, useConverter);
                i++;
            }
        }

        private void convertMapToProperties(
                Map<?, ?> map,
                ConfigurationPropertyName prefix,
                boolean nested,
                boolean useConverter) {
            map.forEach((key, value) -> {
                convertToProperties(value, prefix.append("[" + key + "]"), nested, useConverter);
            });
        }

        public HashMap<String, String> getProperties() {
            return properties;
        }
    }
}
