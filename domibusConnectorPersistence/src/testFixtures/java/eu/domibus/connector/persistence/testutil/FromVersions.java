package eu.domibus.connector.persistence.testutil;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FromVersions {
    FromVersion[] value();
}
