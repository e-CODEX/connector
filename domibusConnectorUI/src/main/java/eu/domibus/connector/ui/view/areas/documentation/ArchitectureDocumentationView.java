package eu.domibus.connector.ui.view.areas.documentation;


import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.view.StaticContentView;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = ArchitectureDocumentationView.ROUTE, layout = DocumentationLayout.class)
@Order(1)
@TabMetadata(title = "Architecture", tabGroup = DocumentationLayout.TAB_GROUP_NAME)
public class ArchitectureDocumentationView extends StaticContentView {
    public static final String ROUTE = "architecture";

    public ArchitectureDocumentationView() {
        super("documentation/architecture/architecture_index.html");
    }
}
