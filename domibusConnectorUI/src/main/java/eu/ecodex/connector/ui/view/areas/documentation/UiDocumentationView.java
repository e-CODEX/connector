/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.documentation;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.view.StaticContentView;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
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
