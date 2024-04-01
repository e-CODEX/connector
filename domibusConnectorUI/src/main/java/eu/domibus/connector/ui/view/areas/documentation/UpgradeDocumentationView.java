package eu.domibus.connector.ui.view.areas.documentation;


import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.view.StaticContentView;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = UpgradeDocumentationView.ROUTE, layout = DocumentationLayout.class)
@Order(5)
@TabMetadata(title = "Upgrade", tabGroup = DocumentationLayout.TAB_GROUP_NAME)
public class UpgradeDocumentationView extends StaticContentView {
    public static final String ROUTE = "upgrade";

    public UpgradeDocumentationView() {
        super("documentation/upgrade/upgrade_index.html");
    }
}
