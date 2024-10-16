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

import com.vaadin.flow.component.grid.Grid;
import eu.ecodex.connector.ui.dto.WebQueue;

/**
 * The QueueGrid class represents a grid that displays information about web queues.
 *
 * @see Grid
 * @see WebQueue
 * @see WebQueue#getName()
 * @see WebQueue#getMsgsOnQueue()
 * @see WebQueue#getMsgsOnDlq()
 */
public class QueueGrid extends Grid<WebQueue> {
    /**
     * Constructor.
     */
    public QueueGrid() {
        super();

        this.setWidth("100%");
        this.setAllRowsVisible(true);

        addColumn(WebQueue::getName).setHeader("Queue").setWidth("40%");
        addColumn(WebQueue::getMsgsOnQueue).setHeader("Messages on Queue").setWidth("30%");
        addColumn(WebQueue::getMsgsOnDlq).setHeader("Messages on Error Queue").setWidth("30%");
    }
}
