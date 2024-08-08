package eu.domibus.connector.persistence.testutil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that defines a version from which a certain feature is available. This annotation can
 * be used on methods to indicate that they are available from a specific version.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(FromVersions.class)
public @interface FromVersion {
    /**
     * Returns the value of the annotation.
     *
     * @return the value of the annotation
     */
    String value();
}
