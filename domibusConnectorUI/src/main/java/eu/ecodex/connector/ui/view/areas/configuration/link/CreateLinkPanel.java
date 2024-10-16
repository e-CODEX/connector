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

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.service.DCLinkFacade;
import eu.ecodex.connector.persistence.service.DCLinkPersistenceService;
import eu.ecodex.connector.ui.component.WizardComponent;
import eu.ecodex.connector.ui.component.WizardStep;
import eu.ecodex.connector.ui.view.areas.configuration.link.wizard.ChooseImplStep;
import eu.ecodex.connector.ui.view.areas.configuration.link.wizard.CreateLinkConfigurationStep;
import eu.ecodex.connector.ui.view.areas.configuration.link.wizard.CreateLinkPartnerStep;
import java.time.Duration;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Scope;

/**
 * The {@code CreateLinkPanel} class represents a panel for creating a new link configuration using
 * a wizard component.
 */
@Scope(SCOPE_PROTOTYPE)
@org.springframework.stereotype.Component
@Route("createlink")
@lombok.Setter
@Getter
@SuppressWarnings("squid:S1135")
public class CreateLinkPanel extends VerticalLayout {
    private static final Logger LOGGER = LogManager.getLogger(CreateLinkPanel.class);
    private final DCLinkPersistenceService dcLinkPersistenceService;
    private final DCLinkFacade dcLinkFacade;
    private final ObjectProvider<CreateLinkPartnerStep> createLinkPartnerStepProvider;
    private final ObjectProvider<CreateLinkConfigurationStep> createLinkConfigurationStepProvider;
    private LinkType linkType;

    /**
     * Constructor.
     *
     * @param dcLinkPersistenceService            The service for persisting DCLink entities.
     * @param dcLinkFacade                        The facade for accessing and manipulating DCLink
     *                                            entities.
     * @param createLinkPartnerStepProvider       An object provider for CreateLinkPartnerStep
     *                                            instances.
     * @param createLinkConfigurationStepProvider An object provider for CreateLinkConfigurationStep
     *                                            instances.
     */
    public CreateLinkPanel(
        DCLinkPersistenceService dcLinkPersistenceService,
        DCLinkFacade dcLinkFacade,
        ObjectProvider<CreateLinkPartnerStep> createLinkPartnerStepProvider,
        ObjectProvider<CreateLinkConfigurationStep> createLinkConfigurationStepProvider
    ) {
        this.dcLinkFacade = dcLinkFacade;
        this.createLinkPartnerStepProvider = createLinkPartnerStepProvider;
        this.createLinkConfigurationStepProvider = createLinkConfigurationStepProvider;
        this.dcLinkPersistenceService = dcLinkPersistenceService;
        init();
    }

    private WizardComponent wizard;
    private Dialog parentDialog;
    private Binder<LnkConfigItem> linkConfigItemBinder = new Binder<>();
    private LnkConfigItem linkConfigItem;
    private DomibusConnectorLinkConfiguration linkConfiguration;
    private DomibusConnectorLinkPartner linkPartner;

    private void init() {
        linkConfigItem = new LnkConfigItem();
        linkConfigItem.setLinkType(getLinkType());
        linkConfigItemBinder.setBean(linkConfigItem);

        linkConfiguration = new DomibusConnectorLinkConfiguration();
        linkPartner = new DomibusConnectorLinkPartner();
        linkPartner.setLinkType(getLinkType());
        linkPartner.setEnabled(true);
        linkPartner.setLinkConfiguration(linkConfiguration);

        initUI();
    }

    private void initUI() {
        // TODO: use binder...
        var chooseImplStep = new ChooseImplStep(dcLinkFacade, linkConfigItemBinder);
        var createLinkConfigurationStep = createLinkConfigurationStepProvider.getObject();
        linkConfigItemBinder.bind(
            createLinkConfigurationStep,
            (ValueProvider<LnkConfigItem, DomibusConnectorLinkConfiguration>)
                LnkConfigItem::getLinkConfiguration,
            (Setter<LnkConfigItem, DomibusConnectorLinkConfiguration>)
                LnkConfigItem::setLinkConfiguration
        );

        var createLinkPartnerStep = createLinkPartnerStepProvider.getObject();
        linkConfigItemBinder.bind(
            createLinkPartnerStep,
            (ValueProvider<LnkConfigItem, DomibusConnectorLinkPartner>)
                LnkConfigItem::getLinkPartner,
            (Setter<LnkConfigItem, DomibusConnectorLinkPartner>) LnkConfigItem::setLinkPartner
        );

        wizard = WizardComponent.getBuilder()
                                .addStep(chooseImplStep)
                                .addStep(createLinkConfigurationStep)
                                .addStep(createLinkPartnerStep)
                                .addFinishedListener(this::wizardFinished)
                                .build();
        add(wizard);
    }

