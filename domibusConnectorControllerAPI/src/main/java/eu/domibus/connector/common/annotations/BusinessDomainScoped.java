package eu.domibus.connector.common.annotations;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.*;


@Scope(value = BusinessDomainScoped.DC_BUSINESS_DOMAIN_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessDomainScoped {
    String DC_BUSINESS_DOMAIN_SCOPE_NAME = "dcBusinessDomain";
}
