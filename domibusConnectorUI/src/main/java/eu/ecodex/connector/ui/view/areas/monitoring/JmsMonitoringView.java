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

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.controller.QueueController;
import eu.ecodex.connector.ui.dto.WebQueue;
import eu.ecodex.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This class represents the Jms Monitoring view in the application.
 * It is responsible for displaying and managing Jms queues.
 *
 * @see Component
 * @see UIScope
 * @see Route
 * @see Order
 * @see TabMetadata
 * @see DCVerticalLayoutWithTitleAndHelpButton
 * @see AfterNavigationObserver
 *
 * @since 1.0
 */
@Component
@UIScope
@Route(value = JmsMonitoringView.ROUTE, layout = MonitoringLayout.class)
@Order(1)
@TabMetadata(title = "Jms Queues", tabGroup = MonitoringLayout.TAB_GROUP_NAME)
public class JmsMonitoringView extends DCVerticalLayoutWithTitleAndHelpButton
    implements AfterNavigationObserver {
    public static final String ROUTE = "queues";
    public static final String HELP_ID = "ui/monitoring/jms_monitoring.html";
    public static final String TITLE = "Jms Queues";
    private final QueueController queueController;
    private final QueueGrid queueGrid;

    /**
     * Constructor.
     *
     * @param queueController the QueueController used to manage queues and messages
     * @see QueueController
     */
    public JmsMonitoringView(QueueController queueController) {
        super(HELP_ID, TITLE);
        this.queueController = queueController;
        queueGrid = new QueueGrid();
        queueGrid.setItemDetailsRenderer(createDetailsRenderer());
        final var gridLayoutContainer = new VerticalLayout(queueGrid);
        add(gridLayoutContainer);
    }

    private ComponentRenderer<DetailsLayout, WebQueue> createDetailsRenderer() {
        return new ComponentRenderer<>(
            () -> new DetailsLayout(queueController, this),
            DetailsLayout::setData
        );
    }

    void updateData(WebQueue select) {
        queueGrid.setItems(queueController.getQueues());
        if (select != null) {
            queueGrid.select(select);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        updateData(null);
    }
}
