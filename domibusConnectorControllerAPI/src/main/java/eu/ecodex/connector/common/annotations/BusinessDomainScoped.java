/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.common.annotations;

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
