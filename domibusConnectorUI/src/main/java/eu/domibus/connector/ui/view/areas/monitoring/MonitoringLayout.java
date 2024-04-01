package eu.domibus.connector.ui.view.areas.monitoring;

import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTabs;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


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
