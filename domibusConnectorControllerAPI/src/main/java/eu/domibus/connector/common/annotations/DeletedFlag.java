package eu.domibus.connector.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;


/**
 * This annotation is set as an indicator,
 * that this field acts as deleted indicator
 * This means if deleted is true, it should be
 * serialized to null properties
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeletedFlag {
}
