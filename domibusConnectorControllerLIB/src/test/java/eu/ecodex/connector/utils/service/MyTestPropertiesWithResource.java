/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.utils.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * This class represents an instance of MyTestPropertiesWithResource. It provides methods to get and
 * set the value of the 'r' property, which represents a resource.
 */
@Getter
@Setter
public class MyTestPropertiesWithResource {
    @SuppressWarnings("checkstyle:MemberName")
    private Resource r = new ClassPathResource("/testfile");
}
