package eu.domibus.connector.ui.utils.binder;

import com.vaadin.flow.data.binder.ErrorLevel;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpringBeanValidationBinderFactory {

    private final Validator validator;

    public SpringBeanValidationBinderFactory(Validator validator) {
        this.validator = validator;
    }

    public <BEAN> SpringBeanValidationBinder<BEAN> create(Class<BEAN> item) {
        return create(item, false);
    }

    public <BEAN> SpringBeanValidationBinder<BEAN> create(Class<BEAN> item, boolean scanNested) {
        SpringBeanValidationBinder<BEAN> binder = new SpringBeanValidationBinder<BEAN>(validator, item, scanNested);

        binder.withValidator(new com.vaadin.flow.data.binder.Validator<BEAN>() {
            @Override
            public ValidationResult apply(BEAN value, ValueContext context) {
                Set<ConstraintViolation<BEAN>> validate = validator.validate(value);
                if (validate.isEmpty()) {
                    return ValidationResult.ok();
                } else {
                    String errorMessage = validate.stream()
                            .map(cv -> {
                                return cv.getPropertyPath().toString() + " " + cv.getMessage();
                            })
                            .collect(Collectors.joining("\n"));
                    return ValidationResult.create(errorMessage, ErrorLevel.ERROR);
                }
            }
        });

        return binder;
    }

}
