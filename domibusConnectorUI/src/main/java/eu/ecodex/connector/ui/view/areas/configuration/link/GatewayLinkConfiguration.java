/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.link.service.DCLinkFacade;
import eu.ecodex.connector.ui.utils.RoleRequired;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
import eu.ecodex.connector.ui.view.areas.configuration.link.importoldconfig.ImportOldGatewayConfigDialog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The GatewayLinkConfiguration class represents a configuration for a gateway link.
 */
@Component
@UIScope
@TabMetadata(title = "Gateway Configuration", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Route(value = GatewayLinkConfiguration.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@Order(2)
public class GatewayLinkConfiguration extends LinkConfiguration {
    public static final String ROUTE = "gwlink";
    public static final String TITLE = "Gateway Configuration";
    private final ObjectProvider<ImportOldGatewayConfigDialog> importOldGatewayConfigDialog;
    private Button importOldConfigButton =
        new Button("Import Link Config From 4.2 Connector Properties");

    /**
     * Constructor.
     *
     * @param dcLinkFacade                 The DCLinkFacade object used for the gateway link
     *                                     configuration.
     * @param importOldGatewayConfigDialog The ObjectProvider object used for importing old gateway
     *                                     configurations.
     * @param applicationContext           The ApplicationContext object used for the gateway link
     *                                     configuration.
     */
    public GatewayLinkConfiguration(
        DCLinkFacade dcLinkFacade,
        ObjectProvider<ImportOldGatewayConfigDialog> importOldGatewayConfigDialog,
        ApplicationContext applicationContext) {
        super(dcLinkFacade, LinkType.GATEWAY, TITLE);
        this.importOldGatewayConfigDialog = importOldGatewayConfigDialog;
        importOldConfigButton.addClickListener(this::importOldConfig);
        super.buttonBar.add(importOldConfigButton);
    }

    private void importOldConfig(ClickEvent<Button> buttonClickEvent) {
        var dialog = importOldGatewayConfigDialog.getObject();
        dialog.setDialogCloseCallback(this::refreshList);
        dialog.open();
    }
}
