package eu.domibus.connector.ui.utils.binder;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.*;

import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

//TODO: create spring factory...
public class SpringBeanValidationBinder<BEAN> extends Binder<BEAN> {


    private final Class<BEAN> beanType;
    private final Validator javaxValidator;

    private RequiredFieldConfigurator requiredConfigurator = RequiredFieldConfigurator.DEFAULT;

    /**
     * Creates a new binder that uses reflection based on the provided bean type
     * to resolve bean properties.
     * It assumes that the javaxValidator is initialized by SpringFramework
     *
     *
     * A {@link SpringBeanValidator} is added to each binding that is defined using a
     * property name.
     *
     * @param beanType              the bean type to use, not {@code null}
     * @param scanNestedDefinitions if {@code true}, scan for nested property definitions as well
     */
    public SpringBeanValidationBinder(javax.validation.Validator javaxValidator, Class<BEAN> beanType, boolean scanNestedDefinitions) {
        super(beanType, scanNestedDefinitions);
        this.javaxValidator = javaxValidator;
//        if (!BeanUtil.checkBeanValidationAvailable()) {
//            throw new IllegalStateException(
//                    com.vaadin.flow.data.binder.BeanValidationBinder.class.getSimpleName()
//                            + " cannot be used because a JSR-303 Bean Validation "
//                            + "implementation not found on the classpath or could not be initialized. Use "
//                            + Binder.class.getSimpleName() + " instead");
//        }
        this.beanType = beanType;
    }


    /**
     * Gets field required indicator configuration logic.
     *
     * @return required indicator configurator, may be {@code null}
     * @see #setRequiredConfigurator(RequiredFieldConfigurator)
     */
    public RequiredFieldConfigurator getRequiredConfigurator() {
        return requiredConfigurator;
    }

    @Override
    protected BindingBuilder<BEAN, ?> configureBinding(
            BindingBuilder<BEAN, ?> binding,
            PropertyDefinition<BEAN, ?> definition) {
        Class<?> actualBeanType = findBeanType(beanType, definition);
        //TODO: replace...
        SpringBeanValidator validator = new SpringBeanValidator(javaxValidator, actualBeanType,
                definition.getTopLevelName());

        if (requiredConfigurator != null) {
            configureRequired(binding, definition, validator);
        }
        return binding.withValidator(validator);
    }


    /**
     * Finds the bean type containing the property the given definition refers
     * to.
     *
     * @param beanType
     *            the root beanType
     * @param definition
     *            the definition for the property
     * @return the bean type containing the given property
     */
    @SuppressWarnings({ "rawtypes" })
    private Class<?> findBeanType(Class<BEAN> beanType,
                                  PropertyDefinition<BEAN, ?> definition) {
        if (definition instanceof BeanPropertySet.NestedBeanPropertyDefinition) {
            return ((BeanPropertySet.NestedBeanPropertyDefinition) definition).getParent()
                    .getType();
        } else {
            // Non nested properties must be defined in the main type
            return beanType;
        }
    }

    private void configureRequired(BindingBuilder<BEAN, ?> binding,
                                   PropertyDefinition<BEAN, ?> definition, SpringBeanValidator validator) {
        assert requiredConfigurator != null;
        Class<?> propertyHolderType = definition.getPropertyHolderType();
        BeanDescriptor descriptor = validator.getJavaxBeanValidator()
                .getConstraintsForClass(propertyHolderType);
        PropertyDescriptor propertyDescriptor = descriptor
                .getConstraintsForProperty(definition.getTopLevelName());
        if (propertyDescriptor == null) {
            return;
        }
        if (propertyDescriptor.getConstraintDescriptors().stream()
                .map(ConstraintDescriptor::getAnnotation)
                .anyMatch(constraint -> requiredConfigurator.test(constraint,
                        binding))) {
            binding.getField().setRequiredIndicatorVisible(true);
        }
    }

    /**
     * Sets a logic which allows to configure require indicator via
     * {@link HasValue#setRequiredIndicatorVisible(boolean)} based on property
     * descriptor.
     * <p>
     * Required indicator configuration will not be used at all if
     * {@code configurator} is null.
     * <p>
     * By default the {@link RequiredFieldConfigurator#DEFAULT} configurator is
     * used.
     *
     * @param configurator
     *            required indicator configurator, may be {@code null}
     */
    public void setRequiredConfigurator(
            RequiredFieldConfigurator configurator) {
        requiredConfigurator = configurator;
    }


}
