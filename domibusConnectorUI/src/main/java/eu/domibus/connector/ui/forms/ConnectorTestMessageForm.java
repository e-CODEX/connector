package eu.domibus.connector.ui.forms;

import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.GeneratedVaadinTextField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;

import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorParty.PartyRoleType;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.dto.WebMessageDetail;
import eu.domibus.connector.ui.dto.WebMessageDetail.Party;
import eu.domibus.connector.ui.dto.WebMessageDetail.Service;

public class ConnectorTestMessageForm extends FormLayout {
	
	
	private TextField conversationId = FormsUtil.getFormattedTextField();
	private TextField backendMessageId = FormsUtil.getFormattedRequiredTextField();
	private TextField originalSender = FormsUtil.getFormattedRequiredTextField();
	private TextField finalRecipient = FormsUtil.getFormattedRequiredTextField();
	private ComboBox<WebMessageDetail.Party> toParty = (ComboBox<WebMessageDetail.Party>) FormsUtil.getRequiredCombobox();
	private TextField service = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField action = FormsUtil.getFormattedTextFieldReadOnly();
	private ComboBox<WebMessageDetail.Party> fromParty = (ComboBox<WebMessageDetail.Party>) FormsUtil.getRequiredCombobox();
	
	private Binder<WebMessage> binder = new Binder<>(WebMessage.class);

	public ConnectorTestMessageForm() {
		fillForm();
	}

	private void fillForm() {
		binder.bindInstanceFields(this);
		
		addFormItem(conversationId, "Conversation ID");
		
		binder.forField(backendMessageId).withValidator((Validator<String>) (value, context) -> {
			if (value.length() < 1) {
                return ValidationResult
                        .error("Backend Message ID must not be empty!");
            }
			return ValidationResult.ok();
		}).bind(
				webMessage -> webMessage.getBackendMessageId()!=null?webMessage.getBackendMessageId():"",
				(webMessage,backendMessageId) -> {
					webMessage.setBackendMessageId(backendMessageId);
				});
		addFormItem(backendMessageId, "Backend Message ID"); 
		
		binder.forField(originalSender).withValidator((Validator<String>) (value, context) -> {
			if (value.length() < 1) {
                return ValidationResult
                        .error("Original Sender must not be empty!");
            }
			return ValidationResult.ok();
		}).bind(
				webMessage -> webMessage.getMessageInfo()!=null?webMessage.getMessageInfo().getOriginalSender():"",
				(webMessage,originalSender) -> {
					webMessage.getMessageInfo().setOriginalSender(originalSender);
				});
		addFormItem(originalSender, "Original Sender"); 
		
		binder.forField(finalRecipient).withValidator((Validator<String>) (value, context) -> {
			if (value.length() < 1) {
                return ValidationResult
                        .error("Final Recipient must not be empty!");
            }
			return ValidationResult.ok();
		}).bind(
				webMessage -> webMessage.getMessageInfo()!=null?webMessage.getMessageInfo().getFinalRecipient():"",
				(webMessage,finalRecipient) -> {
					webMessage.getMessageInfo().setFinalRecipient(finalRecipient);
				});
		addFormItem(finalRecipient, "Final Recipient");
		
		
		binder.forField(toParty).withValidator((Validator<WebMessageDetail.Party>) (value, context) -> {
			if (value == null) {
                return ValidationResult
                        .error("ToParty must not be empty!");
            }
			return ValidationResult.ok();
		}).bind(
				webMessage -> webMessage.getMessageInfo()!=null ? webMessage.getMessageInfo().getTo() : null,
				(webMessage,toParty) -> {
					webMessage.getMessageInfo().setTo(toParty);
				});
		addFormItem(toParty, "ToParty");

		binder.forField(service).bind(
				webMessage -> webMessage.getMessageInfo()!=null?webMessage.getMessageInfo().getService().getService():"",
				(webMessage,service) -> {
					webMessage.getMessageInfo().getService().setService(service);
				});
		addFormItem(service, "Service");

		binder.forField(action).bind(
				webMessage -> webMessage.getMessageInfo()!=null?webMessage.getMessageInfo().getAction().getAction():"",
				(webMessage,action) -> {
					webMessage.getMessageInfo().getAction().setAction(action);
				});
		addFormItem(action, "Action");

		binder.forField(fromParty)
				.bind(webMessage -> webMessage.getMessageInfo() != null ? webMessage.getMessageInfo().getFrom() : null,
						(webMessage, fromParty) -> {
							webMessage.getMessageInfo().setFrom(fromParty);
						});
		fromParty.setReadOnly(true);

		addFormItem(fromParty, "From Party");
		
	}

	public ConnectorTestMessageForm(Component... components) {
		super(components);
		// TODO Auto-generated constructor stub
	}
	
	public void setParties(List<DomibusConnectorParty> parties) {
		
		toParty.setItems(parties.stream()
				.filter(p -> p.getRoleType().equals(PartyRoleType.RESPONDER))
								.map(p -> new WebMessageDetail.Party(p.getPartyId(), p.getPartyIdType(), p.getRole())));
		fromParty.setItems(parties.stream()
				.filter(p -> p.getRoleType().equals(PartyRoleType.INITIATOR))
				.map(p -> new WebMessageDetail.Party(p.getPartyId(), p.getPartyIdType(), p.getRole())));
	}
	
	public void setMessage(WebMessage message) {
		this.removeAll();
		fillForm();
		binder.setBean(message);
	}
	
	public WebMessage getMessage() {
		return binder.getBean();
	}

	public Binder<WebMessage> getBinder() {
		return binder;
	}
}
