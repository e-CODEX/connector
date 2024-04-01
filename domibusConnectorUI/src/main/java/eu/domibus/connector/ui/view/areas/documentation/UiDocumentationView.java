package eu.domibus.connector.ui.view.areas.documentation;


import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.view.StaticContentView;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = UiDocumentationView.ROUTE, layout = DocumentationLayout.class)
@Order(3)
@TabMetadata(title = "UI", tabGroup = DocumentationLayout.TAB_GROUP_NAME)
public class UiDocumentationView extends StaticContentView {
    public static final String ROUTE = "ui";

    public UiDocumentationView() {
        super("documentation/ui/ui_index.html");
    }
}
