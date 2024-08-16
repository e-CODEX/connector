/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.documentation;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.view.StaticContentView;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The `InstallationDocumentationView` class represents a view that displays installation
 * documentation.
 */
@Component
@UIScope
@Route(value = InstallationDocumentationView.ROUTE, layout = DocumentationLayout.class)
@Order(4)
@TabMetadata(title = "Installation", tabGroup = DocumentationLayout.TAB_GROUP_NAME)
public class InstallationDocumentationView extends StaticContentView {
    public static final String ROUTE = "installation";

    /**
     * Constructor.
     */
    public InstallationDocumentationView() {
        super("documentation/installation/installation_index.html");
    }
}
