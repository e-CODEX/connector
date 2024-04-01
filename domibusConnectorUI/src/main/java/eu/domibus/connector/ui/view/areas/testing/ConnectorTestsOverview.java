package eu.domibus.connector.ui.view.areas.testing;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.view.StaticContentView;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@UIScope
@Component
@Route(value = ConnectorTestsOverview.ROUTE, layout = ConnectorTestsLayout.class)
@Order(3)
@TabMetadata(title = "Information on Connector Tests", tabGroup = ConnectorTestsLayout.TAB_GROUP_NAME)
public class ConnectorTestsOverview extends StaticContentView {
    public static final String ROUTE = "information";

    public ConnectorTestsOverview() {
        super("documentation/ui/c2ctests/connector_tests_overview.html");
    }
}
