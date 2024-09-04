/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.messages;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.component.WebMessagesGrid;
import eu.ecodex.connector.ui.dto.WebMessage;
import eu.ecodex.connector.ui.layout.DCVerticalLayoutWithWebMessageGrid;
import eu.ecodex.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;
import eu.ecodex.connector.ui.service.WebMessageService;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The {@code MessagesList} class represents a list component for displaying messages.
 */
@Component
@UIScope
@Route(value = MessagesList.ROUTE, layout = MessageLayout.class)
@Order(1)
@TabMetadata(title = "All Messages", tabGroup = MessageLayout.TAB_GROUP_NAME)
@SuppressWarnings("squid:S1135")
public class MessagesList extends VerticalLayout implements AfterNavigationObserver {
    public static final String ROUTE = "messagelist";
    private final WebMessagesGrid grid;

    /**
     * Constructor.
     *
     * @param messageService            The WebMessageService used to retrieve and manipulate web
     *                                  messages.
     * @param messagePersistenceService The DomibusConnectorWebMessagePersistenceService used to
     *                                  interact with the persistence for web messages.
     * @param details                   The MessageDetails object used to configure the message
     *                                  grid.
     */
    public MessagesList(
        WebMessageService messageService,
        DomibusConnectorWebMessagePersistenceService messagePersistenceService,
        MessageDetails details) {

        grid = new WebMessagesGrid(details, messagePersistenceService, new WebMessage());

        var gridLayout = new DCVerticalLayoutWithWebMessageGrid(grid);
        gridLayout.setVisible(true);
        gridLayout.setHeight("100vh");
        add(gridLayout);
        setSizeFull();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // TODO see why the method body is empty
        // grid.reloadList();
    }
}
