/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

/**
 * The {@code LogoutView} class represents a view for logging out of the application.
 *
 * @see Div
 * @see BeforeEnterObserver
 */
@Route(value = LogoutView.ROUTE)
@AnonymousAllowed
public class LogoutView extends Div implements BeforeEnterObserver {
    public static final String ROUTE = "logout";

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
            VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null
        );
        UI.getCurrent().getPage().setLocation(LoginView.ROUTE);
    }
}
