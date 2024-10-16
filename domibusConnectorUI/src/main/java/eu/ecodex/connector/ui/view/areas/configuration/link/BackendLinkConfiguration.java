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
import eu.ecodex.connector.ui.view.areas.configuration.link.importoldconfig.ImportOldBackendConfigDialog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This class represents the configuration for the backend link in the application.
 */
@Component
@UIScope
@TabMetadata(title = "Backend Configuration", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Route(value = BackendLinkConfiguration.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@Order(1)
public class BackendLinkConfiguration extends LinkConfiguration {
    public static final String ROUTE = "backendlink";
    public static final String TITLE = "Backend Configuration";
    private final ObjectProvider<ImportOldBackendConfigDialog> importOldBackendConfig;
    private final Button importOldConfigButton =
        new Button("Import Link Config From 4.2 Connector Properties");

    /**
     * This class represents the configuration for the backend link in the application.
     */
    public BackendLinkConfiguration(
        DCLinkFacade dcLinkFacade,
        ObjectProvider<ImportOldBackendConfigDialog> importOldGatewayConfigDialog,
        ApplicationContext applicationContext) {
        super(dcLinkFacade, LinkType.BACKEND, TITLE);
        this.importOldBackendConfig = importOldGatewayConfigDialog;
        importOldConfigButton.addClickListener(this::importOldConfig);
        super.buttonBar.add(importOldConfigButton);
    }

    private void importOldConfig(ClickEvent<Button> buttonClickEvent) {
        ImportOldBackendConfigDialog dialog = importOldBackendConfig.getObject();
        dialog.setDialogCloseCallback(this::refreshList);
        dialog.open();
    }
}
