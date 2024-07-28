/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.exception.handling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation that indicates that an exception should be stored into the database.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 *     @StoreMessageExceptionIntoDatabase(passException = false)
 *     public void processMessage(DomibusConnectorMessage message) {
 *         throw DomibusConnectorMessageExceptionBuilder.createBuilder()
 *                     .setText("i am an exception!")
 *                     .setSourceObject(this)
 *                     .setMessage(message)
 *                     .build();
 *     }
 * }</pre>
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * @see StoreMessageExceptionIntoDatabaseAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StoreMessageExceptionIntoDatabase {
    /**
     * This method is a part of the custom annotation {@code StoreMessageExceptionIntoDatabase}.
     * It returns a boolean value that indicates whether the exception should be passed on or not.
     * should the exception thrown again after it has been stored into the database?
     *
     * @return {@code true} if the exception should be passed on, {@code false} otherwise.
     * @see StoreMessageExceptionIntoDatabase
     */
    boolean passException() default false;

    /**
     * Returns the info text associated with the StoreMessageExceptionIntoDatabase annotation.
     *
     * @return the info text
     */
    String infoText() default "";
}
