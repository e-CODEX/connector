package eu.domibus.connector.ui.view.areas.configuration.link.wizard;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.server.Command;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.ui.component.WizardStep;
import eu.domibus.connector.ui.view.areas.configuration.link.LnkConfigItem;
import org.springframework.util.StringUtils;


public class ChooseImplStep extends VerticalLayout implements WizardStep {
    // private final DCActiveLinkManagerService linkManagerService;
    private final DCLinkFacade dcLinkFacade;
    private final Binder<LnkConfigItem> lnkConfigItemBinder;
    private final VerticalLayout newLinkConfigDiv = new VerticalLayout();
    private final ComboBox<LinkPlugin> implChooser = new ComboBox<>();
    private final VerticalLayout existingLinkConfigDiv = new VerticalLayout();
    private final ComboBox<DomibusConnectorLinkConfiguration> linkConfigurationComboBox = new ComboBox<>();
    private RadioButtonGroup<LnkConfigItem.NewConfig> newConfig;
    private TextField linkConfigName;

    public ChooseImplStep(DCLinkFacade dcLinkFacade, Binder<LnkConfigItem> lnkConfigItem) {
        this.dcLinkFacade = dcLinkFacade;
        this.lnkConfigItemBinder = lnkConfigItem;

        lnkConfigItemBinder.addValueChangeListener(this::lnkCOnfigItemValueChanged);
        initUI();
    }

    private void lnkCOnfigItemValueChanged(HasValue.ValueChangeEvent<?> valueChangeEvent) {
        Object value = valueChangeEvent.getValue();
        if (value instanceof LnkConfigItem) {
        }
    }

    private void newConfigValueChanged(
            AbstractField.ComponentValueChangeEvent<RadioButtonGroup<LnkConfigItem.NewConfig>,
                    LnkConfigItem.NewConfig> event) {
        if (event.getValue() == LnkConfigItem.NewConfig.NEW_LINK_CONFIG) {
            // TODO: set visible for new link configuration, editable name, choose plugin impl...
            //            implChooser.setReadOnly(false);
            //            linkConfigName.setReadOnly(false);
            newLinkConfigDiv.setVisible(true);
            existingLinkConfigDiv.setVisible(false);
        } else if (event.getValue() == LnkConfigItem.NewConfig.EXISTING_LINK_CONFIG) {
            // TODO: set visible for existing link config, choose existing link config...
            newLinkConfigDiv.setVisible(false);
            existingLinkConfigDiv.setVisible(true);
        } else {
            throw new IllegalStateException("Should not end here! Unknown LnkConfigItem.NewConfig item" + event.getValue());
        }
    }

    private void initUI() {
        newConfig = new RadioButtonGroup<>();
        newConfig.setItems(LnkConfigItem.NewConfig.values());
        newConfig.addValueChangeListener(this::newConfigValueChanged);

        lnkConfigItemBinder.bind(
                newConfig,
                (ValueProvider<LnkConfigItem, LnkConfigItem.NewConfig>) LnkConfigItem::getNewConfig,
                (Setter<LnkConfigItem, LnkConfigItem.NewConfig>) LnkConfigItem::setNewConfig
        );

        this.add(newConfig);

        initNewLinkConfigDiv();
        initCreateLinkConfigDiv();

        newConfig.setValue(LnkConfigItem.NewConfig.NEW_LINK_CONFIG);

        updateUI();
    }

    private void initCreateLinkConfigDiv() {
        existingLinkConfigDiv.add(linkConfigurationComboBox);
        linkConfigurationComboBox.setItemLabelGenerator(c -> c.getConfigName().toString());
        linkConfigurationComboBox.setItems(dcLinkFacade.getAllLinkConfigurations(LinkType.GATEWAY));

        lnkConfigItemBinder
                .forField(linkConfigurationComboBox)
                .bind(
                        (ValueProvider<LnkConfigItem, DomibusConnectorLinkConfiguration>) LnkConfigItem::getLinkConfiguration,
                        (Setter<LnkConfigItem, DomibusConnectorLinkConfiguration>) LnkConfigItem::setLinkConfiguration
                );

        this.add(existingLinkConfigDiv);
    }

    private void initNewLinkConfigDiv() {
        this.add(newLinkConfigDiv);
        linkConfigName = new TextField("Link Configuration Name");
        newLinkConfigDiv.add(linkConfigName);

        lnkConfigItemBinder
                .forField(linkConfigName)
                .withValidator((Validator<String>) (value, context) -> {
                    if (!StringUtils.hasText(value)) {
                        return ValidationResult.error("Must not be emtpy!");
                    }
                    return ValidationResult.ok();
                })
                .bind(
                        (ValueProvider<LnkConfigItem, String>) linkConfiguration -> linkConfiguration
                                .getLinkConfiguration().getConfigName() == null ? "" : linkConfiguration
                                .getLinkConfiguration().getConfigName().toString(),
                        (Setter<LnkConfigItem, String>) (linkConfiguration, configName) -> linkConfiguration
                                .getLinkConfiguration()
                                .setConfigName(configName == null ?
                                                       new DomibusConnectorLinkConfiguration.LinkConfigName(
                                                               "") :
                                                       new DomibusConnectorLinkConfiguration.LinkConfigName(
                                                               configName))
                );

        implChooser.setLabel("Link Implementation");
        implChooser.setItemLabelGenerator((ItemLabelGenerator<LinkPlugin>) LinkPlugin::getPluginName);
        implChooser.setItems(dcLinkFacade.getAvailableLinkPlugins(getLinkType()));
        //        implChooser.addValueChangeListener(this::choosenLinkImplChanged);
        implChooser.setMinWidth("10em");
        newLinkConfigDiv.add(implChooser);

        lnkConfigItemBinder
                .forField(implChooser)
                .withValidator((Validator<? super LinkPlugin>) (value, context) -> {
                    if (value == null) {
                        return ValidationResult.error("Must be set!");
                    }
                    return ValidationResult.ok();
                })
                .bind(
                        (ValueProvider<LnkConfigItem, LinkPlugin>) LnkConfigItem::getLinkPlugin,
                        (Setter<LnkConfigItem, LinkPlugin>) LnkConfigItem::setLinkPlugin
                );
    }

    private LinkType getLinkType() {
        return this.lnkConfigItemBinder.getBean().getLinkType();
    }

    private void updateUI() {
        // implChooser.setReadOnly(readOnly && !implChangeAble);
        // linkConfigName.setReadOnly(readOnly);
        // configPropsList.setReadOnly(readOnly);

    }

    public void setImplChangeAble(boolean changeAble) {
        // this.implChangeAble = changeAble;
        // implChooser.setReadOnly(readOnly && !implChangeAble);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getStepTitle() {
        return "Choose Link Implementation";
    }

    @Override
    public void onForward(Command onForwardExecute) {
    }
}
