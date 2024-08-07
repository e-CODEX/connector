package eu.domibus.connector.security.configuration.validation;


import eu.domibus.connector.dss.configuration.validation.ValidEtisValidationPolicyXmlValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE, FIELD, METHOD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = {CheckAllowedAdvancedElectronicSystemTypeValidator.class})
@Documented
@NotNull
public @interface CheckAllowedAdvancedElectronicSystemType  {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
