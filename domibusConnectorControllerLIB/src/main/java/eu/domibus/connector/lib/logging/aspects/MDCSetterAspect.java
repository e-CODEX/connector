/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.logging.aspects;

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
        value = "@annotation(eu.domibus.connector.lib.logging.MDC) && @annotation(mdcAnnotation)",
        argNames = "mdcAnnotation"
    )
    public Object handleMdc(
        ProceedingJoinPoint pjp,
        eu.domibus.connector.lib.logging.MDC mdcAnnotation) throws Throwable {
        org.slf4j.MDC.put(mdcAnnotation.name(), mdcAnnotation.value());
        try {
            return pjp.proceed();
        } finally {
            org.slf4j.MDC.remove(mdcAnnotation.name());
        }
    }
}
