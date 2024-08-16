/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.utils.binder;

import com.vaadin.flow.data.binder.BeanPropertySet;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.RequiredFieldConfigurator;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;
import lombok.Data;

/**
 * The {@code SpringBeanValidationBinder} class is a subclass of {@code Binder} that provides
 * functionality to bind and validate form fields in a Spring framework application using
 * Bean Validation (JSR-303).
 *
 * <p>This class configures a {@link SpringBeanValidator} for each binding defined using a
 * property name. The binder uses reflection based on the provided bean type to resolve bean
 * properties. It assumes that the {@code javaxValidator} is initialized by the Spring Framework.
 *
 * <p>The binder can be used to configure required fields and add validators to form fields.
 *
 * @param <BEAN> the type of the bean being bound
 */
@Data
@SuppressWarnings({"squid:S1135", "checkstyle:ClassTypeParameterName"})
public class SpringBeanValidationBinder<BEAN> extends Binder<BEAN> {
    // TODO: create spring factory...
    private final Class<BEAN> beanType;
    private final Validator javaxValidator;
    private RequiredFieldConfigurator requiredConfigurator = RequiredFieldConfigurator.DEFAULT;

    /**
     * Creates a new binder that uses reflection based on the provided bean type to resolve bean
     * properties. It assumes that the javaxValidator is initialized by SpringFramework
     *
     * <p>A {@link SpringBeanValidator} is added to each binding that is defined using a property
     * name.
     *
     * @param beanType              the bean type to use, not {@code null}
     * @param scanNestedDefinitions if {@code true}, scan for nested property definitions as well
     */
    public SpringBeanValidationBinder(
        javax.validation.Validator javaxValidator, Class<BEAN> beanType,
        boolean scanNestedDefinitions) {
        super(beanType, scanNestedDefinitions);
        this.javaxValidator = javaxValidator;
        this.beanType = beanType;
    }

    @Override
    protected BindingBuilder<BEAN, ?> configureBinding(
        BindingBuilder<BEAN, ?> binding,
        PropertyDefinition<BEAN, ?> definition) {
        Class<?> actualBeanType = findBeanType(beanType, definition);
        // TODO: replace...
        var validator = new SpringBeanValidator(
            javaxValidator, actualBeanType,
            definition.getTopLevelName()
        );

        if (requiredConfigurator != null) {
            configureRequired(binding, definition, validator);
        }
        return binding.withValidator(validator);
    }

    /**
     * Finds the bean type containing the property the given definition refers to.
     *
     * @param beanType   the root beanType
     * @param definition the definition for the property
     * @return the bean type containing the given property
     */
    @SuppressWarnings({"rawtypes"})
    private Class<?> findBeanType(
        Class<BEAN> beanType,
        PropertyDefinition<BEAN, ?> definition) {
        if (definition instanceof BeanPropertySet.NestedBeanPropertyDefinition propertyDefinition) {
            return propertyDefinition.getParent().getType();
        } else {
            // Non nested properties must be defined in the main type
            // TODO see why this section is empty
            return beanType;
        }
    }

    private void configureRequired(
        BindingBuilder<BEAN, ?> binding,
        PropertyDefinition<BEAN, ?> definition, SpringBeanValidator validator) {
        assert requiredConfigurator != null;
        var propertyHolderType = definition.getPropertyHolderType();
        var descriptor =
            validator.getJavaxBeanValidator().getConstraintsForClass(propertyHolderType);
        var propertyDescriptor = descriptor.getConstraintsForProperty(definition.getTopLevelName());
        if (propertyDescriptor == null) {
            return;
        }
        if (propertyDescriptor.getConstraintDescriptors()
                              .stream()
                              .map(ConstraintDescriptor::getAnnotation)
                              .anyMatch(constraint -> requiredConfigurator.test(
                                  constraint,
                                  binding
                              ))) {
            binding.getField().setRequiredIndicatorVisible(true);
        }
    }
}
