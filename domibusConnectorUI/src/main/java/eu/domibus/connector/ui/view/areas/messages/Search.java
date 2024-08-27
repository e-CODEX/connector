/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.messages;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.component.WebMessagesGrid;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;
import eu.domibus.connector.ui.service.WebMessageService;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import jakarta.annotation.PostConstruct;
import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The {@code Search} class represents a component for searching messages.
 */
@Component
@UIScope
@Route(value = Search.ROUTE, layout = MessageLayout.class)
@Order(2)
@TabMetadata(title = "Search", tabGroup = MessageLayout.TAB_GROUP_NAME)
public class Search extends VerticalLayout {
    public static final String ROUTE = "search";
    private static final long serialVersionUID = 1L;
    private final MessageDetails details;
    private final WebMessageService messageService;
    private final DomibusConnectorWebMessagePersistenceService dcMessagePersistenceService;
    TextField searchMessageIdText = new TextField();
    TextField searchEbmsIdText = new TextField();
    TextField searchBackendMessageIdText = new TextField();
    TextField searchConversationIdText = new TextField();
    private final VerticalLayout main = new VerticalLayout();

    /**
     * The Search class is responsible for performing search operations on web messages in the
     * Domibus Connector system.
     *
     * @param messageService              a WebMessageService instance used to retrieve and
     *                                    manipulate web messages
     * @param details                     a MessageDetails object containing details about the
     *                                    search criteria
     * @param dcMessagePersistenceService a DomibusConnectorWebMessagePersistenceService instance
     *                                    used to perform persistence operations on web messages
     */
    public Search(
        WebMessageService messageService, MessageDetails details,
        DomibusConnectorWebMessagePersistenceService dcMessagePersistenceService) {
        this.messageService = messageService;
        this.details = details;
        this.dcMessagePersistenceService = dcMessagePersistenceService;
    }

    @PostConstruct
    void init() {
        var connectorMessageIdSearch = new HorizontalLayout();

        searchMessageIdText.setPlaceholder("Search by Connector Message ID");
        searchMessageIdText.setWidth(UiStyle.WIDTH_300_PX);
        connectorMessageIdSearch.add(searchMessageIdText);

        var searchConnectorMessageIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
        searchConnectorMessageIdBtn.addClickListener(
            e -> searchByConnectorMessageId(searchMessageIdText.getValue()));
        connectorMessageIdSearch.add(searchConnectorMessageIdBtn);

        add(connectorMessageIdSearch);

        var ebmsIdSearch = new HorizontalLayout();

        searchEbmsIdText.setPlaceholder("Search by EBMS Message ID");
        searchEbmsIdText.setWidth(UiStyle.WIDTH_300_PX);
        ebmsIdSearch.add(searchEbmsIdText);

        var searchEbmsIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
        searchEbmsIdBtn.addClickListener(e -> searchByEbmsId(searchEbmsIdText.getValue()));
        ebmsIdSearch.add(searchEbmsIdBtn);

        add(ebmsIdSearch);

        var backendMessageIdSearch = new HorizontalLayout();

        searchBackendMessageIdText.setPlaceholder("Search by Backend Message ID");
        searchBackendMessageIdText.setWidth("300px");
        backendMessageIdSearch.add(searchBackendMessageIdText);

        var searchBackendMessageIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
        searchBackendMessageIdBtn.addClickListener(
            e -> searchByBackendMessageId(searchBackendMessageIdText.getValue()));
        backendMessageIdSearch.add(searchBackendMessageIdBtn);

        add(backendMessageIdSearch);

        var conversationIdSearch = new HorizontalLayout();

        searchConversationIdText.setPlaceholder("Search by Conversation ID");
        searchConversationIdText.setWidth("300px");
        conversationIdSearch.add(searchConversationIdText);

        var searchConversationIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
        searchConversationIdBtn.addClickListener(
            e -> searchByConversationId(searchConversationIdText.getValue()));
        conversationIdSearch.add(searchConversationIdBtn);

        add(conversationIdSearch);

        add(main);

        setHeight("100vh");
        setWidth("100vw");
    }

    private void addGridWithData(WebMessage example) {
        main.removeAll();

        var messagesGrid = new WebMessagesGrid(details, dcMessagePersistenceService, example);
        messagesGrid.reloadList();
        messagesGrid.setVisible(true);

        main.add(messagesGrid);
        main.setAlignItems(Alignment.STRETCH);
        main.setWidth("100vw");
    }

    private void searchByBackendMessageId(String backendMessageId) {
        Optional<WebMessage> messageByBackendMessageId =
            messageService.getMessageByBackendMessageId(backendMessageId);
        searchBackendMessageIdText.setValue("");
        messageByBackendMessageId.ifPresent(details::show);
    }

    private void searchByEbmsId(String ebmsId) {
        Optional<WebMessage> messageByEbmsId = messageService.getMessageByEbmsId(ebmsId);
        searchEbmsIdText.setValue("");
        messageByEbmsId.ifPresent(details::show);
    }

    private void searchByConversationId(String conversationId) {
        var webMessage = new WebMessage();
        webMessage.setConversationId(conversationId);
        addGridWithData(webMessage);
    }

    private void searchByConnectorMessageId(String connectorMessageId) {
        Optional<WebMessage> messageByConnectorId =
            messageService.getMessageByConnectorId(connectorMessageId);
        searchMessageIdText.setValue("");
        messageByConnectorId.ifPresent(details::show);
    }
}
