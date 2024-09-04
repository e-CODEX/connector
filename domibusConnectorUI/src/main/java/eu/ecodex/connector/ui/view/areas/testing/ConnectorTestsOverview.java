/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.testing;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.view.StaticContentView;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The ConnectorTestsOverview class represents a component that displays information about Connector
 * Tests in the Domibus Connector Administration UI.
 *
 * @see StaticContentView
 * @see UIScope
 * @see Component
 * @see Route
 * @see Order
 * @see TabMetadata
 */
@UIScope
@Component
@Route(value = ConnectorTestsOverview.ROUTE, layout = ConnectorTestsLayout.class)
@Order(3)
@TabMetadata(
    title = "Information on Connector Tests", tabGroup = ConnectorTestsLayout.TAB_GROUP_NAME
)
public class ConnectorTestsOverview extends StaticContentView {
    public static final String ROUTE = "information";

    /**
     * Constructor.
     *
     * @see StaticContentView
     * @see UIScope
     * @see Component
     * @see Route
     * @see Order
     * @see TabMetadata
     */
    public ConnectorTestsOverview() {
        super("documentation/ui/c2ctests/connector_tests_overview.html");
    }
}
