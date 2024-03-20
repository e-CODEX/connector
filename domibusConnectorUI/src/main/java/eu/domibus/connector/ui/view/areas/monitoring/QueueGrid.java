package eu.domibus.connector.ui.view.areas.monitoring;

import com.vaadin.flow.component.grid.Grid;
import eu.domibus.connector.ui.dto.WebQueue;

public class QueueGrid extends Grid<WebQueue> {

    public QueueGrid() {
        super();

        this.setWidth("100%");
        this.setHeightByRows(true);

        addColumn(WebQueue::getName).setHeader("Queue").setWidth("40%");
        addColumn(WebQueue::getMsgsOnQueue).setHeader("Messages on Queue").setWidth("30%");
        addColumn(WebQueue::getMsgsOnDlq).setHeader("Messages on Error Queue").setWidth("30%");
    }


}
