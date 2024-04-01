package eu.domibus.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import eu.domibus.connector.domain.enums.LinkMode;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.api.PluginFeature;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Component(DCLinkPartnerField.BEAN_NAME)
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DCLinkPartnerField extends CustomField<DomibusConnectorLinkPartner> {
    public static final String BEAN_NAME = "DCLinkPartnerField";
    private static final Logger LOGGER = LogManager.getLogger(DCLinkPartnerField.class);
    protected final SpringBeanValidationBinder<DomibusConnectorLinkPartner> binder;
    private final ApplicationContext applicationContext;
    private final DCActiveLinkManagerService linkManagerService;
    private TextField linkPartnerNameTextField;
    private TextField descriptionTextField;
    private Checkbox startOnConnectorStart;
    private ComboBox<LinkMode> rcvLinkModeComboBox;
    private ComboBox<LinkMode> sendLinkModeComboBox;
    private DCConfigurationPropertiesListField configPropsList;

    private DomibusConnectorLinkPartner linkPartner;

    public DCLinkPartnerField(
            ApplicationContext applicationContext,
            SpringBeanValidationBinderFactory springBeanValidationBinderFactory,
            DCActiveLinkManagerService linkManagerService,
            ConfigurationPropertyCollector configurationPropertyCollector) {
        this.applicationContext = applicationContext;
        this.linkManagerService = linkManagerService;

        binder = springBeanValidationBinderFactory.create(DomibusConnectorLinkPartner.class);

        initUI();
    }

    private void initUI() {
        VerticalLayout layout = new VerticalLayout();
        this.add(layout);

        binder.addValueChangeListener(this::valueChanged);

        linkPartnerNameTextField = new TextField("Link Partner Name");
        binder.forField(linkPartnerNameTextField)
              .asRequired()
              .withValidator((Validator<String>) (value, context) -> {
                  if (value.isEmpty()) {
                      return ValidationResult.error("Is empty!");
                  }
                  return ValidationResult.ok();
              })
              .bind(
                      p -> p.getLinkPartnerName() == null ? null : p.getLinkPartnerName().getLinkName(),
                      (DomibusConnectorLinkPartner p, String s) -> {
                          p.setLinkPartnerName(new DomibusConnectorLinkPartner.LinkPartnerName(s));
                      }
              );
        layout.add(linkPartnerNameTextField);

        descriptionTextField = new TextField("Description");
        binder.bind(
                descriptionTextField,
                DomibusConnectorLinkPartner::getDescription,
                DomibusConnectorLinkPartner::setDescription
        );
        layout.add(descriptionTextField);

        startOnConnectorStart = new Checkbox("Start with connector start");
        binder.bind(
                startOnConnectorStart,
                DomibusConnectorLinkPartner::isEnabled,
                DomibusConnectorLinkPartner::setEnabled
        );
        layout.add(startOnConnectorStart);

        sendLinkModeComboBox = new ComboBox<>("Sender Mode");
        sendLinkModeComboBox.setItems(LinkMode.values());
        binder.forField(sendLinkModeComboBox)
              .asRequired()
              .withValidator(this::validateSendLinkMode)
              .bind(DomibusConnectorLinkPartner::getSendLinkMode, DomibusConnectorLinkPartner::setSendLinkMode);
        layout.add(sendLinkModeComboBox);

        rcvLinkModeComboBox = new ComboBox<>("Receiver Mode");
        rcvLinkModeComboBox.setItems(LinkMode.values());
        binder.forField(rcvLinkModeComboBox)
              .asRequired()
              .withValidator(this::validateRcvLinkMode)
              .bind(DomibusConnectorLinkPartner::getRcvLinkMode, DomibusConnectorLinkPartner::setRcvLinkMode);
        layout.add(rcvLinkModeComboBox);

        configPropsList = applicationContext.getBean(DCConfigurationPropertiesListField.class);
        configPropsList.setLabel("Link Partner Properties");
        configPropsList.setSizeFull();
        binder
                .forField(configPropsList)
                .bind(DomibusConnectorLinkPartner::getProperties, DomibusConnectorLinkPartner::setProperties);

        layout.add(configPropsList);
        configPropsList.setSizeFull();

        updateUI();
    }

    private ValidationResult validateSendLinkMode(LinkMode linkMode, ValueContext valueContext) {
        Optional<LinkPlugin> linkPluginByName = getLinkPlugin();
        if (linkPluginByName.isPresent()) {
            List<LinkMode> rcvItems = linkPluginByName
                    .get().getFeatures()
                    .stream()
                    .map(feature -> {
                        if (PluginFeature.SEND_PUSH_MODE == feature) {
                            return LinkMode.PUSH;
                        } else if (PluginFeature.SEND_PASSIVE_MODE == feature) {
                            return LinkMode.PASSIVE;
                        } else {
                            return null;
                        }
                    }).filter(Objects::nonNull).collect(Collectors.toList());
            if (rcvItems.contains(linkMode)) {
                return ValidationResult.ok();
            } else {
                return ValidationResult.error(String.format(
                        "Only [%s] LinkModes  are supported",
                        rcvItems.stream().map(LinkMode::toString).collect(Collectors.joining(","))
                ));
            }
        }
        return ValidationResult.ok();
    }

    private ValidationResult validateRcvLinkMode(LinkMode linkMode, ValueContext valueContext) {
        Optional<LinkPlugin> linkPluginByName = getLinkPlugin();
        if (linkPluginByName.isPresent()) {
            List<LinkMode> rcvItems = linkPluginByName
                    .get().getFeatures()
                    .stream()
                    .map(feature -> {
                        if (PluginFeature.RCV_PULL_MODE == feature) {
                            return LinkMode.PULL;
                        } else if (PluginFeature.RCV_PASSIVE_MODE == feature) {
                            return LinkMode.PASSIVE;
                        } else {
                            return null;
                        }
                    }).filter(Objects::nonNull).collect(Collectors.toList());
            if (rcvItems.contains(linkMode)) {
                return ValidationResult.ok();
            } else {
                return ValidationResult.error(String.format(
                        "Only [%s] LinkModes  are supported",
                        rcvItems.stream().map(LinkMode::toString).collect(Collectors.joining(","))
                ));
            }
        }
        return ValidationResult.ok();
    }

    private Optional<LinkPlugin> getLinkPlugin() {
        Optional<LinkPlugin> linkPluginByName = Optional.empty();
        if (linkPartner != null && linkPartner.getLinkConfiguration() != null) {
            linkPluginByName =
                    linkManagerService.getLinkPluginByName(linkPartner.getLinkConfiguration().getLinkImpl());
        }
        return linkPluginByName;
    }

    private void updateUI() {
        boolean ro = isReadOnly();
        binder.setReadOnly(ro);
    }

    private void updateConfigurationProperties(LinkPlugin linkPlugin) {
        if (linkPlugin == null) {
            configPropsList.setConfigurationClasses(new ArrayList<>());
        } else {
            configPropsList.setConfigurationClasses(linkPlugin.getPartnerConfigurationProperties());
        }
    }

    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        updateUI();
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        DomibusConnectorLinkPartner changedValue = new DomibusConnectorLinkPartner();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        linkPartner = changedValue;
    }

    @Override
    protected DomibusConnectorLinkPartner generateModelValue() {
        return linkPartner;
    }

    @Override
    protected void setPresentationValue(DomibusConnectorLinkPartner domibusConnectorLinkPartner) {
        binder.readBean(domibusConnectorLinkPartner);

        if (domibusConnectorLinkPartner != null && domibusConnectorLinkPartner.getLinkConfiguration() != null) {
            linkManagerService
                    .getLinkPluginByName(domibusConnectorLinkPartner.getLinkConfiguration().getLinkImpl())
                    .ifPresent(this::updateConfigurationProperties);
        } else {
            this.updateConfigurationProperties(null);
        }
        updateUI();
    }
}
