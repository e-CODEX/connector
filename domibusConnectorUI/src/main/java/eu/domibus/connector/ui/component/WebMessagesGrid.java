/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.event.SortEvent;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;
import eu.domibus.connector.ui.view.areas.messages.MessageDetails;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.firitin.components.grid.PagingGrid;

/**
 * The WebMessagesGrid class represents a grid component used to display a list of web messages. It
 * extends the PaginatedGrid class and implements the AfterNavigationObserver interface.
 */
@Data
public class WebMessagesGrid extends PagingGrid<WebMessage> implements AfterNavigationObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebMessagesGrid.class);
    public static final String WIDTH_450_PX = "450px";
    public static final String WIDTH_300_PX = "300px";
    public static final String WIDTH_150_PX = "150px";
    public static final String TAG_CREATED = "created";
    // collect all hideable columns, to be iterated over later.
    private Map<String, Column<WebMessage>> hideableColumns = new HashMap<>();
    private final MessageDetails details;
    private final DomibusConnectorWebMessagePersistenceService dcMessagePersistenceService;
    WebMessage exampleWebMessage;
    Page<WebMessage> currentPage;
    CallbackDataProvider<WebMessage, WebMessage> callbackDataProvider;

    /**
     * Constructor.
     *
     * @param details                     the MessageDetails object used for displaying message
     *                                    details
     * @param dcMessagePersistenceService the DomibusConnectorWebMessagePersistence service used for
     *                                    retrieving and manipulating web messages
     * @param exampleWebMessage           the example web message used for creating a filter
     */
    public WebMessagesGrid(
        MessageDetails details,
        DomibusConnectorWebMessagePersistenceService dcMessagePersistenceService,
        WebMessage exampleWebMessage) {
        super();
        this.details = details;
        this.dcMessagePersistenceService = dcMessagePersistenceService;
        this.exampleWebMessage = exampleWebMessage;
        addAllColumns();

        for (Column<WebMessage> col : getColumns()) {
            col.setResizable(true);
        }

        setMultiSort(false);
        setWidth("100%");
        addSortListener(this::handleSortEvent);
    }

    private void addAllColumns() {
        addComponentColumn(this::geMessageDetailsLink).setHeader("Details").setWidth("30px");
        addColumn(
            webMessage -> webMessage.getMessageInfo() != null
                && webMessage.getMessageInfo().getFrom() != null
                ? webMessage.getMessageInfo().getFrom().getPartyString()
                : ""
        )
            .setHeader("From Party").setWidth("70px").setKey("messageInfo.from.partyId")
            .setSortable(true);
        addColumn(
            webMessage ->
                webMessage.getMessageInfo() != null && webMessage.getMessageInfo().getTo() != null
                    ? webMessage.getMessageInfo().getTo().getPartyString()
                    : ""
        )
            .setHeader("To Party").setWidth("70px").setKey("messageInfo.to.partyId")
            .setSortable(true);

        addHideableColumn(
            WebMessage::getConnectorMessageId,
            "Connector Message ID",
            WIDTH_450_PX,
            "connectorMessageId",
            false,
            true
        );
        addHideableColumn(
            WebMessage::getEbmsMessageId,
            "ebMS Message ID",
            WIDTH_450_PX,
            "ebmsMessageId",
            false,
            false
        );
        addHideableColumn(
            WebMessage::getBackendMessageId,
            "Backend Message ID",
            WIDTH_450_PX,
            "backendMessageId",
            false,
            false
        );
        addHideableColumn(
            WebMessage::getConversationId,
            "Conversation ID",
            WIDTH_450_PX,
            "conversationId",
            false,
            false
        );
        addHideableColumn(
            webMessage -> webMessage.getMessageInfo() != null
                ? webMessage.getMessageInfo().getOriginalSender()
                : "",
            "Original sender",
            WIDTH_300_PX,
            "messageInfo.originalSender",
            true,
            false
        );
        addHideableColumn(
            webMessage -> webMessage.getMessageInfo() != null
                ? webMessage.getMessageInfo().getFinalRecipient()
                : "",
            "Final recipient", WIDTH_300_PX, "messageInfo.finalRecipient", true, false
        );
        addHideableColumn(
            webMessage -> webMessage.getMessageInfo() != null
                && webMessage.getMessageInfo().getService() != null
                ? webMessage.getMessageInfo().getService().getServiceString()
                : "",
            "Service", WIDTH_150_PX, "messageInfo.service.service", true, true
        );
        addHideableColumn(
            webMessage -> webMessage.getMessageInfo() != null
                && webMessage.getMessageInfo().getAction() != null
                ? webMessage.getMessageInfo().getAction().getAction()
                : "",
            "Action", WIDTH_150_PX, "messageInfo.action.action", true, true
        );
        addHideableColumn(
            WebMessage::getBackendName, "backend name", WIDTH_150_PX, "backendName", true, true);
        addHideableColumn(
            WebMessage::getDirection, "direction", WIDTH_150_PX, "direction", false, false);
        addHideableColumn(
            WebMessage::getDeliveredToNationalSystem, "delivered backend", WIDTH_300_PX,
            "deiliveredToNationalSystem", true, false
        );
        addHideableColumn(
            WebMessage::getDeliveredToGateway, "delivered gateway", WIDTH_300_PX,
            "deliveredToGateway",
            true, false
        );
        addHideableColumn(
            WebMessage::getCreated, TAG_CREATED, WIDTH_300_PX, TAG_CREATED, true, true
        );
        addHideableColumn(
            WebMessage::getConfirmed, "confirmed", WIDTH_300_PX, "confirmed", true, false
        );
        addHideableColumn(
            WebMessage::getRejected, "rejected", WIDTH_300_PX, "rejected", true, false
        );
    }

    private void addHideableColumn(
        ValueProvider<WebMessage, ?> valueProvider, String header, String width, String key,
        boolean sortable, boolean visible) {
        var column = addColumn(valueProvider).setHeader(header).setKey(key).setWidth(width)
                                             .setSortable(sortable);
        column.setVisible(visible);
        hideableColumns.put(header, column);
    }

    public Set<String> getHideableColumnNames() {
        return hideableColumns.keySet();
    }

    /**
     * Creates a button that when clicked, shows the details of the given connector message.
     *
     * @param connectorMessage the connector message for which to show the details
     * @return a Button object that, when clicked, shows the details of the given connector message
     */
    public Button geMessageDetailsLink(WebMessage connectorMessage) {
        var getDetails = new Button(new Icon(VaadinIcon.SEARCH));
        getDetails.addClickListener(e -> details.show(connectorMessage));
        return getDetails;
    }

    private void handleSortEvent(
        SortEvent<Grid<WebMessage>, GridSortOrder<WebMessage>> gridGridSortOrderSortEvent) {
        gridGridSortOrderSortEvent
            .getSortOrder()
            .stream()
            .map(SortOrder::getDirection);
    }

    private Example<WebMessage> createExample() {
        return Example.of(
            exampleWebMessage, ExampleMatcher
                .matchingAny()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
    }

    private int countCallback(Query<WebMessage, WebMessage> webMessageWebMessageQuery) {
        return (int) dcMessagePersistenceService.count(createExample());
    }

    private Stream<WebMessage> fetchCallback(
        Query<WebMessage, WebMessage> webMessageWebMessageQuery) {
        LOGGER.debug("Call fetchCallback");
        int offset = webMessageWebMessageQuery.getOffset();
        LOGGER.debug("Offset: {}", offset);
        var collect = getSortOrder()
            .stream()
            .filter(sortOrder -> sortOrder.getSorted().getKey() != null)
            .map(sortOrder ->
                     sortOrder.getDirection() == SortDirection.ASCENDING
                         ? Sort.Order.asc(sortOrder.getSorted().getKey())
                         : Sort.Order.desc(sortOrder.getSorted().getKey()))
            .collect(Collectors.toList());
        if (collect.isEmpty()) {
            collect.add(Sort.Order.desc(TAG_CREATED)); // set default sort order if none selected
        }
        var sort = Sort.by(collect.toArray(new Sort.Order[] {}));

        // creating page request with sort order and offset
        var pageRequest = PageRequest.of(offset / getPageSize(), getPageSize(), sort);
        LOGGER.debug("PageRequest: {}", pageRequest);
        var all = dcMessagePersistenceService.findAll(createExample(), pageRequest);
        LOGGER.debug("Page requested size: {}", all.getSize());
        this.currentPage = all;

        return all.stream();
    }

    public void reloadList() {
        LOGGER.debug("#reloadList");
        getCallbackDataProvider().refreshAll();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        LOGGER.debug("#afterNavigation: Create and set callbackDataProvider");
        callbackDataProvider
            = getCallbackDataProvider();
        setDataProvider(callbackDataProvider);
    }

    private CallbackDataProvider<WebMessage, WebMessage> getCallbackDataProvider() {
        if (callbackDataProvider == null) {
            callbackDataProvider =
                new CallbackDataProvider<>(this::fetchCallback, this::countCallback);
        }
        return callbackDataProvider;
    }
}
