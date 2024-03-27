package eu.domibus.connector.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;


/**
 * This annotation is set if the annotated type
 * should be mapped as nested. Nested means,
 * that every field is mapped as individual property
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapNested {
}
