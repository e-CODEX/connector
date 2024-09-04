/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.exception.handling;

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
