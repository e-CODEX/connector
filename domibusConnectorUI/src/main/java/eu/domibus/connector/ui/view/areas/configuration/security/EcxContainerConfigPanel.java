/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.security;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.configuration.security.importoldconfig.ImportEcodexContainerConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Represents a configuration panel for ECx containers. This panel is used for configuring ECx
 * container properties and importing old configurations.
 *
 * @see Component
 * @see UIScope
 * @see Route
 * @see RoleRequired
 * @see TabMetadata
 * @see Order
 * @see DCVerticalLayoutWithTitleAndHelpButton
 */
@Component
@UIScope
@Route(value = EcxContainerConfigPanel.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@TabMetadata(
    title = "ECodex Container Configuration", tabGroup = ConfigurationLayout.TAB_GROUP_NAME
)
@Order(5)
@SuppressWarnings("squid:S1135")
public class EcxContainerConfigPanel extends DCVerticalLayoutWithTitleAndHelpButton {
    public static final String ROUTE = "ecxContainer";
    public static final String TITLE = "ECodex Container Configuration";
    public static final String HELP_ID = "ui/configuration/ecodex_container_configuration.html";

    /**
     * Constructor.
     *
     * @param configurationPanelFactory   the factory used to create configuration panels
     * @param importEcodexContainerConfig the provider for importing old configurations
     * @param form                        the form associated with the panel
     */
    public EcxContainerConfigPanel(
        ConfigurationPanelFactory configurationPanelFactory,
        ObjectProvider<ImportEcodexContainerConfig> importEcodexContainerConfig,
        EcxContainerConfigForm form) {
        super(HELP_ID, TITLE);
        ConfigurationPanelFactory.ConfigurationPanel<DCEcodexContainerProperties> configurationPanel
            = configurationPanelFactory.createConfigurationPanel(
            form,
            DCEcodexContainerProperties.class
        );

        var importOldConfig = new Button("Import old config");
        importOldConfig.addClickListener(event -> {
            var dialog = importEcodexContainerConfig.getObject();
            // TODO due some reason dialogCloseActionListener does not work
            //  dialog.addDialogCloseActionListener((ComponentEventListener<
            //  Dialog.DialogCloseActionEvent>) event1 -> configurationPanel.refreshUI());
            dialog.setDialogCloseCallback(configurationPanel::refreshUI);
            dialog.open();
        });
        this.add(importOldConfig);
        this.add(configurationPanel);
    }
}
