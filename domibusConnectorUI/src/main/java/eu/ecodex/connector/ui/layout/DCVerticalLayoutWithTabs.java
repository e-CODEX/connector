/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.layout;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import eu.ecodex.connector.ui.utils.DCTabHandler;
import eu.ecodex.connector.ui.view.areas.pmodes.PmodeLayout;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * The DCVerticalLayoutWithTabs class represents a vertical layout component with tabs. It extends
 * the VerticalLayout class and implements the BeforeEnterObserver and RouterLayout interfaces.
 *
 * @see VerticalLayout
 * @see BeforeEnterObserver
 * @see RouterLayout
 */
@SuppressWarnings("checkstyle:MemberName")
public class DCVerticalLayoutWithTabs extends VerticalLayout
    implements BeforeEnterObserver, RouterLayout {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PmodeLayout.class);
    private final String TAB_GROUP_NAME;
    private final ApplicationContext applicationContext;
    private final DCTabHandler DCTabHandler = new DCTabHandler();

    /**
     * This class represents a vertical layout with tabs. It extends the VerticalLayout class and
     * implements the BeforeEnterObserver and RouterLayout interfaces.
     *
     * @param tAB_GROUP_NAME     The name of the tab group.
     * @param applicationContext The application context used for creating the tabs.
     * @see VerticalLayout
     * @see BeforeEnterObserver
     * @see RouterLayout
     */
    @SuppressWarnings("checkstyle:ParameterName")
    public DCVerticalLayoutWithTabs(String tAB_GROUP_NAME, ApplicationContext applicationContext) {
        super();
        TAB_GROUP_NAME = tAB_GROUP_NAME;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    void init() {

        setSizeFull();

        DCTabHandler.createTabs(applicationContext, TAB_GROUP_NAME);
        add(DCTabHandler.getTabs());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        DCTabHandler.beforeEnter(event);
    }
}
