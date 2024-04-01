package eu.domibus.connector.ui.view.areas.documentation;


import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTabs;
import org.springframework.context.ApplicationContext;


@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(DocumentationLayout.ROUTE_PREFIX)
@ParentLayout(DCMainLayout.class)
public class DocumentationLayout extends DCVerticalLayoutWithTabs {
    public static final String ROUTE_PREFIX = "documentation";
    public static final String TAB_GROUP_NAME = "Documentation";

    public DocumentationLayout(ApplicationContext applicationContext) {
        super(TAB_GROUP_NAME, applicationContext);
    }
}
