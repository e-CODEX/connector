/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.users;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.utils.RoleRequired;
import org.springframework.stereotype.Component;

/**
 * The UserOverview class represents a class that serves as a redirect to the UserList class.
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
@Route(value = UserOverview.ROUTE, layout = UserLayout.class)
@RoleRequired(role = "ADMIN")
public class UserOverview extends VerticalLayout implements BeforeEnterObserver {
    // This class does not do much, it is just a redirect
    // maybe it's better to directly route to the default active tab in
    // DCMainLayout
    // UserLayout already has prefix "user"
    public static final String ROUTE = "";

    // Always redirect to Import
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.forwardTo(UserList.class);
    }
}
