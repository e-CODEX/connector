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
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Annotation to qualify the ObjectMapper used for domain model to JSON object mapping.
 *
 * <p>This annotation is used to specify the ObjectMapper bean that should be used for mapping
 * domain model objects to JSON objects. It can be applied to a class, method, field or parameter
 * to indicate that the specified ObjectMapper bean should be used for mapping the annotated
 * element. This allows for multiple ObjectMapper beans to be defined, each catering to a specific
 * mapping requirement.
 *
 * <p>{@code DomainModelJsonObjectMapper} is a meta-annotation that is annotated with Spring's
 * {@link Qualifier} annotation, allowing it to be
 * used as a custom qualifier for specifying the desired ObjectMapper bean.
 */
@Qualifier(DomainModelJsonObjectMapper.VALUE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainModelJsonObjectMapper {
    String VALUE = "eu.ecodex.connector.common.annotations.DomainModelJsonObjectMapper";
}
