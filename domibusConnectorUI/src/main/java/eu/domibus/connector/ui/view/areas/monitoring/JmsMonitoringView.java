package eu.domibus.connector.ui.view.areas.monitoring;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.controller.QueueController;
import eu.domibus.connector.ui.dto.WebQueue;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = JmsMonitoringView.ROUTE, layout = MonitoringLayout.class)
@Order(1)
@TabMetadata(title = "Jms Queues", tabGroup = MonitoringLayout.TAB_GROUP_NAME)
public class JmsMonitoringView extends DCVerticalLayoutWithTitleAndHelpButton implements AfterNavigationObserver {
    public static final String ROUTE = "queues";
    public static final String HELP_ID = "ui/monitoring/jms_monitoring.html";
    public static final String TITLE = "Jms Queues";

    private final QueueController queueController;
    private final QueueGrid queueGrid;

    public JmsMonitoringView(QueueController queueController) {
        super(HELP_ID, TITLE);
        this.queueController = queueController;
        queueGrid = new QueueGrid();
        queueGrid.setItemDetailsRenderer(createDetailsRenderer());
        final VerticalLayout gridLayoutCotainer = new VerticalLayout(queueGrid);
        add(gridLayoutCotainer);
    }

    private ComponentRenderer<DetailsLayout, WebQueue> createDetailsRenderer() {
        return new ComponentRenderer<>(
                () -> new DetailsLayout(queueController, this),
                DetailsLayout::setData
        );
    }

    void updateData(WebQueue select) {
        queueGrid.setItems(queueController.getQueues());
        if (select != null) queueGrid.select(select);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        updateData(null);
    }
}
