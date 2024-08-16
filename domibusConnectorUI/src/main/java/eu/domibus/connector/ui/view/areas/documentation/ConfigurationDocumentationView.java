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
 * The ConfigurationDocumentationView class represents a view component for displaying the
 * configuration documentation.
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
@Route(value = ConfigurationDocumentationView.ROUTE, layout = DocumentationLayout.class)
@Order(2)
@TabMetadata(title = "Configuration", tabGroup = DocumentationLayout.TAB_GROUP_NAME)
public class ConfigurationDocumentationView extends StaticContentView {
    public static final String ROUTE = "configuration";

    /**
     * Constructor.
     */
    public ConfigurationDocumentationView() {
        super("documentation/configuration/configuration_index.html");
    }
}
