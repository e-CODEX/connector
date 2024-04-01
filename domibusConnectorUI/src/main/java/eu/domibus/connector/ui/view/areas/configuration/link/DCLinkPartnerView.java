package eu.domibus.connector.ui.view.areas.configuration.link;


import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationOverview;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.domibus.connector.ui.view.areas.configuration.link.DCLinkConfigurationView.EDIT_MODE_TYPE_QUERY_PARAM;
import static eu.domibus.connector.ui.view.areas.configuration.link.DCLinkConfigurationView.LINK_TYPE_QUERY_PARAM;


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

    private final Label titleLabel = new Label("Edit LinkPartner");
    private Button discardButton;
    private Button saveButton;

    private LinkType linkType;
    private DomibusConnectorLinkPartner linkPartner;
    private EditMode editMode;
    private DomibusConnectorLinkConfiguration lnkConfig;

    public DCLinkPartnerView(DCLinkFacade dcLinkFacade, DCLinkPartnerField dcLinkPartnerField) {
        this.dcLinkFacade = dcLinkFacade;
        this.dcLinkPartnerField = dcLinkPartnerField;

        initUI();
    }

    private void initUI() {
        discardButton = new Button("Back");
        saveButton = new Button("Save");

        final HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.add(discardButton, saveButton);

        discardButton.addClickListener(this::discardButtonClicked);
        saveButton.addClickListener(this::saveButtonClicked);

        this.add(titleLabel);
        this.add(buttonBar);
        this.add(dcLinkPartnerField);

        dcLinkPartnerField.addValueChangeListener(this::dcLinkPartnerFieldValueChanged);
        dcLinkPartnerField.setValue(dcLinkPartnerField.getEmptyValue());
    }

    private void dcLinkPartnerFieldValueChanged(AbstractField.ComponentValueChangeEvent<CustomField<DomibusConnectorLinkPartner>, DomibusConnectorLinkPartner> customFieldDomibusConnectorLinkPartnerComponentValueChangeEvent) {
        this.linkPartner = customFieldDomibusConnectorLinkPartnerComponentValueChangeEvent.getValue();
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
            if (linkType == LinkType.GATEWAY) {
                ui.navigate(GatewayLinkConfiguration.class);
            } else if (linkType == LinkType.BACKEND) {
                ui.navigate(BackendLinkConfiguration.class);
            } else {
                ui.navigate(ConfigurationOverview.class);
            }
        });
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        Map<String, List<String>> parameters = location.getQueryParameters().getParameters();
        this.editMode = parameters.getOrDefault(EDIT_MODE_TYPE_QUERY_PARAM, Collections.emptyList())
                                  .stream().findFirst().map(EditMode::valueOf).orElse(EditMode.VIEW);
        this.linkType = parameters.getOrDefault(LINK_TYPE_QUERY_PARAM, Collections.emptyList())
                                  .stream().findFirst().map(LinkType::valueOf).orElse(null);
        String linkConfigName = parameters.getOrDefault(LINK_CONFIGURATION_NAME, Collections.emptyList())
                                          .stream().findFirst().orElse(null);

        DomibusConnectorLinkPartner.LinkPartnerName lp = new DomibusConnectorLinkPartner.LinkPartnerName(parameter);
        Optional<DomibusConnectorLinkPartner> optionalLinkPartner = dcLinkFacade.loadLinkPartner(lp);
        if (optionalLinkPartner.isPresent()) {
            DomibusConnectorLinkPartner linkPartner = optionalLinkPartner.get();
            this.lnkConfig = linkPartner.getLinkConfiguration();
            dcLinkPartnerField.setValue(dcLinkPartnerField.getEmptyValue()); // force update event
            dcLinkPartnerField.setValue(linkPartner);
            linkType = linkPartner.getLinkType();
            dcLinkPartnerField.setVisible(true);
            titleLabel.setText(EDIT_TITLE_LABEL_TEXT + " " + parameter);
            saveButton.setEnabled(linkPartner.getConfigurationSource() == ConfigurationSource.DB);
        } else if (editMode == EditMode.CREATE && linkConfigName != null) {
            Optional<DomibusConnectorLinkConfiguration> domibusConnectorLinkConfiguration =
                    dcLinkFacade.loadLinkConfig(new DomibusConnectorLinkConfiguration.LinkConfigName(linkConfigName));
            if (!domibusConnectorLinkConfiguration.isPresent()) {
                throw new IllegalArgumentException("Illegal parameter supplied");
            }
            this.lnkConfig = domibusConnectorLinkConfiguration.get();
            DomibusConnectorLinkPartner linkPartner = new DomibusConnectorLinkPartner();
            linkPartner.setConfigurationSource(ConfigurationSource.DB);
            linkPartner.setLinkConfiguration(this.lnkConfig);
            dcLinkPartnerField.setValue(linkPartner);
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
        } else if (editMode == EditMode.EDIT) {
            dcLinkPartnerField.setReadOnly(false);
            saveButton.setEnabled(linkPartner.getConfigurationSource() == ConfigurationSource.DB);
        } else if (editMode == EditMode.CREATE) {
            dcLinkPartnerField.setReadOnly(false);
            saveButton.setEnabled(linkPartner.getConfigurationSource() == ConfigurationSource.DB);
        }
    }
}
