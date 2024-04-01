package eu.domibus.connector.ui.view.areas.tools;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = ToolsView.ROUTE, layout = ToolsLayout.class)
@RoleRequired(role = "ADMIN")
@TabMetadata(title = "Tools", tabGroup = ToolsLayout.TAB_GROUP_NAME)
public class ToolsView extends VerticalLayout {
    public static final String ROUTE = "tools";

    public ToolsView() {
        add(new Label("This tab section contains a list of tools"));
    }
}
