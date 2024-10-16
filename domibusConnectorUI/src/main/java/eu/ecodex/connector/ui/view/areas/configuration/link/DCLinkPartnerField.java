/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import eu.ecodex.connector.domain.enums.LinkMode;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.api.LinkPlugin;
import eu.ecodex.connector.link.api.PluginFeature;
import eu.ecodex.connector.link.service.DCActiveLinkManagerService;
import eu.ecodex.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.ecodex.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Represents a custom field used for configuring a Domibus connector link partner.
 */
@Component(DCLinkPartnerField.BEAN_NAME)
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("squid:S6830")
public class DCLinkPartnerField extends CustomField<DomibusConnectorLinkPartner> {
    public static final String BEAN_NAME = "DCLinkPartnerField";
    private final ApplicationContext applicationContext;
    private final DCActiveLinkManagerService linkManagerService;
    protected final SpringBeanValidationBinder<DomibusConnectorLinkPartner> binder;
    private TextField linkPartnerNameTextField;
    private TextField descriptionTextField;
    private Checkbox startOnConnectorStart;
    private ComboBox<LinkMode> rcvLinkModeComboBox;
    private ComboBox<LinkMode> sendLinkModeComboBox;
    private DCConfigurationPropertiesListField configPropsList;
    private DomibusConnectorLinkPartner linkPartner;

    /**
     * Constructor.
     *
     * @param applicationContext                The ApplicationContext object used for dependency
     *                                          injection.
     * @param springBeanValidationBinderFactory The SpringBeanValidationBinderFactory object used
     *                                          for creating instances of
     *                                          SpringBeanValidationBinder.
     * @param linkManagerService                The DCActiveLinkManagerService object used for
     *                                          managing link partners.
     * @param configurationPropertyCollector    The ConfigurationPropertyCollector object used for
     *                                          collecting configuration properties.
     */
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
        var layout = new VerticalLayout();
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
                  (DomibusConnectorLinkPartner p, String s) -> p.setLinkPartnerName(
                      new DomibusConnectorLinkPartner.LinkPartnerName(s)
                  )
              );
        layout.add(linkPartnerNameTextField);

        descriptionTextField = new TextField("Description");
        binder.bind(
            descriptionTextField, DomibusConnectorLinkPartner::getDescription,
            DomibusConnectorLinkPartner::setDescription
        );
        layout.add(descriptionTextField);

        startOnConnectorStart = new Checkbox("Start with connector start");
        binder.bind(
            startOnConnectorStart, DomibusConnectorLinkPartner::isEnabled,
            DomibusConnectorLinkPartner::setEnabled
        );
        layout.add(startOnConnectorStart);

        sendLinkModeComboBox = new ComboBox<>("Sender Mode");
        sendLinkModeComboBox.setItems(LinkMode.values());
        binder.forField(sendLinkModeComboBox)
              .asRequired()
              .withValidator(this::validateSendLinkMode)
              .bind(
                  DomibusConnectorLinkPartner::getSendLinkMode,
                  DomibusConnectorLinkPartner::setSendLinkMode
              );
        layout.add(sendLinkModeComboBox);

        rcvLinkModeComboBox = new ComboBox<>("Receiver Mode");
        rcvLinkModeComboBox.setItems(LinkMode.values());
        binder.forField(rcvLinkModeComboBox)
              .asRequired()
              .withValidator(this::validateRcvLinkMode)
              .bind(
                  DomibusConnectorLinkPartner::getRcvLinkMode,
                  DomibusConnectorLinkPartner::setRcvLinkMode
              );
        layout.add(rcvLinkModeComboBox);

        configPropsList = applicationContext.getBean(DCConfigurationPropertiesListField.class);
        configPropsList.setLabel("Link Partner Properties");
        configPropsList.setSizeFull();
        binder
            .forField(configPropsList)
            .bind(
                DomibusConnectorLinkPartner::getProperties,
                DomibusConnectorLinkPartner::setProperties
            );

        layout.add(configPropsList);
        configPropsList.setSizeFull();

        updateUI();
    }

    private ValidationResult validateSendLinkMode(LinkMode linkMode, ValueContext valueContext) {
        Optional<LinkPlugin> linkPluginByName = getLinkPlugin();
        if (linkPluginByName.isPresent()) {
            List<LinkMode> rcvItems = linkPluginByName.get()
                                                      .getFeatures()
                                                      .stream()
                                                      .map(feature -> switch (feature) {
                                                          case PluginFeature.SEND_PUSH_MODE ->
                                                              LinkMode.PUSH;
                                                          case PluginFeature.SEND_PASSIVE_MODE ->
                                                              LinkMode.PASSIVE;
                                                          default -> null;
                                                      }).filter(Objects::nonNull)
                                                      .toList();
            if (rcvItems.contains(linkMode)) {
                return ValidationResult.ok();
            } else {
                return ValidationResult.error(String.format(
                    "Only [%s] LinkModes  are supported",
                    rcvItems.stream()
                            .map(LinkMode::toString)
                            .collect(
                                Collectors.joining(","))
                ));
            }
        }
        return ValidationResult.ok();
    }

    private ValidationResult validateRcvLinkMode(LinkMode linkMode, ValueContext valueContext) {
        Optional<LinkPlugin> linkPluginByName = getLinkPlugin();
        if (linkPluginByName.isPresent()) {
            List<LinkMode> rcvItems = linkPluginByName.get().getFeatures()
                                                      .stream()
                                                      .map(feature -> switch (feature) {
                                                          case PluginFeature.RCV_PULL_MODE ->
                                                              LinkMode.PULL;
                                                          case PluginFeature.RCV_PASSIVE_MODE ->
                                                              LinkMode.PASSIVE;
                                                          default -> null;
                                                      }).filter(Objects::nonNull)
                                                      .toList();
            if (rcvItems.contains(linkMode)) {
                return ValidationResult.ok();
            } else {
                return ValidationResult.error(String.format(
                    "Only [%s] LinkModes  are supported",
                    rcvItems.stream()
                            .map(LinkMode::toString)
                            .collect(
                                Collectors.joining(","))
                ));
            }
        }
        return ValidationResult.ok();
    }

    private Optional<LinkPlugin> getLinkPlugin() {
        Optional<LinkPlugin> linkPluginByName = Optional.empty();
        if (linkPartner != null && linkPartner.getLinkConfiguration() != null) {
            linkPluginByName = linkManagerService.getLinkPluginByName(
                linkPartner.getLinkConfiguration().getLinkImpl());
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

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        updateUI();
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new DomibusConnectorLinkPartner();
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

        if (domibusConnectorLinkPartner != null
            && domibusConnectorLinkPartner.getLinkConfiguration() != null) {
            linkManagerService.getLinkPluginByName(
                                  domibusConnectorLinkPartner.getLinkConfiguration().getLinkImpl())
                              .ifPresent(this::updateConfigurationProperties);
        } else {
            this.updateConfigurationProperties(null);
        }
        updateUI();
    }
}
