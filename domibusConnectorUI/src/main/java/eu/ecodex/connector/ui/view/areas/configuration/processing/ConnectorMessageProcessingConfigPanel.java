/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.processing;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.controller.spring.ConnectorMessageProcessingProperties;
import eu.ecodex.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.ecodex.connector.ui.utils.RoleRequired;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Represents a configuration panel for managing general message processing configuration settings.
 */
@Component
@UIScope
@Route(value = ConnectorMessageProcessingConfigPanel.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")

@TabMetadata(
    title = "General Message Processing Config", tabGroup = ConfigurationLayout.TAB_GROUP_NAME
)
@Order(40)
public class ConnectorMessageProcessingConfigPanel extends DCVerticalLayoutWithTitleAndHelpButton {
    public static final String TITLE = "General Message Processing Config";
    public static final String HELP_ID = "ui/configuration/message_processing_config.html";
    public static final String ROUTE = "messageProcessingConfig";

    /**
     * Constructor.
     *
     * @param configurationPanelFactory the factory used to create configuration panels
     * @param form                      the properties config form used to initialize the
     *                                  configuration panel
     */
    public ConnectorMessageProcessingConfigPanel(
        ConfigurationPanelFactory configurationPanelFactory,
        ConnectorMessageProcessingPropertiesConfigForm form) {
        super(HELP_ID, TITLE);
        var configurationPanel = configurationPanelFactory.createConfigurationPanel(
            form,
            ConnectorMessageProcessingProperties.class
        );

        this.add(configurationPanel);
    }
}
