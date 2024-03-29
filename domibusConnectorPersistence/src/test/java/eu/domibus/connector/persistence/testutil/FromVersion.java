package eu.domibus.connector.persistence.testutil;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(FromVersions.class)
public @interface FromVersion {
    String value();
}
