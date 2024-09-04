/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.lib.logging.aspects;

import eu.ecodex.connector.lib.logging.MDC;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * The MDCSetterAspect class is an Aspect that sets and removes a Mapped Diagnostic Context (MDC)
 * key-value pair for the current thread of execution. MDC is a feature of some logging frameworks
 * that allows the association of contextual information with log messages.
 */
@Aspect
public class MDCSetterAspect {
    /**
     * This method handles setting and removing a Mapped Diagnostic Context (MDC) key-value pair for
     * the current thread of execution. MDC is a feature of some logging frameworks that allows the
     * association of contextual information with log messages.
     *
     * @param pjp           the ProceedingJoinPoint object representing the method being
     *                      intercepted
     * @param mdcAnnotation the MDC annotation specifying the name-value pair for the MDC key
     * @return the result of the intercepted method
     * @throws Throwable if an error occurs during the method interception
     */
    @Around(
        value = "@annotation(eu.ecodex.connector.lib.logging.MDC) && @annotation(mdcAnnotation)",
        argNames = "mdcAnnotation"
    )
    public Object handleMdc(
        ProceedingJoinPoint pjp,
        MDC mdcAnnotation) throws Throwable {
        org.slf4j.MDC.put(mdcAnnotation.name(), mdcAnnotation.value());
        try {
            return pjp.proceed();
        } finally {
            org.slf4j.MDC.remove(mdcAnnotation.name());
        }
    }
}
