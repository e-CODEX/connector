/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.link.service;

import static eu.domibus.connector.link.service.LinkPluginQualifier.LINK_QUALIFIER_NAME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The {@code LinkPluginQualifier} annotation is used to qualify the usage of a link plugin in the
 * Domibus connector.
 *
 * <p>It can be applied to fields, methods, parameters, types, or annotation types.
 *
 * <p>The qualifier name is specified by the {@code LINK_QUALIFIER_NAME} constant in the
 * annotation.
 */
@Target(
    {ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE,
        ElementType.ANNOTATION_TYPE}
)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier(LINK_QUALIFIER_NAME)
public @interface LinkPluginQualifier {
    String LINK_QUALIFIER_NAME =
        "eu.domibus.connector.link.service.LinkPluginQualifier";
}
