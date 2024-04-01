package eu.domibus.connector.ui.view.areas.messages;

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
@RoutePrefix(MessageLayout.ROUTE_PREFIX)
@ParentLayout(DCMainLayout.class)
public class MessageLayout extends DCVerticalLayoutWithTabs {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MessageLayout.class);
    public static final String ROUTE_PREFIX = "message";
    public static final String TAB_GROUP_NAME = "Message";

    public MessageLayout(ApplicationContext applicationContext) {
        super(TAB_GROUP_NAME, applicationContext);
    }
}
