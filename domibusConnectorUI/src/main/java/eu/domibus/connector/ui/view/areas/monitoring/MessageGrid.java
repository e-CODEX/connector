/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.monitoring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.ui.controller.QueueController;
import eu.domibus.connector.ui.dto.WebQueue;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;

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
        this.setHeightByRows(true);

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
