/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.evidences;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import eu.ecodex.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.ecodex.connector.ui.utils.RoleRequired;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
import eu.ecodex.connector.ui.view.areas.configuration.security.importoldconfig.ImportOldEvidenceConfigDialog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Represents a configuration page for the Evidence Builder. This page is used to configure settings
 * related to the Evidence Builder.
 *
 * @author riederb
 * @see DCVerticalLayoutWithTitleAndHelpButton
 * @see Component
 * @see UIScope
 * @see Route
 * @see RoleRequired
 * @see TabMetadata
 * @see Order
 */
@Component
@UIScope
@Route(value = EvidenceBuilderConfiguration.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@TabMetadata(
    title = "Evidence Builder Configuration", tabGroup = ConfigurationLayout.TAB_GROUP_NAME
)
@Order(6)
@SuppressWarnings("squid:S1135")
public class EvidenceBuilderConfiguration extends DCVerticalLayoutWithTitleAndHelpButton {
    public static final String ROUTE = "evidencebuilder";
    public static final String TITLE = "Evidence Builder Configuration";
    public static final String HELP_ID = "ui/configuration/evidence_builder_configuration.html";

    /**
     * Constructor.
     *
     * @param configurationPanelFactory the factory for creating the configuration panel
     * @param importOldEvidenceConfigDialog the dialog for importing older evidence configurations
     * @param form the form for the Evidence Toolkit configuration properties
     */
    public EvidenceBuilderConfiguration(
        ConfigurationPanelFactory configurationPanelFactory,
        ObjectProvider<ImportOldEvidenceConfigDialog> importOldEvidenceConfigDialog,
        EvidencesToolkitConfigurationPropertiesForm form) {
        super(HELP_ID, TITLE);
        ConfigurationPanelFactory.ConfigurationPanel<EvidencesToolkitConfigurationProperties>
            configurationPanel
            = configurationPanelFactory.createConfigurationPanel(
            form,
            EvidencesToolkitConfigurationProperties.class
        );

        var importOldConfig = new Button("Import old config");
        importOldConfig.addClickListener(event -> {
            var dialog = importOldEvidenceConfigDialog.getObject();
            // TODO due some reason dialogCloseActionListener does not work
            //  dialog.addDialogCloseActionListener((ComponentEventListener<Dialog
            //  .DialogCloseActionEvent>) event1 -> configurationPanel.refreshUI());
            dialog.setDialogCloseCallback(configurationPanel::refreshUI);
            dialog.open();
        });
        this.add(importOldConfig);
        this.add(configurationPanel);
    }
}

