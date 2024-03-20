package eu.domibus.connector.ui.view.areas.pmodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTabs;
import eu.domibus.connector.ui.layout.DCMainLayout;

@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(PmodeLayout.ROUTE_PREFIX)
@ParentLayout(DCMainLayout.class)
public class PmodeLayout extends DCVerticalLayoutWithTabs {

    protected final static Logger LOGGER = LoggerFactory.getLogger(PmodeLayout.class);

    public static final String ROUTE_PREFIX = "pmode";
    public static final String TAB_GROUP_NAME = "Pmode";

    public PmodeLayout(ApplicationContext applicationContext )
    {
    	super(TAB_GROUP_NAME, applicationContext);
    }

}