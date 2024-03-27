package eu.domibus.connector.lib.logging.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


@Aspect
public class MDCSetterAspect {
    @Around(
            value = "@annotation(eu.domibus.connector.lib.logging.MDC) && @annotation(mdcAnnotation)",
            argNames = "mdcAnnotation"
    )
    public Object handleMdc(ProceedingJoinPoint pjp, eu.domibus.connector.lib.logging.MDC mdcAnnotation) throws
            Throwable {
        org.slf4j.MDC.put(mdcAnnotation.name(), mdcAnnotation.value());
        try {
            return pjp.proceed();
        } finally {
            org.slf4j.MDC.remove(mdcAnnotation.name());
        }
    }
}
