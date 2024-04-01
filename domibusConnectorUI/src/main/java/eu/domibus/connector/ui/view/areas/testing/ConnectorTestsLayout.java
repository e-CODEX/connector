package eu.domibus.connector.ui.view.areas.testing;

import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTabs;
import eu.domibus.connector.ui.view.areas.pmodes.PmodeLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;


@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(ConnectorTestsLayout.ROUTE_PREFIX)
@ParentLayout(DCMainLayout.class)
public class ConnectorTestsLayout extends DCVerticalLayoutWithTabs {
    public static final String ROUTE_PREFIX = "c2ctests";
    public static final String TAB_GROUP_NAME = "ConnectorTests";
    protected final static Logger LOGGER = LoggerFactory.getLogger(PmodeLayout.class);

    public ConnectorTestsLayout(ApplicationContext applicationContext) {
        super(TAB_GROUP_NAME, applicationContext);
    }
}
