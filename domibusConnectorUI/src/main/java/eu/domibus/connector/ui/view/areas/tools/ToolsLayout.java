/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.tools;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.utils.DCTabHandler;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import java.util.Objects;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * The {@code ToolsLayout} class represents the layout for the Tools section in the Domibus
 * Connector Administration UI.
 *
 * @see VerticalLayout
 * @see BeforeEnterObserver
 * @see RouterLayout
 */
@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(ToolsLayout.ROUTE)
@ParentLayout(DCMainLayout.class)
public class ToolsLayout extends VerticalLayout implements BeforeEnterObserver, RouterLayout {
    public static final String ROUTE = "tools";
    public static final String TAB_GROUP_NAME = "Tools";
    protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLayout.class);
    private final ApplicationContext applicationContext;
    private Div pageContent;
    private final eu.domibus.connector.ui.utils.DCTabHandler dcTabHandler = new DCTabHandler();

    /**
     * Constructor.
     *
     * @see VerticalLayout
     * @see BeforeEnterObserver
     * @see RouterLayout
     */
    public ToolsLayout(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Initializes the ToolsLayout component.
     */
    @PostConstruct
    public void init() {
        dcTabHandler.createTabs(applicationContext, TAB_GROUP_NAME);

        pageContent = new Div();
        pageContent.setSizeFull();

        add(dcTabHandler.getTabs(), pageContent);

        this.expand(pageContent);
        this.setHeight("80vh");
    }

    /**
     * Adds the specified content to the page content area of the ToolsLayout.
     *
     * @param content the content to be added to the layout
     */
    @Override
    public void showRouterLayoutContent(HasElement content) {
        if (content != null) {
            pageContent.getElement().appendChild(Objects.requireNonNull(content.getElement()));
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        dcTabHandler.beforeEnter(event);
    }
}
