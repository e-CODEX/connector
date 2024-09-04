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

import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import eu.ecodex.connector.ui.controller.QueueController;
import eu.ecodex.connector.ui.dto.WebQueue;

/**
 * The DetailsLayout class is a layout that displays detailed information about a specific queue.
 *
 * <p>This layout is typically used within the JmsMonitoringView class to display additional
 * information about a selected queue.
 *
 * @see VerticalLayout
 * @see JmsMonitoringView
 */
public class DetailsLayout extends VerticalLayout {
    private final QueueController queueController;
    JmsMonitoringView parentView;
    private final MessageGrid messageGrid;
    private final MessageGrid dlqMessagesGrid;

    /**
     * Constructor.
     *
     * @param queueController The {@link QueueController} object that controls the queues and
     *                        messages.
     * @param view            The {@link JmsMonitoringView} object that contains the DetailsLayout.
     * @see VerticalLayout
     * @see JmsMonitoringView
     */
    public DetailsLayout(QueueController queueController, JmsMonitoringView view) {
        this.queueController = queueController;
        this.parentView = view;
        this.setWidth("100%");
        final var messageLabel = new NativeLabel("Messages on Queue");
        final var dlqMessageLabel = new NativeLabel("Messages on DLQ");
        messageGrid = new MessageGrid(this.queueController, view);
        dlqMessagesGrid = new MessageGrid(this.queueController, view);
        add(messageLabel, messageGrid, dlqMessageLabel, dlqMessagesGrid);
    }

    void setData(WebQueue queue) {
        messageGrid.setData(queue.getMessages(), queue);
        dlqMessagesGrid.setData(queue.getDlqMessages(), queue);
    }
}
