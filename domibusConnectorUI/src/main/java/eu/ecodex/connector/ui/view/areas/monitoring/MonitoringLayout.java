/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.monitoring;

import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.layout.DCMainLayout;
import eu.ecodex.connector.ui.layout.DCVerticalLayoutWithTabs;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The MonitoringLayout class represents the layout for the Monitoring section of the application.
 */
@UIScope
@Component
@RoutePrefix(MonitoringLayout.ROUTE_PREFIX)
@ParentLayout(DCMainLayout.class)
public class MonitoringLayout extends DCVerticalLayoutWithTabs {
    public static final String ROUTE_PREFIX = "monitoring";
    public static final String TAB_GROUP_NAME = "Monitoring";

    public MonitoringLayout(ApplicationContext applicationContext) {
        super(TAB_GROUP_NAME, applicationContext);
    }
}
