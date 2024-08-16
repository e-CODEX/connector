/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.configuration.SecurityUtils;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.login.LoginView;

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
    Label accessDeniedLabel = new Label();
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
