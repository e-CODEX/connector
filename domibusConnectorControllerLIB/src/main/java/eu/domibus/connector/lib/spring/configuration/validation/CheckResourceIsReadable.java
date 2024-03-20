package eu.domibus.connector.lib.spring.configuration.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ResourceReadableValidator.class)
@Documented
@NotNull
public @interface CheckResourceIsReadable {

    String message() default "{eu.domibus.connector.lib.spring.configuration.validation.resource_input_stream_valid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