    private void wizardFinished(WizardComponent wizardComponent, WizardStep wizardStep) {
        linkPartner.setLinkConfiguration(linkConfiguration);
        linkPartner.setLinkType(getLinkType());
        try {
            dcLinkPersistenceService.addLinkPartner(linkPartner);
        } catch (RuntimeException e) {
            LOGGER.error("Exception occured while creating a new Link Configuration", e);
            Notification.show(
                "No Link Configuration was created due: " + e.getMessage(),
                (int) Duration.ofSeconds(5).toMillis(), Notification.Position.TOP_CENTER
            );
        }
        Notification.show(
            "New LinkPartner configuration successfully created",
            (int) Duration.ofSeconds(5).toMillis(), Notification.Position.TOP_CENTER
        );
        if (parentDialog != null) {
            parentDialog.close();
        }
    }

    /**
     * The ChooseOrCreateLinkConfigurationStep class is a static inner class that represents a step
     * in the CreateLinkPanel wizard.
     */
    public static class ChooseOrCreateLinkConfigurationStep extends VerticalLayout
        implements WizardStep {
        private static final String EXISTING_LINK_CONFIG = "Use existing Link Configuration";
        private static final String NEW_LINK_CONFIG = "Create new Link Configuration";
        private final RadioButtonGroup<String> newLinkConfiguration = new RadioButtonGroup<>();
        private final ComboBox<DomibusConnectorLinkConfiguration> linkConfigurationChooser =
            new ComboBox<>();
        private DCLinkConfigurationField linkConfigPanel;

        public ChooseOrCreateLinkConfigurationStep() {
            initUI();
        }

        private void initUI() {
            // the radio button group to switch mode between new or existing link config
            newLinkConfiguration.setItems(EXISTING_LINK_CONFIG, NEW_LINK_CONFIG);
            newLinkConfiguration.setValue(NEW_LINK_CONFIG);
            newLinkConfiguration.addValueChangeListener(this::newExistingLinkConfigChanged);

            // choose an existing link configuration
            linkConfigurationChooser.addValueChangeListener(this::linkConfigChanged);
            linkConfigurationChooser.setAllowCustomValue(false);
            linkConfigurationChooser.setRequired(true);
            linkConfigurationChooser.setItemLabelGenerator(item -> {
                if (item != null) {
                    return item.getConfigName() + " with impl " + item.getLinkImpl();
                }
                return "No Configuration set";
            });

            add(newLinkConfiguration);
            add(linkConfigurationChooser);
            add(linkConfigPanel);

            newLinkConfiguration.setValue(EXISTING_LINK_CONFIG);
        }

        private void newExistingLinkConfigChanged(
            HasValue.ValueChangeEvent<String> valueChangeEvent) {
            if (NEW_LINK_CONFIG.equals(valueChangeEvent.getValue())) {
                linkConfigPanel.setReadOnly(false);
                linkConfigurationChooser.setReadOnly(true);
                var newLinkConfig = new DomibusConnectorLinkConfiguration();
                newLinkConfig.setConfigName(
                    new DomibusConnectorLinkConfiguration.LinkConfigName("changeme")
                );
            } else if (EXISTING_LINK_CONFIG.equals(valueChangeEvent.getValue())) {
                linkConfigPanel.setReadOnly(true);
                linkConfigurationChooser.setReadOnly(false);
            }
        }

        private void linkConfigChanged(
            AbstractField.ComponentValueChangeEvent<ComboBox<DomibusConnectorLinkConfiguration>,
                DomibusConnectorLinkConfiguration> changeEvent) {
            DomibusConnectorLinkConfiguration value = changeEvent.getValue();
            if (value != null) {
                linkConfigPanel.setVisible(true);
            } else {
                linkConfigPanel.setVisible(false);
            }
        }

        @Override
        public Component getComponent() {
            return this;
        }

        @Override
        public void onForward(Command success) {
            // BinderValidationStatus<DomibusConnectorLinkConfiguration> validate;
            // validate = linkConfigPanel.validate();
            //
            // if (validate.hasErrors()) {
            //     String errorMessages = validate.getValidationErrors()
            //                                    .stream()
            //                                    .map(ValidationResult::getErrorMessage)
            //                                    .collect(Collectors.joining(","));
            //     ConfirmDialogBuilder.getBuilder()
            //                         .setMessage(errorMessages)
            //                         .setOnConfirmCallback(() -> {
            //                             linkConfigPanel.writeBeanAsDraft(linkConfiguration);
            //                             success.execute();
            //                         })
            //                         .setOnCancelCallback(() -> {
            //                             LOGGER.debug("Saving with errors has been canceled!");
            //                         })
            //                         .show();
            // } else {
            //     try {
            //         linkConfigPanel.writeBean(linkConfiguration);
            //         success.execute();
            //     } catch (ValidationException e) {
            //         LOGGER.error("Validation Exception", e);
            //     }
            // }
        }

        @Override
        public String getStepTitle() {
            return "Configure Link Implementation";
        }
    }
}


