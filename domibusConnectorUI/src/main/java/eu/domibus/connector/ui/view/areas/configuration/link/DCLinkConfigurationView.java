/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationOverview;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * This class represents the view for configuring DC link settings.
 */
@Component
@UIScope
@Route(value = DCLinkConfigurationView.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
public class DCLinkConfigurationView extends VerticalLayout implements HasUrlParameter<String> {
    public static final String ROUTE = "linkConfig";
    public static final String LINK_TYPE_QUERY_PARAM = "linkType";
    public static final String EDIT_MODE_TYPE_QUERY_PARAM = "modeType";
    public static final String TITLE_LABEL_TEXT = "Edit LinkConfiguration";
    private final DCLinkFacade dcLinkFacade;
    private final DCLinkConfigurationField linkConfigurationField;
    private final NativeLabel titleLabel = new NativeLabel();
    private final Button discardButton = new Button("Back");
    private final Button saveButton = new Button("Save");
    private LinkType linkType;
    private EditMode editMode;
    private DomibusConnectorLinkConfiguration linkConfig;

    /**
     * Constructs a new instance of the {@code DCLinkConfigurationView} class.
     *
     * @param dcLinkFacade           the {@code DCLinkFacade} instance used for accessing DC link
     *                               functionality
     * @param linkConfigurationField the {@code DCLinkConfigurationField} instance used for
     *                               configuring DC links
     */
    public DCLinkConfigurationView(
        DCLinkFacade dcLinkFacade, DCLinkConfigurationField linkConfigurationField) {
        this.dcLinkFacade = dcLinkFacade;
        this.linkConfigurationField = linkConfigurationField;

        this.initUI();
    }

    private void initUI() {

        discardButton.addClickListener(this::discardButtonClicked);
        saveButton.addClickListener(this::saveButtonClicked);

        var buttonBar = new HorizontalLayout();
        buttonBar.add(discardButton, saveButton);

        this.add(titleLabel, buttonBar, linkConfigurationField);
    }

    private void saveButtonClicked(ClickEvent<Button> buttonClickEvent) {
        DomibusConnectorLinkConfiguration value = linkConfigurationField.getValue();
        if (editMode == EditMode.EDIT) {
            value.setConfigurationSource(ConfigurationSource.DB);
            dcLinkFacade.updateLinkConfig(value);
        } else if (editMode == EditMode.CREATE) {
            value.setConfigurationSource(ConfigurationSource.DB);
            dcLinkFacade.createNewLinkConfiguration(value);
        }
        navigateBack();
    }

    private void discardButtonClicked(ClickEvent<Button> buttonClickEvent) {
        navigateBack();
    }

    private void navigateBack() {
        getUI().ifPresent(ui -> {
            switch (linkType) {
                case LinkType.GATEWAY -> ui.navigate(GatewayLinkConfiguration.class);
                case LinkType.BACKEND -> ui.navigate(BackendLinkConfiguration.class);
                default -> ui.navigate(ConfigurationOverview.class);
            }
        });
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        var location = event.getLocation();
        Map<String, List<String>> parameters = location.getQueryParameters().getParameters();
        this.linkType = parameters.getOrDefault(LINK_TYPE_QUERY_PARAM, Collections.emptyList())
                                  .stream().findFirst().map(LinkType::valueOf).orElse(null);
        this.editMode = parameters.getOrDefault(EDIT_MODE_TYPE_QUERY_PARAM, Collections.emptyList())
                                  .stream().findFirst().map(EditMode::valueOf)
                                  .orElse(EditMode.VIEW);

        var configName = new DomibusConnectorLinkConfiguration.LinkConfigName((parameter));
        Optional<DomibusConnectorLinkConfiguration> optionalConfig =
            dcLinkFacade.loadLinkConfig(configName);
        if (optionalConfig.isPresent()) {
            DomibusConnectorLinkConfiguration linkConfig = optionalConfig.get();
            linkConfigurationField.setValue(
                linkConfigurationField.getEmptyValue()); // force value change event
            linkConfigurationField.setValue(linkConfig);
            linkConfigurationField.setVisible(true);
            this.linkConfig = linkConfig;
            titleLabel.setText(TITLE_LABEL_TEXT + " " + parameter);
        } else if (editMode == EditMode.CREATE) {
            var linkConfig = new DomibusConnectorLinkConfiguration();
            linkConfig.setConfigurationSource(ConfigurationSource.DB);
            linkConfig.setConfigName(
                new DomibusConnectorLinkConfiguration.LinkConfigName("New Link Config"));
            linkConfigurationField.setValue(linkConfig);
            linkConfigurationField.setVisible(true);
            this.linkConfig = linkConfig;
            titleLabel.setText(TITLE_LABEL_TEXT + " new config");
        } else {
            titleLabel.setText(TITLE_LABEL_TEXT + " [None]");
            linkConfigurationField.setVisible(false);
        }
        linkConfigurationField.setEditMode(editMode);
        updateUI();
    }

    private void updateUI() {
        if (editMode == EditMode.VIEW) {
            saveButton.setEnabled(false);
        } else if (editMode == EditMode.EDIT) {
            saveButton.setEnabled(linkConfig.getConfigurationSource() == ConfigurationSource.DB);
        } else if (editMode == EditMode.CREATE) {
            saveButton.setEnabled(linkConfig.getConfigurationSource() == ConfigurationSource.DB);
        }
    }
}
