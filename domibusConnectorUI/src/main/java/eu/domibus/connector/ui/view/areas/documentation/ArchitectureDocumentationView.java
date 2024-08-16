/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.documentation;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.view.StaticContentView;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The ArchitectureDocumentationView class represents a view component that displays the
 * architecture documentation.
 */
@Component
@UIScope
@Route(value = ArchitectureDocumentationView.ROUTE, layout = DocumentationLayout.class)
@Order(1)
@TabMetadata(title = "Architecture", tabGroup = DocumentationLayout.TAB_GROUP_NAME)
public class ArchitectureDocumentationView extends StaticContentView {
    public static final String ROUTE = "architecture";

    /**
     * Constructor.
     */
    public ArchitectureDocumentationView() {
        super("documentation/architecture/architecture_index.html");
    }
}
