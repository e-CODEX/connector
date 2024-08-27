/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
