package eu.domibus.connector.ui.view.areas.users;

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
@RoutePrefix(UserLayout.ROUTE_PREFIX)
@ParentLayout(DCMainLayout.class)
public class UserLayout extends DCVerticalLayoutWithTabs {
    public static final String ROUTE_PREFIX = "user";
    public static final String TAB_GROUP_NAME = "User";
    protected static final Logger LOGGER = LoggerFactory.getLogger(UserLayout.class);

    public UserLayout(ApplicationContext applicationContext) {
        super(TAB_GROUP_NAME, applicationContext);
    }
}
