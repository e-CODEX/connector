package eu.domibus.connector.ui.view.areas.pmodes;

import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;


@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(PmodeLayout.ROUTE_PREFIX)
@ParentLayout(DCMainLayout.class)
public class PmodeLayout extends DCVerticalLayoutWithTabs {
    public static final String ROUTE_PREFIX = "pmode";
    public static final String TAB_GROUP_NAME = "Pmode";
    protected final static Logger LOGGER = LoggerFactory.getLogger(PmodeLayout.class);

    public PmodeLayout(ApplicationContext applicationContext) {
        super(TAB_GROUP_NAME, applicationContext);
    }
}
