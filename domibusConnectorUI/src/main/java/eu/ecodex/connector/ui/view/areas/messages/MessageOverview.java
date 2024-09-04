/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.messages;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.utils.RoleRequired;
import org.springframework.stereotype.Component;

/**
 * The {@code MessageOverview} class is a {@code VerticalLayout} component that serves as a redirect
 * to the {@code MessagesList} class.
 *
 * @see VerticalLayout
 * @see BeforeEnterObserver
 * @see UIScope
 * @see Component
 * @see Route
 * @see RoleRequired
 */
@UIScope
@Component
@Route(value = MessageOverview.ROUTE, layout = MessageLayout.class)
@RoleRequired(role = "ADMIN")
public class MessageOverview extends VerticalLayout implements BeforeEnterObserver {
    // This class does not do much, it is just a redirect
    // maybe it's better to directly route to the default active tab in
    // DCMainLayout
    // Pmodelayout already has prefix "messages"
    public static final String ROUTE = "";

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.forwardTo(MessagesList.class);
    }
}
