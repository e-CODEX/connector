package eu.domibus.connector.ui.view.areas.monitoring;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import eu.domibus.connector.ui.controller.QueueController;
import eu.domibus.connector.ui.dto.WebQueue;


public class DetailsLayout extends VerticalLayout {
    private final QueueController queueController;
    JmsMonitoringView parentView;
    private final MessageGrid msgsGrid;
    private final MessageGrid dlqMsgsGrid;

    public DetailsLayout(QueueController queueController, JmsMonitoringView view) {
        this.queueController = queueController;
        this.parentView = view;
        this.setWidth("100%");
        final Label msgsLabel = new Label("Messages on Queue");
        final Label dlqLabel = new Label("Messages on DLQ");
        msgsGrid = new MessageGrid(this.queueController, view);
        dlqMsgsGrid = new MessageGrid(this.queueController, view);
        add(msgsLabel, msgsGrid, dlqLabel, dlqMsgsGrid);
    }

    void setData(WebQueue queue) {
        msgsGrid.setData(queue.getMessages(), queue);
        dlqMsgsGrid.setData(queue.getDlqMessages(), queue);
    }
}
