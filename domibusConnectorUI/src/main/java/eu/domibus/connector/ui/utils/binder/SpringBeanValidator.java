package eu.domibus.connector.ui.utils.binder;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.internal.BeanUtil;
import com.vaadin.flow.data.binder.Validator;

import javax.validation.*;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class SpringBeanValidator implements Validator<Object> {

    private final javax.validation.Validator javaxValidator;
    private String propertyName;
    private Class<?> beanType;


    /**
     * Creates a new JSR-303 {@code BeanValidator} that validates values of the
     * specified property. Localizes validation messages using the
     * {@linkplain Locale#getDefault() default locale}.
     *
     * @param beanType     the bean type declaring the property, not null
     * @param propertyName the property to validate, not null
     * @throws IllegalStateException if {@link BeanUtil#checkBeanValidationAvailable()} returns
     *                               false
     */
    public SpringBeanValidator(javax.validation.Validator javaxValidator, Class<?> beanType, String propertyName) {
        Objects.requireNonNull(beanType, "bean class cannot be null");
        Objects.requireNonNull(propertyName, "property name cannot be null");

        this.beanType = beanType;
        this.propertyName = propertyName;
        this.javaxValidator = javaxValidator;
    }

    public javax.validation.Validator getJavaxBeanValidator() {
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
     * Validates the given value as if it were the value of the bean property
     * configured for this validator. Returns {@code Result.ok} if there are no
     * JSR-303 constraint violations, a {@code Result.error} of chained
     * constraint violation messages otherwise.
     * <p>
     * Null values are accepted unless the property has an {@code @NotNull}
     * annotation or equivalent.
     *
     * @param value
     *            the input value to validate
     * @param context
     *            the value context for validation
     * @return the validation result
     */
    public ValidationResult apply(final Object value, ValueContext context) {
        Set<? extends ConstraintViolation<?>> violations = getJavaxBeanValidator()
                .validateValue(beanType, propertyName, value);

        Locale locale = context.getLocale().orElse(Locale.getDefault());

        Optional<ValidationResult> result = violations.stream()
                .map(violation -> ValidationResult
                        .error(getMessage(violation, locale)))
                .findFirst();
        return result.orElse(ValidationResult.ok());
    }

    @Override
    public String toString() {
        return String.format("%s[%s.%s]", getClass().getSimpleName(),
                beanType.getSimpleName(), propertyName);
    }

    /**
     * Returns the underlying JSR-303 bean validator factory used. A factory is
     * created using {@link Validation} if necessary.
     *
     * @return the validator factory to use
     */
//    protected static ValidatorFactory getJavaxBeanValidatorFactory() {
//        return SpringBeanValidator.LazyFactoryInitializer.FACTORY;
//    }


    /**
     * Returns the interpolated error message for the given constraint violation
     * using the locale specified for this validator.
     *
     * @param violation
     *            the constraint violation
     * @param locale
     *            the used locale
     * @return the localized error message
     */
    protected String getMessage(ConstraintViolation<?> violation,
                                Locale locale) {
        return javaxValidator.unwrap(ValidatorFactory.class)
        .getMessageInterpolator()
                .interpolate(violation.getMessageTemplate(),
                        createContext(violation), locale);
    }

    /**
     * Creates a simple message interpolation context based on the given
     * constraint violation.
     *
     * @param violation
     *            the constraint violation
     * @return the message interpolation context
     */
    protected MessageInterpolator.Context createContext(ConstraintViolation<?> violation) {
        return new SpringBeanValidator.ContextImpl(violation);
    }

//    private static class LazyFactoryInitializer implements Serializable {
//        private static final ValidatorFactory FACTORY = getFactory();
//
//        private LazyFactoryInitializer() {
//        }
//
//        private static ValidatorFactory getFactory() {
//            return Validation.buildDefaultValidatorFactory();
//        }
//    }

}
