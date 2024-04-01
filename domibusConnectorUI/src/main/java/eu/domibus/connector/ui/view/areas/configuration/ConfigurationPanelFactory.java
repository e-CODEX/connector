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
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;


@Component
public class ConfigurationPanelFactory {
    private final ConfigurationPropertyManagerService configurationPropertyManagerService;
    private final SpringBeanValidationBinderFactory springBeanValidationBinderFactory;

    public ConfigurationPanelFactory(
            ConfigurationPropertyManagerService configurationPropertyManagerService,
            SpringBeanValidationBinderFactory springBeanValidationBinderFactory) {
        this.configurationPropertyManagerService = configurationPropertyManagerService;
        this.springBeanValidationBinderFactory = springBeanValidationBinderFactory;
    }

    public <T> ConfigurationPanel<T> createConfigurationPanel(FormLayout form, Class<T> configurationClazz) {
        return new ConfigurationPanel<>(form, configurationClazz);
    }

    public Dialog showChangedPropertiesDialog(Object boundConfigValue) {
        return showChangedPropertiesDialog(boundConfigValue, null);
    }

    public Dialog showChangedPropertiesDialog(Object boundConfigValue, DialogCloseCallback dialogCloseCallback) {
        Map<String, String> updatedConfiguration = configurationPropertyManagerService.getUpdatedConfiguration(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                boundConfigValue
        );

        // use custom callback with overwriting setOpened because, addDialogCloseActionListener does not work
        final Dialog d = new Dialog() {
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

        Button saveButton = new Button(VaadinIcon.CHECK.create());
        saveButton.addClickListener(clickEvent -> saveUpdatedProperties(
                d,
                boundConfigValue.getClass(),
                updatedConfiguration
        ));

        Button discardButton = new Button(VaadinIcon.CLOSE.create());
        discardButton.addClickListener(ev -> d.close());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(saveButton, discardButton);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(horizontalLayout);
        verticalLayout.add(g);

        d.add(verticalLayout);
        d.setCloseOnEsc(true);
        d.setSizeFull();
        d.open();
        return d;
    }

    // save changed properties and close dialog
    private void saveUpdatedProperties(
            Dialog d,
            Class<?> configurationClazz,
            Map<String, String> updatedConfiguration) {
        configurationPropertyManagerService.updateConfiguration(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                                                                configurationClazz, updatedConfiguration
        );
        d.close();
    }

    public interface DialogCloseCallback {
        void dialogHasBeenClosed();
    }

    public class ConfigurationPanel<T> extends VerticalLayout implements AfterNavigationObserver {
        private final Label errorField;
        private final FormLayout form;
        private final Class<T> configurationClazz;
        private Binder<T> binder;
        private T boundConfigValue;


        private ConfigurationPanel(FormLayout ecxContainerConfigForm, Class<T> configurationClazz) {
            this.form = ecxContainerConfigForm;
            this.configurationClazz = configurationClazz;

            this.errorField = new Label("");

            initUI();
        }

        private void initUI() {

            VerticalLayout configDiv = new VerticalLayout();

            HorizontalLayout saveResetButtonLayout = new HorizontalLayout();
            Button saveChangesButton = new Button("Save Changes");
            saveChangesButton.addClickListener(this::saveChangesButtonClicked);
            saveResetButtonLayout.add(saveChangesButton);

            Button resetChangesButton = new Button("Reset Changes");
            resetChangesButton.addClickListener(this::resetButtonClicked);
            saveResetButtonLayout.add(resetChangesButton);

            configDiv.add(saveResetButtonLayout);
            configDiv.add(errorField);

            binder = springBeanValidationBinderFactory.create(configurationClazz);
            binder.setStatusLabel(errorField);

            binder.bindInstanceFields(form);
            configDiv.add(form);

            add(configDiv);
        }

        private void resetButtonClicked(ClickEvent<Button> buttonClickEvent) {
            T currentConfig = readConfigFromPropertyService();
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
                Notification.show("Error, cannot save due:\n" + validate.getBeanValidationErrors()
                                                                        .stream()
                                                                        .map(vr -> vr.getErrorMessage())
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
