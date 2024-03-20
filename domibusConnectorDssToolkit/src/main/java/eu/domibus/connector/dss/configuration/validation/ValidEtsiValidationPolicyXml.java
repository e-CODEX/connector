package eu.domibus.connector.dss.configuration.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE, FIELD, METHOD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = {ValidEtisValidationPolicyXmlValidator.class})
@Documented
@NotNull
public @interface ValidEtsiValidationPolicyXml {

    String message() default "The provided xml is not a schema valid EtsiValidationPolicy!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
