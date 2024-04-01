package eu.domibus.connector.ui.view.areas.configuration;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.utils.RoleRequired;
import org.springframework.stereotype.Component;


@UIScope
@Component
@Route(value = ConfigurationOverview.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
public class ConfigurationOverview extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE = "";

    public ConfigurationOverview() {
        Label l = new Label("Configuration");
        this.add(l);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        //        event.getUI().navigate(EnvironmentConfiguration.ROUTE);
    }

    //    public ConfigurationOverviewView() {
    //        Label label = new Label();
    //        label.setText("Configuration area, use the tabs above to choose the configuration topic");
    //        add(label);
    //    }
}
