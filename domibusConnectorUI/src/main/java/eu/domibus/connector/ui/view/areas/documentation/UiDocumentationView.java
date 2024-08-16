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
 * The UiDocumentationView class represents a Vaadin view component that displays the UI
 * documentation.
 *
 * @see StaticContentView
 * @see Component
 * @see UIScope
 * @see Route
 * @see Order
 * @see TabMetadata
 */
@Component
@UIScope
@Route(value = UiDocumentationView.ROUTE, layout = DocumentationLayout.class)
@Order(3)
@TabMetadata(title = "UI", tabGroup = DocumentationLayout.TAB_GROUP_NAME)
public class UiDocumentationView extends StaticContentView {
    public static final String ROUTE = "ui";

    /**
     * Constructor.
     */
    public UiDocumentationView() {
        super("documentation/ui/ui_index.html");
    }
}
