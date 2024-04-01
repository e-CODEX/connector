package eu.domibus.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationOverview;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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

    private final Label titleLabel = new Label();
    private final Button discardButton = new Button("Back");
    private final Button saveButton = new Button("Save");

    private LinkType linkType;
    private EditMode editMode;
    private DomibusConnectorLinkConfiguration linkConfig;

    public DCLinkConfigurationView(DCLinkFacade dcLinkFacade, DCLinkConfigurationField linkConfigurationField) {
        this.dcLinkFacade = dcLinkFacade;
        this.linkConfigurationField = linkConfigurationField;

        this.initUI();
    }

    private void initUI() {
        discardButton.addClickListener(this::discardButtonClicked);
        saveButton.addClickListener(this::saveButtonClicked);

        HorizontalLayout buttonBar = new HorizontalLayout();
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
        this.linkType = parameters.getOrDefault(LINK_TYPE_QUERY_PARAM, Collections.emptyList())
                                  .stream().findFirst().map(LinkType::valueOf).orElse(null);
        this.editMode = parameters.getOrDefault(EDIT_MODE_TYPE_QUERY_PARAM, Collections.emptyList())
                                  .stream().findFirst().map(EditMode::valueOf).orElse(EditMode.VIEW);

        DomibusConnectorLinkConfiguration.LinkConfigName configName =
                new DomibusConnectorLinkConfiguration.LinkConfigName((parameter));
        Optional<DomibusConnectorLinkConfiguration> optionalConfig = dcLinkFacade.loadLinkConfig(configName);
        if (optionalConfig.isPresent()) {
            DomibusConnectorLinkConfiguration linkConfig = optionalConfig.get();
            linkConfigurationField.setValue(linkConfigurationField.getEmptyValue()); // force value change event
            linkConfigurationField.setValue(linkConfig);
            //            linkConfigPanel.setImplAndConfigNameReadOnly(true);
            linkConfigurationField.setVisible(true);
            this.linkConfig = linkConfig;
            titleLabel.setText(TITLE_LABEL_TEXT + " " + parameter);
        } else if (editMode == EditMode.CREATE) {
            DomibusConnectorLinkConfiguration linkConfig = new DomibusConnectorLinkConfiguration();
            linkConfig.setConfigurationSource(ConfigurationSource.DB);
            linkConfig.setConfigName(new DomibusConnectorLinkConfiguration.LinkConfigName("New Link Config"));
            linkConfigurationField.setValue(linkConfig);
            //            linkConfigPanel.setImplAndConfigNameReadOnly(false);
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
