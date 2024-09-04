/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.spring;

import eu.ecodex.connector.common.annotations.BusinessDomainScoped;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This class represents the test properties for a specific business domain. It is used to configure
 * the properties for the test environment.
 */
@Component
@BusinessDomainScoped
@ConfigurationProperties(prefix = "mytest.prop-dash")
@Getter
@Setter
public class TestProperties {
    private String t1;
    private String camelCaseProperty;
}
