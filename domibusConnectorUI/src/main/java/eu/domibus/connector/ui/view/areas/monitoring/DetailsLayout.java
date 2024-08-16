/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.monitoring;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import eu.domibus.connector.ui.controller.QueueController;
import eu.domibus.connector.ui.dto.WebQueue;

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
        final var messageLabel = new Label("Messages on Queue");
        final var dlqMessageLabel = new Label("Messages on DLQ");
        messageGrid = new MessageGrid(this.queueController, view);
        dlqMessagesGrid = new MessageGrid(this.queueController, view);
        add(messageLabel, messageGrid, dlqMessageLabel, dlqMessagesGrid);
    }

    void setData(WebQueue queue) {
        messageGrid.setData(queue.getMessages(), queue);
        dlqMessagesGrid.setData(queue.getDlqMessages(), queue);
    }
}
