package eu.domibus.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.function.ValueProvider;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DCLinkConfigurationField extends CustomField<DomibusConnectorLinkConfiguration> {
    private static final Logger LOGGER = LogManager.getLogger(DCLinkConfigurationField.class);

    private final ApplicationContext applicationContext;
    private final DCActiveLinkManagerService linkManagerService;

    private final ComboBox<LinkPlugin> implChooser = new ComboBox<>();
    private DCConfigurationPropertiesListField configPropsList;
    private TextField linkConfigName;
    private final SpringBeanValidationBinder<DomibusConnectorLinkConfiguration> binder;
    private boolean readOnly = false;
    private boolean implAndConfigNameReadOnly = true;

    private DomibusConnectorLinkConfiguration value;

    public DCLinkConfigurationField(
            ApplicationContext applicationContext,
            DCActiveLinkManagerService linkManagerService,
            SpringBeanValidationBinderFactory springBeanValidationBinderFactory
    ) {
        this.applicationContext = applicationContext;
        this.linkManagerService = linkManagerService;

        binder = springBeanValidationBinderFactory.create(DomibusConnectorLinkConfiguration.class);
        binder.addValueChangeListener(this::valueChanged);

        initUI();
    }

    private void initUI() {
        VerticalLayout layout = new VerticalLayout();
        this.add(layout);

        linkConfigName = new TextField("Link Configuration Name");
        linkConfigName.setReadOnly(readOnly);

        implChooser.setItems(linkManagerService.getAvailableLinkPlugins());
        implChooser.setLabel("Link Implementation");
        implChooser.setItemLabelGenerator((ItemLabelGenerator<LinkPlugin>) LinkPlugin::getPluginName);
        implChooser.addValueChangeListener(this::choosenLinkImplChanged);
        implChooser.setMinWidth("10em");
        implChooser.setReadOnly(readOnly);

        binder
                .forField(linkConfigName)
                .withValidator((Validator<String>) (value, context) -> {
                    if (!StringUtils.hasText(value)) {
                        return ValidationResult.error("Must not be emtpy!");
                    }
                    return ValidationResult.ok();
                })
                .bind(
                        (ValueProvider<DomibusConnectorLinkConfiguration, String>) linkConfiguration ->
                                linkConfiguration.getConfigName() == null ? "" : linkConfiguration
                                        .getConfigName().toString(),
                        (Setter<DomibusConnectorLinkConfiguration, String>) (linkConfiguration, configName) ->
                                linkConfiguration.setConfigName(
                                        configName == null ? new DomibusConnectorLinkConfiguration.LinkConfigName("") :
                                                new DomibusConnectorLinkConfiguration.LinkConfigName(
                                                        configName))
                );

        binder
                .forField(implChooser)
                .withValidator((Validator<? super LinkPlugin>) (value, context) -> {
                    if (value == null) {
                        return ValidationResult.error("Must be set!");
                    }
                    return ValidationResult.ok();
                })
                .bind(
                        (ValueProvider<DomibusConnectorLinkConfiguration, LinkPlugin>) linkConfiguration -> {
                            Optional<LinkPlugin> linkPlugin =
                                    linkManagerService.getLinkPluginByName(linkConfiguration.getLinkImpl());
                            if (linkPlugin.isPresent()) {
                                return linkPlugin.get();
                            } else {
                                LOGGER.warn("No Implementation found for [{}]", linkConfiguration.getLinkImpl());
                                return null;
                            }
                        },
                        (Setter<DomibusConnectorLinkConfiguration, LinkPlugin>) (linkConfiguration, linkPlugin) ->
                                linkConfiguration.setLinkImpl(
                                        linkPlugin == null ? null : linkPlugin.getPluginName())
                );

        configPropsList = applicationContext.getBean(DCConfigurationPropertiesListField.class);
        configPropsList.setLabel("Link Configuration Properties");
        configPropsList.setSizeFull();
        binder
                .forField(configPropsList)
                .bind(
                        DomibusConnectorLinkConfiguration::getProperties,
                        DomibusConnectorLinkConfiguration::setProperties
                );

        layout.add(linkConfigName, implChooser, configPropsList);

        updateUI();
    }

    private void updateUI() {
        implChooser.setReadOnly(readOnly || implAndConfigNameReadOnly);
        linkConfigName.setReadOnly(readOnly || implAndConfigNameReadOnly);
        configPropsList.setReadOnly(readOnly);
    }

    public void setEditMode(EditMode editMode) {
        if (editMode == EditMode.EDIT) {
            this.setReadOnly(false);
            this.setImplAndConfigNameReadOnly(true);
        } else if (editMode == EditMode.CREATE) {
            this.setReadOnly(false);
            this.setImplAndConfigNameReadOnly(false);
        } else if (editMode == EditMode.VIEW || editMode == EditMode.DEL) {
            this.setReadOnly(true);
        }
    }

    private void setImplAndConfigNameReadOnly(boolean implAndConfigNameReadOnly) {
        this.implAndConfigNameReadOnly = implAndConfigNameReadOnly;
        updateUI();
    }

    private void choosenLinkImplChanged(HasValue.ValueChangeEvent<LinkPlugin> valueChangeEvent) {
        LinkPlugin value = valueChangeEvent.getValue();
        updateConfigurationProperties(value);
        updateUI();
    }

    private void updateConfigurationProperties(LinkPlugin value) {
        List<Class<?>> configurationClasses = new ArrayList<>();
        if (value != null) {
            configurationClasses = value.getPluginConfigurationProperties();
        }
        if (configurationClasses != null) {
            configPropsList.setConfigurationClasses(configurationClasses);
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
        this.readOnly = readOnly;
        super.setReadOnly(readOnly);
        updateUI();
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        DomibusConnectorLinkConfiguration changedValue = new DomibusConnectorLinkConfiguration();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected DomibusConnectorLinkConfiguration generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(DomibusConnectorLinkConfiguration linkConfig) {
        if (linkConfig != null) {
            linkManagerService.getLinkPluginByName(linkConfig.getLinkImpl())
                              .ifPresent(this::updateConfigurationProperties);
        } else {
            this.updateConfigurationProperties(null);
        }
        binder.readBean(linkConfig);
        updateUI();
    }

    @Override
    public DomibusConnectorLinkConfiguration getValue() {
        return value;
    }
}
