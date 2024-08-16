/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.layout.DCMainLayout;

/**
 * The DashboardView class represents the main view of the Domibus Connector Administration UI.
 *
 * @see VerticalLayout
 * @see Label
 * @see UIScope
 * @see Route
 * @see PageTitle
 */
@UIScope
@Route(value = DashboardView.ROUTE, layout = DCMainLayout.class)
@PageTitle("domibusConnector - Administrator")
public class DashboardView extends VerticalLayout {
    public static final String ROUTE = "";
    Label welcomeLabel  = new Label();

    public DashboardView() {
        welcomeLabel.setText("Welcome to Domibus Connector Administration UI");
        add(welcomeLabel);
    }
}
