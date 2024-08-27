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

import static eu.domibus.connector.ui.view.areas.configuration.link.DCLinkConfigurationView.EDIT_MODE_TYPE_QUERY_PARAM;
import static eu.domibus.connector.ui.view.areas.configuration.link.DCLinkConfigurationView.LINK_TYPE_QUERY_PARAM;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
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
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
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
 * The DCLinkPartnerView class represents a view for managing link partners in the DC Link service.
 */
@Component
@UIScope
@Route(value = DCLinkPartnerView.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
public class DCLinkPartnerView extends VerticalLayout implements HasUrlParameter<String> {
    public static final String ROUTE = "linkPartner";
    public static final String CREATE_TITLE_LABEL_TEXT = "Create LinkPartner";
    public static final String EDIT_TITLE_LABEL_TEXT = "Edit LinkPartner";
    public static final String LINK_CONFIGURATION_NAME = "ConfigName";
    private final DCLinkFacade dcLinkFacade;
    private final DCLinkPartnerField dcLinkPartnerField;
    private final NativeLabel titleLabel = new NativeLabel(EDIT_TITLE_LABEL_TEXT);
    private Button discardButton;
    private Button saveButton;
    private LinkType linkType;
    private DomibusConnectorLinkPartner linkPartner;
    private EditMode editMode;
    private DomibusConnectorLinkConfiguration lnkConfig;

    /**
     * Constructor.
     *
     * @param dcLinkFacade       the DCLinkFacade object used for communication with the DC Link
     *                           service
     * @param dcLinkPartnerField the DCLinkPartnerField object representing the partner field
     */
    public DCLinkPartnerView(DCLinkFacade dcLinkFacade, DCLinkPartnerField dcLinkPartnerField) {
        this.dcLinkFacade = dcLinkFacade;
        this.dcLinkPartnerField = dcLinkPartnerField;

        initUI();
    }

    private void initUI() {
        discardButton = new Button("Back");
        saveButton = new Button("Save");

        final var buttonBar = new HorizontalLayout();
        buttonBar.add(discardButton, saveButton);

        discardButton.addClickListener(this::discardButtonClicked);
        saveButton.addClickListener(this::saveButtonClicked);

        this.add(titleLabel);
        this.add(buttonBar);
        this.add(dcLinkPartnerField);

        dcLinkPartnerField.addValueChangeListener(this::dcLinkPartnerFieldValueChanged);
        dcLinkPartnerField.setValue(dcLinkPartnerField.getEmptyValue());
    }

    private void dcLinkPartnerFieldValueChanged(
        AbstractField.ComponentValueChangeEvent<CustomField<DomibusConnectorLinkPartner>,
            DomibusConnectorLinkPartner>
            customFieldDomibusConnectorLinkPartnerComponentValueChangeEvent) {
        this.linkPartner =
            customFieldDomibusConnectorLinkPartnerComponentValueChangeEvent.getValue();
    }

    private void saveButtonClicked(ClickEvent<Button> buttonClickEvent) {
        DomibusConnectorLinkPartner value = this.linkPartner;
        if (editMode == EditMode.EDIT || editMode == EditMode.CREATE) {
            value.setConfigurationSource(ConfigurationSource.DB);
            value.setLinkConfiguration(this.lnkConfig);
            value.setLinkType(this.linkType);
        }
        if (editMode == EditMode.EDIT) {
            dcLinkFacade.updateLinkPartner(value);
        } else if (editMode == EditMode.CREATE) {
            dcLinkFacade.createNewLinkPartner(value);
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
        this.editMode = parameters.getOrDefault(EDIT_MODE_TYPE_QUERY_PARAM, Collections.emptyList())
                                  .stream().findFirst().map(EditMode::valueOf)
                                  .orElse(EditMode.VIEW);
        this.linkType = parameters.getOrDefault(LINK_TYPE_QUERY_PARAM, Collections.emptyList())
                                  .stream().findFirst().map(LinkType::valueOf).orElse(null);
        String linkConfigName =
            parameters.getOrDefault(LINK_CONFIGURATION_NAME, Collections.emptyList())
                      .stream().findFirst().orElse(null);

        var linkPartnerName = new DomibusConnectorLinkPartner.LinkPartnerName(parameter);
        Optional<DomibusConnectorLinkPartner> optionalLinkPartner =
            dcLinkFacade.loadLinkPartner(linkPartnerName);
        if (optionalLinkPartner.isPresent()) {
            var linkPartner = optionalLinkPartner.get();
            this.lnkConfig = linkPartner.getLinkConfiguration();
            dcLinkPartnerField.setValue(dcLinkPartnerField.getEmptyValue()); // force update event
            dcLinkPartnerField.setValue(linkPartner);
            linkType = linkPartner.getLinkType();
            dcLinkPartnerField.setVisible(true);
            titleLabel.setText(EDIT_TITLE_LABEL_TEXT + " " + parameter);
            saveButton.setEnabled(linkPartner.getConfigurationSource() == ConfigurationSource.DB);
        } else if (editMode == EditMode.CREATE && linkConfigName != null) {
            Optional<DomibusConnectorLinkConfiguration> domibusConnectorLinkConfiguration =
                dcLinkFacade.loadLinkConfig(
                    new DomibusConnectorLinkConfiguration.LinkConfigName(linkConfigName));
            if (domibusConnectorLinkConfiguration.isEmpty()) {
                throw new IllegalArgumentException("Illegal parameter supplied");
            }
            this.lnkConfig = domibusConnectorLinkConfiguration.get();
            var connectorLinkPartner = new DomibusConnectorLinkPartner();
            connectorLinkPartner.setConfigurationSource(ConfigurationSource.DB);
            connectorLinkPartner.setLinkConfiguration(this.lnkConfig);
            dcLinkPartnerField.setValue(connectorLinkPartner);
            dcLinkPartnerField.setVisible(true);
            titleLabel.setText(CREATE_TITLE_LABEL_TEXT);
            saveButton.setEnabled(true);
        } else {
            titleLabel.setText(EDIT_TITLE_LABEL_TEXT + " [None]");
            dcLinkPartnerField.setVisible(false);
        }
        updateUI();
    }

    private void updateUI() {
        if (editMode == EditMode.VIEW) {
            saveButton.setEnabled(false);
            dcLinkPartnerField.setReadOnly(true);
        } else if (editMode == EditMode.EDIT || editMode == EditMode.CREATE) {
            dcLinkPartnerField.setReadOnly(false);
            saveButton.setEnabled(linkPartner.getConfigurationSource() == ConfigurationSource.DB);
        }
    }
}
