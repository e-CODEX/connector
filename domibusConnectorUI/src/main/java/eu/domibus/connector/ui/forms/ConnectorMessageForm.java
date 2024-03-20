package eu.domibus.connector.ui.forms;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import eu.domibus.connector.ui.dto.WebMessage;

public class ConnectorMessageForm extends FormLayout {

	private TextField connectorMessageId = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField backendMessageId = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField ebmsMessageId = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField conversationId = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField originalSender = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField finalRecipient = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField service = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField action = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField fromParty = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField toParty = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField backendName = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField direction = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField deliveredToNationalSystemString = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField deliveredToGatewayString = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField confirmedString = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField rejectedString = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField createdString = FormsUtil.getFormattedTextFieldReadOnly();

	private Binder<WebMessage> binder = new Binder<>(WebMessage.class);

	public ConnectorMessageForm() {
		fillForm();
//		setResponsiveSteps(new ResponsiveStep("500px", 2, LabelsPosition.TOP));
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

		binder.bind(originalSender,
				webMessage -> webMessage.getMessageInfo()!=null?webMessage.getMessageInfo().getOriginalSender():"",
				(webMessage,originalSender) -> {
					webMessage.getMessageInfo().setOriginalSender(originalSender);
				});
		binder.bind(finalRecipient,
				webMessage -> webMessage.getMessageInfo()!=null?webMessage.getMessageInfo().getFinalRecipient():"",
				(webMessage,finalRecipient) -> {
					webMessage.getMessageInfo().setFinalRecipient(finalRecipient);
				});
		binder.bind(service,
				webMessage -> webMessage.getMessageInfo()!=null && webMessage.getMessageInfo().getService()!=null?webMessage.getMessageInfo().getService().getServiceString():"",
				(webMessage,service) -> {
					webMessage.getMessageInfo().setServiceString(service);
				});
		binder.bind(action,
				webMessage -> webMessage.getMessageInfo()!=null && webMessage.getMessageInfo().getAction()!=null?webMessage.getMessageInfo().getAction().getAction():"",
				(webMessage,service) -> {
					webMessage.getMessageInfo().setServiceString(service);
				});
		binder.bind(fromParty,
				webMessage -> webMessage.getMessageInfo()!=null && webMessage.getMessageInfo().getFrom()!=null?webMessage.getMessageInfo().getFrom().getPartyString():"",
				(webMessage,fromParty) -> {
					webMessage.getMessageInfo().setServiceString(fromParty);
				});
		binder.bind(toParty,
				webMessage -> webMessage.getMessageInfo()!=null && webMessage.getMessageInfo().getTo()!=null?webMessage.getMessageInfo().getTo().getPartyString():"",
				(webMessage,toParty) -> {
					webMessage.getMessageInfo().setServiceString(toParty);
				});
	}

	public void setConnectorMessage(WebMessage message) {
		this.removeAll();
		fillForm();
		binder.setBean(message);
	}

	public String getConnectorMessageId() {
		return this.connectorMessageId.getValue();
	}

	public Binder<WebMessage> getBinder() {
		return binder;
	}

}
