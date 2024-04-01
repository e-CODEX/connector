package eu.domibus.connector.ui.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.security.core.context.SecurityContextHolder;


@Route(value = LogoutView.ROUTE)
@org.springframework.stereotype.Component
@UIScope
public class LogoutView extends Div implements BeforeEnterObserver {
    public static final String ROUTE = "logout";

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        SecurityContextHolder.clearContext();
        event.rerouteTo(LoginView.ROUTE);

        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().getSession().close();
    }
}
