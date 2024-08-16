/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.utils.binder;

import com.vaadin.flow.data.binder.ErrorLevel;
import com.vaadin.flow.data.binder.ValidationResult;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.stereotype.Component;

/**
 * The {@code SpringBeanValidationBinderFactory} class is responsible for creating instances of
 * {@link SpringBeanValidationBinder}. It provides a convenient way to bind and validate form fields
 * in a Spring framework application using Bean Validation (JSR-303).
 *
 * <p>Instances of this class should be created as Spring beans and injected into other classes
 * that require the functionality of {@link SpringBeanValidationBinder}.
 */
@Component
@SuppressWarnings({"checkstyle:MethodTypeParameterName", "squid:S2293", "squid:S119"})
public class SpringBeanValidationBinderFactory {
    private final Validator validator;

    public SpringBeanValidationBinderFactory(Validator validator) {
        this.validator = validator;
    }

    public <BEAN> SpringBeanValidationBinder<BEAN> create(Class<BEAN> item) {
        return create(item, false);
    }

    /**
     * Creates a new instance of {@link SpringBeanValidationBinder} with the specified bean type and
     * scanNested flag.
     *
     * @param item       the bean type to use for binding and validation
     * @param scanNested if true, scan for nested property definitions as well
     * @param <BEAN>     the type of the bean being bound
     * @return the created instance of {@link SpringBeanValidationBinder}
     */
    public <BEAN> SpringBeanValidationBinder<BEAN> create(Class<BEAN> item, boolean scanNested) {
        SpringBeanValidationBinder<BEAN> binder =
            new SpringBeanValidationBinder<>(validator, item, scanNested);

        binder.withValidator((com.vaadin.flow.data.binder.Validator<BEAN>) (value, context) -> {
            Set<ConstraintViolation<BEAN>> validate = validator.validate(value);
            if (validate.isEmpty()) {
                return ValidationResult.ok();
            } else {
                String errorMessage = validate.stream()
                                              .map(cv -> cv.getPropertyPath().toString() + " "
                                                  + cv.getMessage())
                                              .collect(Collectors.joining("\n"));
                return ValidationResult.create(errorMessage, ErrorLevel.ERROR);
            }
        });

        return binder;
    }
}
