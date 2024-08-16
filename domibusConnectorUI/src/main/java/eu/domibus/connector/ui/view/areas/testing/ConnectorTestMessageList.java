/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.testing;

import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.common.DomibusConnectorDefaults;
import eu.domibus.connector.ui.component.WebMessagesGrid;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithWebMessageGrid;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;
import eu.domibus.connector.ui.service.WebConnectorTestService;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.messages.MessageDetails;
import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The ConnectorTestMessageList class represents a UI component that displays a list of connector
 * test messages.
 *
 * @see DCVerticalLayoutWithTitleAndHelpButton
 * @see AfterNavigationObserver
 */
@Component
@UIScope
@Route(value = ConnectorTestMessageList.ROUTE, layout = ConnectorTestsLayout.class)
@Order(1)
@TabMetadata(title = "Connector Test Messages List", tabGroup = ConnectorTestsLayout.TAB_GROUP_NAME)
@SuppressWarnings("squid:S1135")
public class ConnectorTestMessageList extends DCVerticalLayoutWithTitleAndHelpButton
    implements AfterNavigationObserver {
    public static final String ROUTE = "c2cmessages";
    public static final String TITLE = "Connector Test Messages";
    public static final String HELP_ID = "ui/c2ctests/connector_test_messages_list.html";
    private String connectorTestBackendName = DomibusConnectorDefaults.DEFAULT_TEST_BACKEND;
    WebMessagesGrid grid;

    /**
     * Constructor.
     *
     * @param details                     The MessageDetails object used for displaying the details
     *                                    of a message in the UI.
     * @param testService                 An optional WebConnectorTestService object used for
     *                                    obtaining the connector test backend name.
     * @param dcMessagePersistenceService The DomibusConnectorWebMessagePersistenceService object
     *                                    used for interacting with the persistence layer to
     *                                    retrieve connector test messages .
     */
    public ConnectorTestMessageList(
        MessageDetails details,
        Optional<WebConnectorTestService> testService,
        DomibusConnectorWebMessagePersistenceService dcMessagePersistenceService) {
        super(HELP_ID, TITLE);

        if (testService.isPresent()) {
            connectorTestBackendName = testService.get().getConnectorTestBackendName();
        }
        var example = new WebMessage();
        example.setBackendName(connectorTestBackendName);

        grid = new WebMessagesGrid(details, dcMessagePersistenceService, example);

        var gridLayout = new DCVerticalLayoutWithWebMessageGrid(grid);

        gridLayout.setVisible(true);
        gridLayout.setHeight("100vh");
        add(gridLayout);
        setSizeFull();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent arg0) {
        // grid.reloadList();
        // TODO see why this method body is empty.
    }
}
