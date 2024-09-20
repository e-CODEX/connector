/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.configuration;

import jakarta.xml.ws.WebServiceContext;
import org.apache.cxf.jaxws.context.WebServiceContextImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is a configuration class used for configuring a web service context bean in the
 * application.
 */
@Configuration
public class LinkConfiguration {
    @Bean
    WebServiceContext webServiceContext() {
        return new WebServiceContextImpl();
    }
}
