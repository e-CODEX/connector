/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.configuration;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import javax.servlet.annotation.MultipartConfig;
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
