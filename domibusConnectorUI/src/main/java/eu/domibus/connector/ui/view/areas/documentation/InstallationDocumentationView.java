package eu.domibus.connector.ui.view.areas.documentation;


import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.view.StaticContentView;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = InstallationDocumentationView.ROUTE, layout = DocumentationLayout.class)
@Order(4)
@TabMetadata(title = "Installation", tabGroup = DocumentationLayout.TAB_GROUP_NAME)
public class InstallationDocumentationView extends StaticContentView {
    public static final String ROUTE = "installation";

    public InstallationDocumentationView() {
        super("documentation/installation/installation_index.html");
    }
}
