package eu.domibus.connector.lib.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MDC {
    /**
     * name of the mdc key
     */
    String name();

    /**
     * value of the mdc key
     */
    String value();
}
