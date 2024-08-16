/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.layout;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import eu.domibus.connector.ui.utils.DCTabHandler;
import eu.domibus.connector.ui.view.areas.pmodes.PmodeLayout;
import javax.annotation.PostConstruct;
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
