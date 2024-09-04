/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.tools;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.layout.DCMainLayout;
import eu.ecodex.connector.ui.utils.DCTabHandler;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationLayout;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
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
    private final DCTabHandler dcTabHandler = new DCTabHandler();

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
