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


@UIScope
@Route(value = AccessDeniedView.ROUTE, layout = DCMainLayout.class)
@PageTitle("domibusConnector - Administrator")
public class AccessDeniedView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE = "accessDenied";

    Label l = new Label();
    String view = "";

    public AccessDeniedView() {
        String username = SecurityUtils.getUsername();
        l.setText("User [" + username + "]  has not enough privileges to access " + view);
        add(l);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isUserLoggedIn()) {
            event.getUI().navigate(LoginView.ROUTE);
        }
        // TODO: get previous view and set to view...
    }
}
