/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.utils.field;

import com.vaadin.flow.component.customfield.CustomField;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

/**
 * A service class that finds a custom field bean based on the provided class.
 */
@Service
public class FindFieldService {
    private final ApplicationContext applicationContext;

    public FindFieldService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Finds a custom field bean based on the provided class.
     *
     * @param clz The class of the custom field.
     * @param <T> The type of the custom field.
     * @return The custom field bean.
     * @throws IllegalArgumentException If no field is found for the provided class.
     */
    public <T> CustomField<T> findField(Class<T> clz) {
        var resolvableType = ResolvableType.forClassWithGenerics(CustomField.class, clz);
        var beanNamesForType = applicationContext.getBeanNamesForType(resolvableType);
        if (beanNamesForType.length == 0) {
            throw new IllegalArgumentException("No field found for type " + clz);
        }
        return (CustomField<T>) applicationContext.getBean(beanNamesForType[0]);
    }
}
