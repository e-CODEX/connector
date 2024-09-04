/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.monitoring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.ui.controller.QueueController;
import eu.ecodex.connector.ui.dto.WebQueue;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.List;

/**
 * The MessageGrid class is a custom grid component for displaying messages in a tabular format.
 */
public class MessageGrid extends Grid<Message> {
    private final QueueController queueController;
    private final JmsMonitoringView parentView;
    private WebQueue queue;

    /**
     * Constructor.
     *
     * @param queueController The QueueController object responsible for managing queues and
     *                        messages.
     * @param parentView      The JmsMonitoringView object that contains this grid.
     */
    public MessageGrid(QueueController queueController, JmsMonitoringView parentView) {
        super();
        this.queueController = queueController;
        this.parentView = parentView;

        this.setWidth("90%");
        this.setAllRowsVisible(true);

        addColumn(this::getJMSMessageID).setHeader("Message ID (JMS ID)").setWidth("35%");
        addColumn(this::getConnectorId).setHeader("Connector ID").setWidth("35%");
        addComponentColumn(this::viewMessageButton).setHeader("View Message").setWidth("10%");
        addComponentColumn(this::restoreButton).setHeader("Restore Message and try to reprocess")
                                               .setWidth("10%");
        addComponentColumn(this::deleteButton).setHeader("Delete Message forever").setWidth("10%");
    }

    private Button viewMessageButton(Message message) {
        final var viewMessageBtn = new Button("View");
        viewMessageBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        viewMessageBtn.addClickListener(buttonClickEvent -> {
            queueController.showMessage(message);
            parentView.updateData(queue);
        });
        return viewMessageBtn;
    }

    private Button restoreButton(Message message) {
        final var restore = new Button("Restore");
        restore.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        restore.addClickListener(buttonClickEvent -> {
            queueController.moveMsgFromDlqToQueue(message);
            parentView.updateData(queue);
        });
        return restore;
    }

    private Button deleteButton(Message message) {
        final var delete = new Button("Delete");
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        delete.addClickListener(buttonClickEvent -> {
            queueController.deleteMsg(message);
            parentView.updateData(queue);
        });
        return delete;
    }

    private String getJMSMessageID(Message message) {
        String result = null;
        try {
            result = message.getJMSMessageID();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setData(List<Message> msgs, WebQueue queue) {
        this.setItems(msgs);
        this.queue = queue;
    }

    private String getConnectorId(Message msg) {
        String result = null;
        try {
            final var domibusConnectorMessage =
                (DomibusConnectorMessage) queueController.getConverter().fromMessage(msg);
            result = domibusConnectorMessage.getConnectorMessageId().getConnectorMessageId();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return result;
    }
}
