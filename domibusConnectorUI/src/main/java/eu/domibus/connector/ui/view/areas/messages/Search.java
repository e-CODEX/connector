package eu.domibus.connector.ui.view.areas.messages;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;

@Component
@UIScope
@Route(value = Search.ROUTE, layout= MessageLayout.class)
@Order(2)
@TabMetadata(title = "Search", tabGroup = MessageLayout.TAB_GROUP_NAME)
public class Search extends VerticalLayout {

	public static final String ROUTE = "search";

	private static final long serialVersionUID = 1L;
	
	private MessageDetails details;
	private WebMessageService messageService;
	
	private final DomibusConnectorWebMessagePersistenceService dcMessagePersistenceService;
	
	TextField searchMessageIdText = new TextField();
	TextField searchEbmsIdText = new TextField();
	TextField searchBackendMessageIdText = new TextField();
	TextField searchConversationIdText = new TextField();
	
	private VerticalLayout main = new VerticalLayout();

	public Search(WebMessageService messageService, MessageDetails details, DomibusConnectorWebMessagePersistenceService dcMessagePersistenceService) {
		this.messageService = messageService;
		this.details = details;
		this.dcMessagePersistenceService = dcMessagePersistenceService;
	}

	@PostConstruct
	void init() {

		HorizontalLayout connectorMessageIdSearch = new HorizontalLayout();

		searchMessageIdText.setPlaceholder("Search by Connector Message ID");
		searchMessageIdText.setWidth("300px");
		connectorMessageIdSearch.add(searchMessageIdText);

		Button searchConnectorMessageIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchConnectorMessageIdBtn.addClickListener(e -> searchByConnectorMessageId(searchMessageIdText.getValue()));
		connectorMessageIdSearch.add(searchConnectorMessageIdBtn);

		add(connectorMessageIdSearch);

		HorizontalLayout ebmsIdSearch = new HorizontalLayout();

		searchEbmsIdText.setPlaceholder("Search by EBMS Message ID");
		searchEbmsIdText.setWidth("300px");
		ebmsIdSearch.add(searchEbmsIdText);

		Button searchEbmsIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchEbmsIdBtn.addClickListener(e -> searchByEbmsId(searchEbmsIdText.getValue()));
		ebmsIdSearch.add(searchEbmsIdBtn);

		add(ebmsIdSearch);

		HorizontalLayout backendMessageIdSearch = new HorizontalLayout();

		searchBackendMessageIdText.setPlaceholder("Search by Backend Message ID");
		searchBackendMessageIdText.setWidth("300px");
		backendMessageIdSearch.add(searchBackendMessageIdText);

		Button searchBackendMessageIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchBackendMessageIdBtn.addClickListener(e -> searchByBackendMessageId(searchBackendMessageIdText.getValue()));
		backendMessageIdSearch.add(searchBackendMessageIdBtn);

		add(backendMessageIdSearch);

		HorizontalLayout conversationIdSearch = new HorizontalLayout();

		searchConversationIdText.setPlaceholder("Search by Conversation ID");
		searchConversationIdText.setWidth("300px");
		conversationIdSearch.add(searchConversationIdText);

		Button searchConversationIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchConversationIdBtn.addClickListener(e -> searchByConversationId(searchConversationIdText.getValue()));
		conversationIdSearch.add(searchConversationIdBtn);

		add(conversationIdSearch);

//		HorizontalLayout dateSearch = new HorizontalLayout();
//
//		DatePicker fromDate = new DatePicker();
//		fromDate.setLocale(Locale.ENGLISH);
//		fromDate.setLabel("From Date");
//		fromDate.setErrorMessage("From Date invalid!");
//		dateSearch.add(fromDate);
//
//		DatePicker toDate = new DatePicker();
//		toDate.setLocale(Locale.ENGLISH);
//		toDate.setLabel("To Date");
//		toDate.setErrorMessage("To Date invalid!");
//		dateSearch.add(toDate);

//		Button searchPeriodBtn = new Button(new Icon(VaadinIcon.SEARCH));
//		searchPeriodBtn.addClickListener(e -> searchByPeriod(asDate(fromDate.getValue()), asDate(toDate.getValue())));
//		dateSearch.add(searchPeriodBtn);

//		dateSearch.setAlignItems(Alignment.END);
//
//		add(dateSearch);

		add(main);

		setHeight("100vh");
		setWidth("100vw");
	}
	
