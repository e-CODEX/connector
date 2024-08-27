/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.configuration;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * This class represents the web context configuration for the Domibus Connector Vaadin web
 * application. It is responsible for enabling transaction management, setting up web MVC, enabling
 * Vaadin framework, and configuring multipart requests.
 */
@EnableTransactionManagement
@Configuration
@EnableWebMvc
@EnableVaadin("eu.domibus.connector.ui")
@MultipartConfig
public class DomibusConnectorVaadinWebContext {
}
