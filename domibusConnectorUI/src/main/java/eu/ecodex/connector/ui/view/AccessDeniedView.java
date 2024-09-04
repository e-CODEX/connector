/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view;

import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.configuration.SecurityUtils;
import eu.ecodex.connector.ui.layout.DCMainLayout;
import eu.ecodex.connector.ui.login.LoginView;

/**
 * The {@code AccessDeniedView} class represents a view that is shown when a user does not have
 * sufficient privileges to access a certain resource.
 */
@UIScope
@Route(value = AccessDeniedView.ROUTE, layout = DCMainLayout.class)
@PageTitle("domibusConnector - Administrator")
@SuppressWarnings("squid:S1135")
public class AccessDeniedView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE = "accessDenied";
    NativeLabel accessDeniedLabel = new NativeLabel();
    String view = "";

    /**
     * The {@code AccessDeniedView} class represents a view that is shown when a user does not have
     * sufficient privileges to access a certain resource.
     */
    public AccessDeniedView() {
        String username = SecurityUtils.getUsername();
        accessDeniedLabel.setText(
            "User [" + username + "]  has not enough privileges to access " + view);
        add(accessDeniedLabel);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isUserLoggedIn()) {
            event.getUI().navigate(LoginView.ROUTE);
        }
        // TODO: get previous view and set to view...
    }
}
