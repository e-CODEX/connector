/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration;

import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.utils.RoleRequired;
import org.springframework.stereotype.Component;

/**
 * The ConfigurationOverview class represents a view component in the Domibus Connector
 * application.
 */
@UIScope
@Component
@Route(value = ConfigurationOverview.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@SuppressWarnings("squid:S1135")
public class ConfigurationOverview extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE = "";

    public ConfigurationOverview() {
        var configurationLabel = new NativeLabel("Configuration");
        this.add(configurationLabel);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // TODO see why this method body is empty
        // event.getUI().navigate(EnvironmentConfiguration.ROUTE);
    }
}
