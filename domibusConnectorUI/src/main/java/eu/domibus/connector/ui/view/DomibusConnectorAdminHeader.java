package eu.domibus.connector.ui.view;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.configuration.SecurityUtils;
import org.springframework.stereotype.Component;


@Component
@UIScope
public class DomibusConnectorAdminHeader extends HorizontalLayout implements BeforeEnterObserver {

	LumoLabel currentUser = new LumoLabel();

	public DomibusConnectorAdminHeader(ConnectorConfigurationProperties config) {
		Div ecodexLogo = new Div();
		Image ecodex = new Image("frontend/images/logo_ecodex_0.png", "eCodex");
		ecodex.setHeight("70px");
		ecodexLogo.add(ecodex);
		ecodexLogo.setHeight("70px");
		
		
		Div domibusConnector = new Div();
		LumoLabel dC = new LumoLabel("domibusConnector - Administration");
		dC.getStyle().set("font-size", "30px");
		dC.getStyle().set("font-style", "italic");
		dC.getStyle().set("color", "grey");
		dC.getStyle().set("display", "block");
		LumoLabel stage = new LumoLabel("Stage: [" + config.getStage().getName() + "] Instance: [" + config.getInstanceName() + "]");
		stage.getStyle().set("font-size", "10pt");
		stage.getStyle().set("font-style", "normal");
		stage.getStyle().set("display", "block");

		domibusConnector.add(dC);
		domibusConnector.add(stage);
		domibusConnector.getStyle().set("text-align", "center");

		Div europaLogo = new Div();
		Image europa = new Image("frontend/images/europa-logo.jpg", "europe");
		europa.setHeight("50px");
		europaLogo.add(europa);
		europaLogo.setHeight("50px");
//		europaLogo.getStyle().set("margin-right", "3em");
		
		
		add(ecodexLogo, domibusConnector, europaLogo, currentUser);
		setAlignItems(Alignment.CENTER);
		expand(domibusConnector);
		setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER);
		setWidth("95%");
//		headerLayout.getStyle().set("border-bottom", "1px solid #9E9E9E");
//		headerLayout.getStyle().set("padding-bottom", "16px");


	}


	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (SecurityUtils.isUserLoggedIn()) {
			currentUser.setText("User: " + SecurityUtils.getUsername());
		} else {
			currentUser.setText("");
		}
	}
}
