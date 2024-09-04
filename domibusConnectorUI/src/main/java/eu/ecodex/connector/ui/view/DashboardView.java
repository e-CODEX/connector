/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view;

import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.layout.DCMainLayout;

/**
 * The DashboardView class represents the main view of the Domibus Connector Administration UI.
 *
 * @see VerticalLayout
 * @see NativeLabel
 * @see UIScope
 * @see Route
 * @see PageTitle
 */
@UIScope
@Route(value = DashboardView.ROUTE, layout = DCMainLayout.class)
@PageTitle("domibusConnector - Administrator")
public class DashboardView extends VerticalLayout {
    public static final String ROUTE = "";
    NativeLabel welcomeLabel = new NativeLabel();

    public DashboardView() {
        welcomeLabel.setText("Welcome to Domibus Connector Administration UI");
        add(welcomeLabel);
    }
}
