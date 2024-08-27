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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.dto.WebMessageEvidence;
import eu.domibus.connector.ui.forms.ConnectorMessageForm;
import eu.domibus.connector.ui.service.WebMessageService;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import io.micrometer.core.instrument.util.StringUtils;
import jakarta.annotation.PostConstruct;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The MessageDetails class is responsible for displaying the details of a message in the UI.
 */
@Component
@Route(value = MessageDetails.ROUTE, layout = MessageLayout.class)
@UIScope
@Order(3)
@TabMetadata(title = "Message Details", tabGroup = MessageLayout.TAB_GROUP_NAME)
public class MessageDetails extends VerticalLayout implements HasUrlParameter<String> {
    public static final String ROUTE = "messageDetails";
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(MessageDetails.class);
    private final WebMessageService messageService;
    private final ConnectorMessageForm messageForm;
    private final VerticalLayout messageEvidencesArea;

    /**
     * Constructor.
     *
     * @param messageService The WebMessageService object used for retrieving and manipulating web
     *                       messages.
     * @see WebMessageService
     */
    public MessageDetails(@Autowired WebMessageService messageService) {
        this.messageService = messageService;
        this.messageForm = new ConnectorMessageForm();
        this.messageEvidencesArea = new VerticalLayout();
    }

    @PostConstruct
    void init() {
        var refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
        refreshButton.setText("Refresh");
        refreshButton.addClickListener(e -> {
            if (messageForm.getBinder() != null) {
                loadMessageDetails(messageForm.getBinder().getBean());
            }
        });

        var buttons = new HorizontalLayout(
            refreshButton
        );
        buttons.setWidth(UiStyle.WIDTH_100_VW);
        add(buttons);

        var messageDetailsArea = new VerticalLayout();
        messageForm.getStyle().set("margin-top", "25px");

        messageDetailsArea.add(messageForm);
        messageForm.setEnabled(true);
        messageDetailsArea.setWidth(UiStyle.WIDTH_500_PX);
        add(messageDetailsArea);

        add(messageEvidencesArea);

        setSizeFull();
    }

    /**
     * Loads the details of a web message.
     *
     * @param connectorMessage the WebMessage object containing the connector message details
     */
    public void loadMessageDetails(WebMessage connectorMessage) {
        Optional<WebMessage> optionalMessage = Optional.empty();

        if (!StringUtils.isEmpty(connectorMessage.getConnectorMessageId())) {
            LOGGER.debug(
                "MessageDetails loaded with connectorMessageId [{}]",
                connectorMessage.getConnectorMessageId()
            );
            optionalMessage =
                messageService.getMessageByConnectorId(connectorMessage.getConnectorMessageId());
        }

        if ((optionalMessage.isEmpty()) && !StringUtils.isEmpty(
            connectorMessage.getBackendMessageId())) {
            LOGGER.debug(
                "MessageDetails loaded with backendMessageId [{}]",
                connectorMessage.getBackendMessageId()
            );
            optionalMessage =
                messageService.getMessageByBackendMessageId(connectorMessage.getBackendMessageId());
        }

        if ((optionalMessage.isEmpty()) && !StringUtils.isEmpty(
            connectorMessage.getEbmsMessageId())) {
            LOGGER.debug(
                "MessageDetails loaded with ebmsMessageId [{}]",
                connectorMessage.getEbmsMessageId()
            );
            optionalMessage =
                messageService.getMessageByEbmsId(connectorMessage.getEbmsMessageId());
        }

        if (optionalMessage.isEmpty()) {
            var errorMessage = String.format(
                "No message found within database with connectorMessageId [%s], ebmsMessageId [%s] "
                    + "or backendMessageId [%s] !",
                connectorMessage.getConnectorMessageId(), connectorMessage.getEbmsMessageId(),
                connectorMessage.getBackendMessageId()
            );
            LOGGER.warn(errorMessage);
            Notification.show(errorMessage);
        }

        var webMessageDetail = optionalMessage.get();
        messageForm.setConnectorMessage(webMessageDetail);

        if (!webMessageDetail.getEvidences().isEmpty()) {
            messageEvidencesArea.removeAll();

            var evidences = new Div();
            evidences.setWidth(UiStyle.WIDTH_100_VW);
            var evidencesLabel = new LumoLabel();
            evidencesLabel.setText("Evidences:");
            evidencesLabel.getStyle().set("font-size", "20px");
            evidences.add(evidencesLabel);

            messageEvidencesArea.add(evidences);

            var details = new Div();
            details.setWidth(UiStyle.WIDTH_100_VW);

            Grid<WebMessageEvidence> grid = new Grid<>();

            grid.setItems(webMessageDetail.getEvidences());

            grid.addColumn(WebMessageEvidence::getEvidenceType).setHeader("Evidence Type")
                .setWidth(UiStyle.WIDTH_300_PX);
            grid.addColumn(WebMessageEvidence::getDeliveredToGatewayString)
                .setHeader("Delivered to Gateway").setWidth(UiStyle.WIDTH_300_PX);
            grid.addColumn(WebMessageEvidence::getDeliveredToBackendString)
                .setHeader("Delivered to Backend").setWidth(UiStyle.WIDTH_300_PX);

            grid.setWidth("1000px");
            grid.setHeight("210px");
            grid.setMultiSort(true);

            for (Column<WebMessageEvidence> col : grid.getColumns()) {
                col.setSortable(true);
                col.setResizable(true);
            }

            details.add(grid);

            messageEvidencesArea.add(details);

            messageEvidencesArea.setWidth(UiStyle.WIDTH_100_VW);
            messageEvidencesArea.setVisible(true);
        }
    }

    public static void navigateTo(DomibusConnectorMessageId messageId) {
        UI.getCurrent().navigate(MessageDetails.class, messageId.getConnectorMessageId());
    }

    public void show(WebMessage message) {
        UI.getCurrent().navigate(MessageDetails.class, message.getConnectorMessageId());
    }

    private void clearMessageDetails() {
        messageForm.setConnectorMessage(new WebMessage());

        messageEvidencesArea.removeAll();
        messageEvidencesArea.setVisible(false);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null) {
            var webMessage = new WebMessage();
            webMessage.setConnectorMessageId(parameter);
            loadMessageDetails(webMessage);
        } else {
            clearMessageDetails();
        }
    }
}
