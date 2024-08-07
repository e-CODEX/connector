package eu.domibus.connector.persistence.testutil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that defines a range of versions from which a certain feature is available. This
 * annotation can be used on methods to indicate that they are available from a specific version.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FromVersions {
    /**
     * Returns an array of FromVersion annotations, which define the versions from which a certain
     * feature is available.
     *
     * @return an array of FromVersion annotations
     */
    FromVersion[] value();
}
