/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.lib.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify a name-value pair for the Mapped Diagnostic Context (MDC). MDC
 * is a feature of some logging frameworks that allows the association of contextual information
 * with the current thread of execution. The MDC can be used to output additional metadata in log
 * messages.
 *
 * <p>Note that this annotation is meant to be used on methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MDC {
    /**
     * name of the mdc key.
     */
    String name();

    /**
     * value of the mdc key.
     */
    String value();
}
