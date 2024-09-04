/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.configuration;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import eu.ecodex.connector.ui.login.LoginView;
import eu.ecodex.connector.ui.view.AccessDeniedView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Represents a service init listener for configuring the UI service in Vaadin.
 */
@SuppressWarnings("squid:S1135")
@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {
    private static final Logger LOGGER = LogManager.getLogger(ConfigureUIServiceInitListener.class);
    private final ConnectorUiConfigurationProperties connectorUiConfigurationProperties;
    private final AuthenticationManager authenticationManager;

    public ConfigureUIServiceInitListener(
        ConnectorUiConfigurationProperties connectorUiConfigurationProperties,
        AuthenticationManager authenticationManager) {
        this.connectorUiConfigurationProperties = connectorUiConfigurationProperties;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final var ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    /**
     * Reroutes the user if (s)he is not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (connectorUiConfigurationProperties.isAutoLoginEnabled()) {
            LOGGER.warn("Auto Login is enabled! Only use this in development!");
            var authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    connectorUiConfigurationProperties.getAutoLoginUser(),
                    connectorUiConfigurationProperties.getAutoLoginPassword()
                ));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        }

        if (!LoginView.class.equals(event.getNavigationTarget())
            && !SecurityUtils.isUserLoggedIn()) {
            String path = event.getLocation().getPath();
            // TODO should work after vaadin lib update
            //  after Login forward to previous page
            //  RouteParameters p = new RouteParameters(LoginView.PREVIOUS_ROUTE_PARAMETER, path);
            //  event.forwardTo(LoginView.class, p);
            event.forwardTo(LoginView.class);
        }
        Class<?> navigationTarget = event.getNavigationTarget();
        if (!SecurityUtils.isUserAllowedToView(navigationTarget)) {
            event.forwardTo(AccessDeniedView.class);
        }
        // forward to default page, used for development
        if (connectorUiConfigurationProperties.isAutoLoginEnabled()
            && LoginView.class.equals(event.getNavigationTarget())
            && StringUtils.hasText(connectorUiConfigurationProperties.getDefaultRoute())) {
            event.forwardTo(connectorUiConfigurationProperties.getDefaultRoute());
        }
    }
}