	private void addGridWithData(WebMessage example) {
		main.removeAll();
		
		WebMessagesGrid grid = new WebMessagesGrid(details, dcMessagePersistenceService, example);
		grid.reloadList();
		
		grid.setVisible(true);
		
		
		main.add(grid);
		main.setAlignItems(Alignment.STRETCH);
		main.setWidth("100vw");
	}
	
//	private HorizontalLayout createDownloadLayout(LinkedList<WebMessage> messages) {
//		Div downloadExcel = new Div();
//		
//		Button download = new Button();
//		download.setIcon(new Image("frontend/images/xls.png", "XLS"));
//		
//		download.addClickListener(e -> {
//		
//			Element file = new Element("object");
//			Element dummy = new Element("object");
//			
//			Input oName = new Input();
//			
//			String name = "MessagesList.xls";
//			
//			StreamResource resource = new StreamResource(name,() -> getMessagesListExcel(messages));
//			
//			resource.setContentType("application/xls");
//			
//			file.setAttribute("data", resource);
//			
//			Anchor link = null;
//			link = new Anchor(file.getAttribute("data"), "Download Document");
//			
//			UI.getCurrent().getElement().appendChild(oName.getElement(), file,
//					dummy);
//			oName.setVisible(false);
//			file.setVisible(false);
//			this.getUI().get().getPage().executeJavaScript("window.open('"+link.getHref()+"');");
//		});
//		
//		downloadExcel.add(download);
//		
//		HorizontalLayout downloadLayout = new HorizontalLayout(
//				downloadExcel
//			    );
//		downloadLayout.setWidth("100vw");
//		
//		return downloadLayout;
//	}
	
//	private InputStream getMessagesListExcel(LinkedList<WebMessage> messages) {
//		return messageService.generateExcel(messages);
//	}

	private void searchByBackendMessageId(String backendMessageId) {
		Optional<WebMessage> messageByBackendMessageId = messageService.getMessageByBackendMessageId(backendMessageId);
		searchBackendMessageIdText.setValue("");
		messageByBackendMessageId.ifPresent(m -> details.show(m));
	}

	private void searchByEbmsId(String ebmsId) {
		Optional<WebMessage>  messageByEbmsId = messageService.getMessageByEbmsId(ebmsId);
		searchEbmsIdText.setValue("");
		messageByEbmsId.ifPresent((m) -> details.show(m));

	}

//	private void searchByPeriod(Date fromDate, Date toDate) {
//		toDate = new Date(toDate.getTime() + TimeUnit.DAYS.toMillis( 1 ));
//		LinkedList<WebMessage> fullList = messageService.getMessagesByPeriod(fromDate, toDate);
//		addGridWithData(fullList);
//	}
	
	private void searchByConversationId(String conversationId) {
		WebMessage example = new WebMessage();
		example.setConversationId(conversationId);
//		LinkedList<WebMessage> fullList = messageService.getMessagesByConversationId(conversationId);
		addGridWithData(example);
	}

	private void searchByConnectorMessageId(String connectorMessageId) {
		Optional<WebMessage> messageByConnectorId = messageService.getMessageByConnectorId(connectorMessageId);
		searchMessageIdText.setValue("");
		messageByConnectorId.ifPresent((m) -> details.show(m));
	}
	
//	public static Date asDate(LocalDate localDate) {
//	    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
//	  }
}
