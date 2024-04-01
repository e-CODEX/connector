package eu.domibus.connector.ui.view.areas.users;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.utils.RoleRequired;
import org.springframework.stereotype.Component;


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
