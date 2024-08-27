/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.persistence.service.DomibusConnectorPropertiesPersistenceService;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.utils.DCTabHandler;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * The ConfigurationLayout class represents the layout for the configuration page in the
 * application.
 */
@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(ConfigurationLayout.ROUTE)
@ParentLayout(DCMainLayout.class)
public class ConfigurationLayout extends VerticalLayout
    implements BeforeEnterObserver, RouterLayout {
    public static final String ROUTE = "configuration";
    public static final String TAB_GROUP_NAME = "Configuration";
    protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLayout.class);
    private Div pageContent;
    Button saveConfiguration;
    Button resetConfiguration;
    Button reloadConfiguration;
    private final DCTabHandler dcTabHandler = new DCTabHandler();
    private final DomibusConnectorPropertiesPersistenceService propertiesPersistenceService;
    private final ConfigurationUtil util;
    private final ApplicationContext applicationContext;

    /**
     * Constructor.
     *
     * @param propertiesPersistenceService A service for loading, saving, and resetting connector
     *                                     properties.
     * @param util                         An instance of the ConfigurationUtil class.
     * @param applicationContext           The application context.
     */
    public ConfigurationLayout(
        DomibusConnectorPropertiesPersistenceService propertiesPersistenceService,
        ConfigurationUtil util, ApplicationContext applicationContext) {
        this.propertiesPersistenceService = propertiesPersistenceService;
        this.util = util;
        this.applicationContext = applicationContext;
    }

    /**
     * Initializes the ConfigurationLayout instance by creating tabs, setting up the page content,
     * and configuring the layout.
     *
     * @see ConfigurationLayout
     * @see DCTabHandler#createTabs(ApplicationContext, String)
     * @see DCTabHandler#getTabs()
     */
    @PostConstruct
    public void init() {
        dcTabHandler.createTabs(applicationContext, TAB_GROUP_NAME);

        pageContent = new Div();
        pageContent.setSizeFull();

        add(dcTabHandler.getTabs(), pageContent);

        this.expand(pageContent);
        this.setHeight("80vh");
    }

    /**
     * Shows the content in the router layout.
     *
     * @param content The content to be shown in the router layout. Must implement the HasElement
     *                interface.
     */
    @Override
    public void showRouterLayoutContent(HasElement content) {
        if (content != null) {
            pageContent.getElement()
                       .appendChild(Objects.requireNonNull(content.getElement()));
        }
    }

    /**
     * Creates the button bar for the configuration panel.
     *
     * @return The created horizontal layout containing the configuration buttons.
     */
    private HorizontalLayout createConfigurationButtonBar() {
        var resetActionText = "Discard Changes";
        resetConfiguration = new Button(
            new Icon(VaadinIcon.REFRESH));
        resetConfiguration.setText(resetActionText);
        resetConfiguration.addClickListener(e -> {
            var confirmButton = new Button(UiStyle.TAG_CONFIRM);
            var confirmDialog = createConfigurationConfirmDialog(
                resetActionText,
                confirmButton,
                "All changes since the last time of saving will be discarded."
            );
            confirmButton.addClickListener(e2 -> {
                util.resetConfiguration();
                confirmDialog.close();
            });

            confirmDialog.open();
        });

        var configurationButtonBar = new HorizontalLayout();
        var reset = new Div();
        reset.add(resetConfiguration);
        configurationButtonBar.add(reset);

        var saveActionText = "Save Changes";
        saveConfiguration = new Button(
            new Icon(VaadinIcon.EDIT));
        saveConfiguration.setText(saveActionText);
        saveConfiguration.addClickListener(e -> {
            var confirmButton = new Button(UiStyle.TAG_CONFIRM);
            var confirmDialog = createConfigurationConfirmDialog(
                saveActionText,
                confirmButton,
                "All changed configuration properties will be saved into the database "
                    + "table DOMIBUS_CONNECTOR_PROPERTIES.",
                "Be aware that changes to the configuration except backend client configuration "
                    + "will only take effect after restart of the domibusConnector.",
                "Also take note that the configured properties in the property files will NOT be "
                    + "changed!"
            );
            confirmButton.addClickListener(e2 -> {
                util.saveConfiguration();
                confirmDialog.close();
            });

            confirmDialog.open();
        });

        var save = new Div();
        save.add(saveConfiguration);
        configurationButtonBar.add(save);

        var reloadActionText = "Reload Configuration";
        reloadConfiguration = new Button(
            new Icon(VaadinIcon.FILE_REFRESH));
        reloadConfiguration.setText(reloadActionText);
        reloadConfiguration.addClickListener(e -> {
            var confirmButton = new Button(UiStyle.TAG_CONFIRM);
            var confirmDialog = createConfigurationConfirmDialog(
                reloadActionText,
                confirmButton,
                "All configuration properties will be reloaded to the state they had "
                    + "when the domibusConnector was started the last time.",
                "Be aware that this also effects configuration properties that have already been "
                    + "changed and saved since the last start of the domibusConnector!",
                "If there are changed properties that are already saved, those changes will be "
                    + "reset in the database to the status of the last startup as well."
            );
            confirmButton.addClickListener(e2 -> {
                util.reloadConfiguration();
                confirmDialog.close();
            });

            confirmDialog.open();
        });

        var reload = new Div();
        reload.add(reloadConfiguration);
        reload.getStyle().set("align", "right");
        configurationButtonBar.add(reload);

        configurationButtonBar.setWidth("900px");
        configurationButtonBar.expand(save);
        configurationButtonBar.setHeight("20px");
        configurationButtonBar.setPadding(true);
        configurationButtonBar.getStyle().set("padding-bottom", "30px");

        return configurationButtonBar;
    }

    private Dialog createConfigurationConfirmDialog(
        String headerString, Button confirmButton, String... infoStrings) {

        var headerContent = new Div();
        var header = new NativeLabel(headerString);
        header.getStyle().set("font-weight", "bold");
        header.getStyle().set("font-style", "italic");
        header.getStyle().set("margin-bottom", "10px");
        headerContent.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        headerContent.getStyle().set(UiStyle.TAG_PADDING, "10px");
        headerContent.getStyle().set("margin-bottom", "10px");
        headerContent.add(header);

        var confirmDialog = new Dialog();
        confirmDialog.add(headerContent);

        var contentLayout = new VerticalLayout();
        for (var infoString : infoStrings) {
            var infoLabel = new NativeLabel(infoString);
            infoLabel.getStyle().set("margin-top", "0px");
            contentLayout.add(infoLabel);
        }
        contentLayout.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        contentLayout.getStyle().set(UiStyle.TAG_PADDING, "0px");
        contentLayout.setAlignItems(Alignment.CENTER);

        var content = new Div();
        content.add(contentLayout);
        content.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        confirmDialog.add(content);

        var confirmCancelButtonContent = new Div();
        confirmCancelButtonContent.getStyle()
                                  .set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        confirmCancelButtonContent.getStyle().set(UiStyle.TAG_PADDING, "10px");
        confirmCancelButtonContent.add(confirmButton);

        var cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e3 -> confirmDialog.close());
        confirmCancelButtonContent.add(cancelButton);
        confirmDialog.add(confirmCancelButtonContent);

        return confirmDialog;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        dcTabHandler.beforeEnter(event);
    }
}
