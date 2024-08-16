/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.configuration.SecurityUtils;
import eu.domibus.connector.ui.utils.UiStyle;
import org.springframework.stereotype.Component;

/**
 * The {@code DomibusConnectorAdminHeader} class represents the header component for the Domibus
 * connector administration view.
 */
@Component
@UIScope
public class DomibusConnectorAdminHeader extends HorizontalLayout implements BeforeEnterObserver {
    LumoLabel currentUser = new LumoLabel();

    /**
     * Constructor.
     *
     * @param config the {@code ConnectorConfigurationProperties} object used to configure the
     *               connector behavior
     */
    public DomibusConnectorAdminHeader(ConnectorConfigurationProperties config) {
        var ecodexLogo = new Div();
        var ecodex = new Image("frontend/images/logo_ecodex_0.png", "eCodex");
        ecodex.setHeight("70px");
        ecodexLogo.add(ecodex);
        ecodexLogo.setHeight("70px");

        var connectorAdminLabel = new LumoLabel("domibusConnector - Administration");
        connectorAdminLabel.getStyle().set("font-size", "30px");
        connectorAdminLabel.getStyle().set("font-style", "italic");
        connectorAdminLabel.getStyle().set("color", "grey");
        connectorAdminLabel.getStyle().set("display", "block");
        var stage = new LumoLabel(
            "Stage: [" + config.getStage().getName() + "] Instance: [" + config.getInstanceName()
                + "]");
        stage.getStyle().set("font-size", "10pt");
        stage.getStyle().set("font-style", "normal");
        stage.getStyle().set("display", "block");

        var domibusConnector = new Div();
        domibusConnector.add(connectorAdminLabel);
        domibusConnector.add(stage);
        domibusConnector.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);

        var europaLogo = new Div();
        var europa = new Image("frontend/images/europa-logo.jpg", "europe");
        europa.setHeight("50px");
        europaLogo.add(europa);
        europaLogo.setHeight("50px");

        add(ecodexLogo, domibusConnector, europaLogo, currentUser);
        setAlignItems(Alignment.CENTER);
        expand(domibusConnector);
        setJustifyContentMode(
            com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER);
        setWidth("95%");
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
