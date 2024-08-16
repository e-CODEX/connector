/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.tools;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.stereotype.Component;

/**
 * The ToolsView class is a component that represents a tab section in the Domibus Connector
 * Administration UI.
 */
@Component
@UIScope
@Route(value = ToolsView.ROUTE, layout = ToolsLayout.class)
@RoleRequired(role = "ADMIN")
@TabMetadata(title = "Tools", tabGroup = ToolsLayout.TAB_GROUP_NAME)
public class ToolsView extends VerticalLayout {
    public static final String ROUTE = "tools";

    /**
     * Constructor.
     */
    public ToolsView() {
        add(new Label("This tab section contains a list of tools"));
    }
}
