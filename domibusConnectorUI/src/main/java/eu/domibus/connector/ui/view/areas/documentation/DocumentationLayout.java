/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.documentation;

import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTabs;
import org.springframework.context.ApplicationContext;

/**
 * The DocumentationLayout class represents a layout component for displaying documentation.
 *
 * @see DCVerticalLayoutWithTabs
 * @see UIScope
 * @see org.springframework.stereotype.Component
 * @see RoutePrefix
 * @see ParentLayout
 */
@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(DocumentationLayout.ROUTE_PREFIX)
@ParentLayout(DCMainLayout.class)
public class DocumentationLayout extends DCVerticalLayoutWithTabs {
    public static final String ROUTE_PREFIX = "documentation";
    public static final String TAB_GROUP_NAME = "Documentation";

    /**
     * Constructor.
     *
     * @param applicationContext The ApplicationContext used for creating the tabs. Must not be
     *                           null.
     */
    public DocumentationLayout(ApplicationContext applicationContext) {
        super(TAB_GROUP_NAME, applicationContext);
    }
}
