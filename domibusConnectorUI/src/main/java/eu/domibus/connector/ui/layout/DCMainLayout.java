package eu.domibus.connector.ui.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.login.LogoutView;
import eu.domibus.connector.ui.utils.DCTabHandler;
import eu.domibus.connector.ui.view.DomibusConnectorAdminHeader;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationOverview;
import eu.domibus.connector.ui.view.areas.documentation.ArchitectureDocumentationView;
import eu.domibus.connector.ui.view.areas.info.Info;
import eu.domibus.connector.ui.view.areas.messages.MessageOverview;
import eu.domibus.connector.ui.view.areas.monitoring.JmsMonitoringView;
import eu.domibus.connector.ui.view.areas.pmodes.PmodeOverview;
import eu.domibus.connector.ui.view.areas.testing.ConnectorTestsOverview;
import eu.domibus.connector.ui.view.areas.tools.ToolsView;
import eu.domibus.connector.ui.view.areas.users.UserOverview;


@UIScope
@org.springframework.stereotype.Component
@Push
public class DCMainLayout extends AppLayout implements RouterLayout, BeforeEnterObserver {
    private final Tabs tabs;
    private final DCTabHandler tabManager = new DCTabHandler();

    public DCMainLayout(DomibusConnectorAdminHeader header) {
        setPrimarySection(Section.DRAWER);
        VerticalLayout topBar = new VerticalLayout();
        topBar.add(header);
        addToNavbar(topBar);

        tabManager.setTabFontSize("bigger");
        tabManager
                .createTab()
                .withLabel("Messages")
                .withIcon(new Icon(VaadinIcon.LIST))
                .addForComponent(MessageOverview.class);

        tabManager
                .createTab()
                .withLabel("PModes")
                .withIcon(new Icon(VaadinIcon.FILE_CODE))
                .addForComponent(PmodeOverview.class);

        tabManager
                .createTab()
                .withLabel("Configuration")
                .withIcon(new Icon(VaadinIcon.COG_O))
                .addForComponent(ConfigurationOverview.class);

        tabManager
                .createTab()
                .withLabel("Monitoring")
                .withIcon(new Icon(VaadinIcon.DASHBOARD))
                .addForComponent(JmsMonitoringView.class);

        tabManager
                .createTab()
                .withLabel("Users")
                .withIcon(new Icon(VaadinIcon.USERS))
                .addForComponent(UserOverview.class);

        tabManager
                .createTab()
                .withLabel("Connector Tests")
                .withIcon(new Icon(VaadinIcon.MAILBOX))
                .addForComponent(ConnectorTestsOverview.class);

        tabManager
                .createTab()
                .withLabel("Info")
                .withIcon(VaadinIcon.INFO_CIRCLE_O)
                .addForComponent(Info.class);

        tabManager
                .createTab()
                .withLabel("Tools")
                .withIcon(VaadinIcon.PLAY)
                .addForComponent(ToolsView.class);

        tabManager
                .createTab()
                .withLabel("Documentation")
                .withIcon(VaadinIcon.LIGHTBULB)
                .addForComponent(ArchitectureDocumentationView.class);

        tabManager
                .createTab()
                .withLabel("Logout")
                .withIcon(new Icon(VaadinIcon.ARROW_RIGHT))
                .addForComponent(LogoutView.class);

        tabs = tabManager.getTabs();
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);

        topBar.add(tabs);
    }

    public void beforeEnter(BeforeEnterEvent event) {
        tabManager.beforeEnter(event);
    }
}
