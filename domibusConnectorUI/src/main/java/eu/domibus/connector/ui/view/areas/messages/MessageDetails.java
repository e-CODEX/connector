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
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

//@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@Component
@Route(value = MessageDetails.ROUTE, layout = MessageLayout.class)
@UIScope
@Order(3)
@TabMetadata(title = "Message Details", tabGroup = MessageLayout.TAB_GROUP_NAME)
public class MessageDetails extends VerticalLayout implements HasUrlParameter<String> {

	public static final String ROUTE = "messageDetails";

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = LogManager.getLogger(MessageDetails.class);

	private WebMessageService messageService;
	private ConnectorMessageForm messageForm;
	private VerticalLayout messageEvidencesArea;

	public MessageDetails(@Autowired WebMessageService messageService) {
		this.messageService = messageService;
		this.messageForm = new ConnectorMessageForm();
		this.messageEvidencesArea = new VerticalLayout();
	}

	@PostConstruct
	void init() {
		Button refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
		refreshButton.setText("Refresh");
		refreshButton.addClickListener(e -> {
			if(messageForm.getBinder()!=null)loadMessageDetails(messageForm.getBinder().getBean());
		});

		HorizontalLayout buttons = new HorizontalLayout(
				refreshButton
		);
		buttons.setWidth("100vw");
		add(buttons);

		VerticalLayout messageDetailsArea = new VerticalLayout();
		messageForm.getStyle().set("margin-top","25px");

		messageDetailsArea.add(messageForm);
		//setAlignItems(Alignment.START);
		messageForm.setEnabled(true);
//		messageDetailsArea.setHeight("100vh");
		messageDetailsArea.setWidth("500px");
		add(messageDetailsArea);

		add(messageEvidencesArea);

		setSizeFull();
//		setHeight("100vh");
	}


	public void loadMessageDetails(WebMessage connectorMessage) {

		Optional<WebMessage> optionalMessage = Optional.empty();

		if(!StringUtils.isEmpty(connectorMessage.getConnectorMessageId())) {
			LOGGER.debug("MessageDetails loaded with connectorMessageId [{}]", connectorMessage.getConnectorMessageId());
			optionalMessage = messageService.getMessageByConnectorId(connectorMessage.getConnectorMessageId());
		}

		if ((!optionalMessage.isPresent()) && !StringUtils.isEmpty(connectorMessage.getBackendMessageId())) {
			LOGGER.debug("MessageDetails loaded with backendMessageId [{}]", connectorMessage.getBackendMessageId());
			optionalMessage = messageService.getMessageByBackendMessageId(connectorMessage.getBackendMessageId());
		}

		if ((!optionalMessage.isPresent()) && !StringUtils.isEmpty(connectorMessage.getEbmsMessageId())) {
			LOGGER.debug("MessageDetails loaded with ebmsMessageId [{}]", connectorMessage.getEbmsMessageId());
			optionalMessage = messageService.getMessageByEbmsId(connectorMessage.getEbmsMessageId());
		}

		if (!optionalMessage.isPresent()) {
			String errorMessage = String.format("No message found within database with connectorMessageId [%s], ebmsMessageId [%s] or backendMessageId [%s] !", connectorMessage.getConnectorMessageId(), connectorMessage.getEbmsMessageId(), connectorMessage.getBackendMessageId());
			LOGGER.warn(errorMessage);
			Notification.show(errorMessage);
		}

			WebMessage webMessageDetail = optionalMessage.get();
			messageForm.setConnectorMessage(webMessageDetail);

			if (!webMessageDetail.getEvidences().isEmpty()) {
				messageEvidencesArea.removeAll();

				Div evidences = new Div();
				evidences.setWidth("100vw");
				LumoLabel evidencesLabel = new LumoLabel();
				evidencesLabel.setText("Evidences:");
				evidencesLabel.getStyle().set("font-size", "20px");
				evidences.add(evidencesLabel);

				messageEvidencesArea.add(evidences);

				Div details = new Div();
				details.setWidth("100vw");

				Grid<WebMessageEvidence> grid = new Grid<>();

				grid.setItems(webMessageDetail.getEvidences());

				grid.addColumn(WebMessageEvidence::getEvidenceType).setHeader("Evidence Type").setWidth("300px");
				grid.addColumn(WebMessageEvidence::getDeliveredToGatewayString).setHeader("Delivered to Gateway").setWidth("300px");
				grid.addColumn(WebMessageEvidence::getDeliveredToBackendString).setHeader("Delivered to Backend").setWidth("300px");

				grid.setWidth("1000px");
				grid.setHeight("210px");
				grid.setMultiSort(true);

				for (Column<WebMessageEvidence> col : grid.getColumns()) {
					col.setSortable(true);
					col.setResizable(true);
				}

				details.add(grid);

				messageEvidencesArea.add(details);

				messageEvidencesArea.setWidth("100vw");
				//			add(messageEvidencesArea);
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
	public void setParameter(BeforeEvent event
			, @OptionalParameter String parameter) {
		if(parameter!=null) {
			WebMessage webMessage = new WebMessage();
			webMessage.setConnectorMessageId(parameter);
			loadMessageDetails(webMessage);
		}else {
			clearMessageDetails();
		}
	}
}
