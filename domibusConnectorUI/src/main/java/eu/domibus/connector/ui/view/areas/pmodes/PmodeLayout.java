/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.pmodes;

import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * The PmodeLayout class represents the layout for the PMode tab in the Domibus Connector
 * Administration UI.
 *
 * @see DCVerticalLayoutWithTabs
 * @see UIScope
 * @see org.springframework.stereotype.Component
 * @see RoutePrefix
 * @see ParentLayout
 */
@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(PmodeLayout.ROUTE_PREFIX)
@ParentLayout(DCMainLayout.class)
public class PmodeLayout extends DCVerticalLayoutWithTabs {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PmodeLayout.class);
    public static final String ROUTE_PREFIX = "pmode";
    public static final String TAB_GROUP_NAME = "Pmode";

    /**
     * Constructor.
     *
     * @param applicationContext The ApplicationContext used for creating the tabs. Must not be
     *                           null.
     */
    public PmodeLayout(ApplicationContext applicationContext) {
        super(TAB_GROUP_NAME, applicationContext);
    }
}
