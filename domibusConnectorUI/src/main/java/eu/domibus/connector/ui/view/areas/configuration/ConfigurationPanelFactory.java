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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for creating and managing configuration panels. It is a Spring
 * component that can be dependency-injected into other classes.
 */
@Component
public class ConfigurationPanelFactory {
    private final ConfigurationPropertyManagerService configurationPropertyManagerService;
    private final SpringBeanValidationBinderFactory springBeanValidationBinderFactory;

    /**
     * Constructor.
     *
     * @param configurationPropertyManagerService The service used to manage configuration
     *                                            properties.
     * @param springBeanValidationBinderFactory   The factory used to create instances of
     *                                            {@link SpringBeanValidationBinder}.
     */
    public ConfigurationPanelFactory(
        ConfigurationPropertyManagerService configurationPropertyManagerService,
        SpringBeanValidationBinderFactory springBeanValidationBinderFactory) {
        this.configurationPropertyManagerService = configurationPropertyManagerService;
        this.springBeanValidationBinderFactory = springBeanValidationBinderFactory;
    }

    /**
     * Creates a {@link ConfigurationPanel} with the specified form and configuration class.
     *
     * @param form               the form layout to be used in the configuration panel
     * @param configurationClazz the class of the configuration object
     * @param <T>                the type of the configuration object
     * @return a new instance of {@link ConfigurationPanel} with the specified form and
     *      configuration class
     */
    public <T> ConfigurationPanel<T> createConfigurationPanel(
        FormLayout form, Class<T> configurationClazz) {
        return new ConfigurationPanel<>(form, configurationClazz);
    }

    /**
     * Displays a dialog showing the changed properties of a bound config value.
     *
     * @param boundConfigValue The bound config value for which the changed properties should be
     *                         displayed.
     * @return The dialog that displays the changed properties.
     */
    public Dialog showChangedPropertiesDialog(Object boundConfigValue) {
        return showChangedPropertiesDialog(boundConfigValue, null);
    }

    /**
     * Displays a dialog showing the changed properties of a bound config value.
     *
     * @param boundConfigValue    The bound config value for which the changed properties should be
     *                            displayed.
     * @param dialogCloseCallback The callback to be executed when the dialog is closed.
     * @return The dialog that displays the changed properties.
     */
    public Dialog showChangedPropertiesDialog(
        Object boundConfigValue, DialogCloseCallback dialogCloseCallback) {
        Map<String, String> updatedConfiguration =
            configurationPropertyManagerService.getUpdatedConfiguration(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                boundConfigValue
            );

        // use custom callback with overwriting setOpened because, addDialogCloseActionListener
        // does not work
        final Dialog d = new Dialog() {
            @Override
            public void setOpened(boolean opened) {
                super.setOpened(opened);
                if (!opened && dialogCloseCallback != null) {
                    dialogCloseCallback.dialogHasBeenClosed();
                }
            }
        };

        Grid<Map.Entry<String, String>> g = new Grid<>();
        g.setItems(updatedConfiguration.entrySet());
        g.addColumn(Map.Entry::getKey).setHeader("key");
        g.addColumn(Map.Entry::getValue).setHeader("value");

        var saveButton = new Button(VaadinIcon.CHECK.create());
        saveButton.addClickListener(
            clickEvent -> saveUpdatedProperties(d, boundConfigValue.getClass(),
                                                updatedConfiguration
            ));

        var discardButton = new Button(VaadinIcon.CLOSE.create());
        discardButton.addClickListener(ev -> d.close());

        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(saveButton, discardButton);
        var verticalLayout = new VerticalLayout();
        verticalLayout.add(horizontalLayout);
        verticalLayout.add(g);

        d.add(verticalLayout);
        d.setCloseOnEsc(true);
        d.setSizeFull();
        d.open();
        return d;
    }


    /**
     * Defines the callback interface for when a dialog has been closed.
     */
    public interface DialogCloseCallback {
        void dialogHasBeenClosed();
    }

    // save changed properties and close dialog
    private void saveUpdatedProperties(
        Dialog d, Class<?> configurationClazz, Map<String, String> updatedConfiguration) {
        configurationPropertyManagerService.updateConfiguration(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
            configurationClazz, updatedConfiguration
        );
        d.close();
    }

    /**
     * The `ConfigurationPanel` class is a custom UI component that provides a panel for configuring
     * a specific type of object.
     */
    public class ConfigurationPanel<T> extends VerticalLayout implements AfterNavigationObserver {
        private final Label errorField;
        private final FormLayout form;
        private final Class<T> configurationClazz;
        private Binder<T> binder;
        private T boundConfigValue;

        /**
         * Constructor.
         *
         * @param ecxContainerConfigForm The form layout to be used in the configuration panel
         * @param configurationClazz     The class of the configuration object
         */
        private ConfigurationPanel(FormLayout ecxContainerConfigForm, Class<T> configurationClazz) {
            this.form = ecxContainerConfigForm;
            this.configurationClazz = configurationClazz;

            this.errorField = new Label("");

            initUI();
        }

        private void initUI() {
            var saveResetButtonLayout = new HorizontalLayout();
            var saveChangesButton = new Button("Save Changes");
            saveChangesButton.addClickListener(this::saveChangesButtonClicked);
            saveResetButtonLayout.add(saveChangesButton);

            var resetChangesButton = new Button("Reset Changes");
            resetChangesButton.addClickListener(this::resetButtonClicked);
            saveResetButtonLayout.add(resetChangesButton);

            var configDiv = new VerticalLayout();
            configDiv.add(saveResetButtonLayout);
            configDiv.add(errorField);

            binder = springBeanValidationBinderFactory.create(configurationClazz);
            binder.setStatusLabel(errorField);

            binder.bindInstanceFields(form);
            configDiv.add(form);

            add(configDiv);
        }

        private void resetButtonClicked(ClickEvent<Button> buttonClickEvent) {
            var currentConfig = readConfigFromPropertyService();
            binder.readBean(currentConfig); // reset config
        }

        private T readConfigFromPropertyService() {
            return configurationPropertyManagerService.loadConfiguration(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                configurationClazz
            );
        }

        private void saveChangesButtonClicked(ClickEvent<Button> buttonClickEvent) {
            BinderValidationStatus<T> validate = binder.validate();
            if (validate.isOk()) {
                try {
                    binder.writeBean(this.boundConfigValue);
                } catch (ValidationException e) {
                    // should not occur since validate.isOk()
                    throw new RuntimeException(e);
                }

                showChangedPropertiesDialog(boundConfigValue);
            } else {
                Notification.show(
                    "Error, cannot save due:\n" + validate.getBeanValidationErrors()
                                                          .stream()
                                                          .map(ValidationResult::getErrorMessage)
                                                          .collect(Collectors.joining("\n"))
                );
            }
        }

        @Override
        public void afterNavigation(AfterNavigationEvent event) {
            refreshUI();
        }

        public void refreshUI() {
            this.boundConfigValue = readConfigFromPropertyService();
            binder.setBean(boundConfigValue); // bind bean
        }
    }
}
