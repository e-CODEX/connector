/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * This annotation is used to define a business domain scope for a component or method.
 *
 * <p>The BusinessDomainScoped annotation should be applied to a class or method to indicate that
 * it is scoped to a specific business domain.
 * This allows multiple instances of the annotated class or method to be created, each with its
 * own separate state based on the business domain.
 */
@Scope(
    value = BusinessDomainScoped.DC_BUSINESS_DOMAIN_SCOPE_NAME,
    proxyMode = ScopedProxyMode.TARGET_CLASS
)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessDomainScoped {
    String DC_BUSINESS_DOMAIN_SCOPE_NAME = "dcBusinessDomain";
}
