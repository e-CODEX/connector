/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.utils.binder;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.internal.BeanUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * The SpringBeanValidator class is an implementation of the Validator interface in the Spring
 * Framework. It is used to validate values of a specified property in a bean using the JSR-303
 * validation framework.
 */
public class SpringBeanValidator implements Validator<Object> {
    private final jakarta.validation.Validator javaxValidator;
    private final String propertyName;
    private final Class<?> beanType;

    /**
     * Creates a new JSR-303 {@code BeanValidator} that validates values of the specified property.
     * Localizes validation messages using the {@linkplain Locale#getDefault() default locale}.
     *
     * @param beanType     the bean type declaring the property, not null
     * @param propertyName the property to validate, not null
     * @throws IllegalStateException if {@link BeanUtil#checkBeanValidationAvailable()} returns
     *                               false
     */
    public SpringBeanValidator(
        jakarta.validation.Validator javaxValidator, Class<?> beanType, String propertyName) {
        Objects.requireNonNull(beanType, "bean class cannot be null");
        Objects.requireNonNull(propertyName, "property name cannot be null");

        this.beanType = beanType;
        this.propertyName = propertyName;
        this.javaxValidator = javaxValidator;
    }

    public jakarta.validation.Validator getJavaxBeanValidator() {
        return this.javaxValidator;
    }

    private static final class ContextImpl implements MessageInterpolator.Context, Serializable {
        private final ConstraintViolation<?> violation;

        private ContextImpl(ConstraintViolation<?> violation) {
            this.violation = violation;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return violation.getConstraintDescriptor();
        }

        @Override
        public Object getValidatedValue() {
            return violation.getInvalidValue();
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            return violation.unwrap(type);
        }
    }

    /**
     * Validates the given value as if it were the value of the bean property configured for this
     * validator. Returns {@code Result.ok} if there are no JSR-303 constraint violations, a
     * {@code Result.error} of chained constraint violation messages otherwise.
     *
     * <p>Null values are accepted unless the property has an {@code @NotNull} annotation or
     * equivalent.
     *
     * @param value   the input value to validate
     * @param context the value context for validation
     * @return the validation result
     */
    public ValidationResult apply(final Object value, ValueContext context) {
        Set<? extends ConstraintViolation<?>> violations = getJavaxBeanValidator()
            .validateValue(beanType, propertyName, value);

        var locale = context.getLocale().orElse(Locale.getDefault());

        var result = violations
            .stream()
            .map(violation -> ValidationResult
                .error(getMessage(violation, locale)))
            .findFirst();
        return result.orElse(ValidationResult.ok());
    }

    @Override
    public String toString() {
        return String.format("%s[%s.%s]", getClass().getSimpleName(),
                             beanType.getSimpleName(), propertyName
        );
    }

    /**
     * Returns the interpolated error message for the given constraint violation using the locale
     * specified for this validator.
     *
     * @param violation the constraint violation
     * @param locale    the used locale
     * @return the localized error message
     */
    protected String getMessage(
        ConstraintViolation<?> violation,
        Locale locale) {
        return javaxValidator.unwrap(ValidatorFactory.class)
                             .getMessageInterpolator()
                             .interpolate(violation.getMessageTemplate(),
                                          createContext(violation), locale
                             );
    }

    /**
     * Creates a simple message interpolation context based on the given constraint violation.
     *
     * @param violation the constraint violation
     * @return the message interpolation context
     */
    protected MessageInterpolator.Context createContext(ConstraintViolation<?> violation) {
        return new SpringBeanValidator.ContextImpl(violation);
    }
}
