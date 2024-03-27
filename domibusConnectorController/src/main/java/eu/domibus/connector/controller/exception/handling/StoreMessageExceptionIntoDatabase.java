package eu.domibus.connector.controller.exception.handling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StoreMessageExceptionIntoDatabase {
    /**
     * @return should the exception thrown again after it has been stored
     * into the database?
     */
    boolean passException() default false;

    String infoText() default "";
}
