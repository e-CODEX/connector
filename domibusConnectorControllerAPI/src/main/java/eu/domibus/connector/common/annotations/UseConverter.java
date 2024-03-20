package eu.domibus.connector.common.annotations;


import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UseConverter {

}
