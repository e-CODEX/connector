/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.pmodes;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.StaticContentView;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The PmodeOverview class represents a Vaadin component that displays information on PMode-Sets.
 *
 * @see StaticContentView
 * @see com.vaadin.flow.component.orderedlayout.VerticalLayout
 */
@UIScope
@Component
@Route(value = PmodeOverview.ROUTE, layout = PmodeLayout.class)
@RoleRequired(role = "ADMIN")
@Order(3)
@TabMetadata(title = "Information on PMode-Sets", tabGroup = PmodeLayout.TAB_GROUP_NAME)
public class PmodeOverview extends StaticContentView {
    public static final String ROUTE = "information";

    /**
     * Constructor.
     *
     * @see StaticContentView
     * @see com.vaadin.flow.component.orderedlayout.VerticalLayout
     * @see com.vaadin.flow.component.html.IFrame
     */
    public PmodeOverview() {
        super("documentation/ui/pmodes/pmodes_overview.html");
    }
}
