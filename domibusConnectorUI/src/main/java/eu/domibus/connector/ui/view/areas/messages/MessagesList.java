package eu.domibus.connector.ui.view.areas.messages;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.component.WebMessagesGrid;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithWebMessageGrid;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;
import eu.domibus.connector.ui.service.WebMessageService;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = MessagesList.ROUTE, layout = MessageLayout.class)
@Order(1)
@TabMetadata(title = "All Messages", tabGroup = MessageLayout.TAB_GROUP_NAME)
public class MessagesList extends VerticalLayout implements AfterNavigationObserver {
    public static final String ROUTE = "messagelist";

    private final WebMessagesGrid grid;

    public MessagesList(
            WebMessageService messageService,
            DomibusConnectorWebMessagePersistenceService messagePersistenceService,
            MessageDetails details) {

        grid = new WebMessagesGrid(details, messagePersistenceService, new WebMessage());

        DCVerticalLayoutWithWebMessageGrid gridLayout = new DCVerticalLayoutWithWebMessageGrid(grid);

        gridLayout.setVisible(true);
        gridLayout.setHeight("100vh");
        add(gridLayout);
        setSizeFull();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // grid.reloadList();
    }
}
