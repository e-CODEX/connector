package eu.domibus.connector.ui.view.areas.messages;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.utils.RoleRequired;
import org.springframework.stereotype.Component;


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
