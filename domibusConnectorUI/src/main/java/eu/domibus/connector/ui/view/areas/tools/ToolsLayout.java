package eu.domibus.connector.ui.view.areas.tools;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.utils.DCTabHandler;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Objects;


@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(ToolsLayout.ROUTE)
@ParentLayout(DCMainLayout.class)
public class ToolsLayout extends VerticalLayout implements BeforeEnterObserver, RouterLayout {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLayout.class);
    public static final String ROUTE = "tools";
    public static final String TAB_GROUP_NAME = "Tools";

    private final ApplicationContext applicationContext;
    private Div pageContent;
    private final eu.domibus.connector.ui.utils.DCTabHandler DCTabHandler = new DCTabHandler();

    public ToolsLayout(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        DCTabHandler.createTabs(applicationContext, TAB_GROUP_NAME);

        pageContent = new Div();
        pageContent.setSizeFull();

        add(DCTabHandler.getTabs(), pageContent);

        this.expand(pageContent);
        this.setHeight("80vh");
    }

    public void showRouterLayoutContent(HasElement content) {
        if (content != null) {
            pageContent.getElement().appendChild(Objects.requireNonNull(content.getElement()));
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        DCTabHandler.beforeEnter(event);
    }
}
