/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.forms;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import eu.domibus.connector.ui.dto.WebMessage;
import lombok.Getter;

/**
 * The ConnectorMessageForm class represents a form used to display and edit connector message
 * information.
 */
@Getter
public class ConnectorMessageForm extends FormLayout {
    private final TextField connectorMessageId = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField backendMessageId = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField ebmsMessageId = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField conversationId = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField originalSender = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField finalRecipient = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField service = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField action = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField fromParty = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField toParty = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField backendName = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField direction = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField deliveredToNationalSystemString =
        FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField deliveredToGatewayString = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField confirmedString = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField rejectedString = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField createdString = FormsUtil.getFormattedTextFieldReadOnly();
    private final Binder<WebMessage> binder = new Binder<>(WebMessage.class);

    public ConnectorMessageForm() {
        fillForm();
    }

    private void fillForm() {
        binder.bindInstanceFields(this);

        addFormItem(connectorMessageId, "Connector Message ID");
        addFormItem(backendMessageId, "Backend Message ID");
        addFormItem(ebmsMessageId, "EBMS Message ID");
        addFormItem(conversationId, "Conversation ID");
        addFormItem(originalSender, "Original Sender");
        addFormItem(finalRecipient, "Final Recipient");
        addFormItem(service, "Service");
        addFormItem(action, "Action");
        addFormItem(fromParty, "From Party");
        addFormItem(toParty, "To Party");
        addFormItem(backendName, "Backend Client Name");
        addFormItem(direction, "Direction");
        addFormItem(deliveredToNationalSystemString, "Delivered to Backend at");
        addFormItem(deliveredToGatewayString, "Delivered to Gateway at");
        addFormItem(confirmedString, "Confirmed at");
        addFormItem(rejectedString, "Rejected at");
        addFormItem(createdString, "Message created at");

        binder.bind(
            originalSender,
            webMessage -> webMessage.getMessageInfo() != null
                ? webMessage.getMessageInfo().getOriginalSender()
                : "",
            (webMessage, originalSender) -> webMessage.getMessageInfo()
                                                      .setOriginalSender(originalSender)
        );
        binder.bind(
            finalRecipient,
            webMessage -> webMessage.getMessageInfo() != null
                ? webMessage.getMessageInfo().getFinalRecipient()
                : "",
            (webMessage, finalRecipient) -> webMessage.getMessageInfo()
                                                      .setFinalRecipient(finalRecipient)
        );
        binder.bind(
            service,
            webMessage -> webMessage.getMessageInfo() != null
                && webMessage.getMessageInfo().getService() != null
                ? webMessage.getMessageInfo().getService().getServiceString()
                : "",
            (webMessage, service) -> webMessage.getMessageInfo().setServiceString(service)
        );
        binder.bind(
            action,
            webMessage -> webMessage.getMessageInfo() != null
                && webMessage.getMessageInfo().getAction() != null
                ? webMessage.getMessageInfo().getAction().getAction()
                : "",
            (webMessage, service) -> webMessage.getMessageInfo().setServiceString(service)
        );
        binder.bind(
            fromParty,
            webMessage -> webMessage.getMessageInfo() != null
                && webMessage.getMessageInfo().getFrom() != null
                ? webMessage.getMessageInfo().getFrom().getPartyString()
                : "",
            (webMessage, fromParty) -> webMessage.getMessageInfo().setServiceString(fromParty)
        );
        binder.bind(
            toParty,
            webMessage ->
                webMessage.getMessageInfo() != null && webMessage.getMessageInfo().getTo() != null
                    ? webMessage.getMessageInfo().getTo().getPartyString()
                    : "",
            (webMessage, toParty) -> webMessage.getMessageInfo().setServiceString(toParty)
        );
    }

    /**
     * Sets the connector message for the ConnectorMessageForm.
     *
     * @param message the WebMessage object to be set as the connector message
     */
    public void setConnectorMessage(WebMessage message) {
        this.removeAll();
        fillForm();
        binder.setBean(message);
    }

    public String getConnectorMessageId() {
        return this.connectorMessageId.getValue();
    }
}
