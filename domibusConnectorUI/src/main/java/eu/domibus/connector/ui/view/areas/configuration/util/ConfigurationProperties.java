/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.ui.enums.UserRole;
import java.util.HashMap;
import java.util.Properties;
import lombok.Data;

/**
 * The {@code ConfigurationProperties} class represents a configuration properties object that
 * stores and manages the properties of a system configuration.
 *
 * @see org.springframework.stereotype.Component
 */
@Data
@org.springframework.stereotype.Component
public class ConfigurationProperties {
    private final Properties properties = new Properties();
    private final HashMap<String, Object> initialProperties = new HashMap<String, Object>();
    private final HashMap<String, Component> configurationComponents = new HashMap<>();
    private final HashMap<String, Component> changedComponents = new HashMap<>();

    public void registerComponent(Component component, Object initialValue) {
        configurationComponents.put(component.getId().get(), component);
        initialProperties.put(component.getId().get(), initialValue);
    }

    public void unregisterComponent(Component component) {
        configurationComponents.remove(component.getId().get());
        initialProperties.remove(component.getId().get());
    }

    public void changeComponentValue(Component component, String newStringValue) {
        changedComponents.put(component.getId().get(), component);
        properties.setProperty(component.getId().get(), newStringValue);
    }

    public void clearChanges() {
        changedComponents.clear();
        properties.clear();
    }

    public void clearProperties() {
        properties.clear();
    }

    public HashMap<String, Object> getInitialproperties() {
        return initialProperties;
    }

    public HashMap<String, Component> getConfigurationcomponents() {
        return configurationComponents;
    }

    public HashMap<String, Component> getChangedcomponents() {
        return changedComponents;
    }

    /**
     * Updates the configuration components based on the user's role. The method iterates over the
     * configuration components and sets their read-only status based on the user's role. If the
     * user's role is ADMIN, all components will be editable. If the user's role is not ADMIN, all
     * components will be read-only.
     *
     * @param userRole the role of the user
     */
    public void updateOnRole(UserRole userRole) {
        for (String componentId : getConfigurationcomponents().keySet()) {
            com.vaadin.flow.component.Component c = getConfigurationcomponents().get(componentId);
            boolean readonly = !userRole.equals(UserRole.ADMIN);
            if (c instanceof ComboBox<?>) {
                ((ComboBox<String>) c).setReadOnly(readonly);
            } else if (c instanceof Checkbox checkbox) {
                checkbox.setReadOnly(readonly);
            } else if (c instanceof TextField textField) {
                textField.setReadOnly(readonly);
            }
        }
    }

    /**
     * Updates the configuration components based on the given properties.
     *
     * @param properties the properties containing the updated values
     */
    public void updateConfigurationComponentsOnProperties(Properties properties) {
        for (String componentId : getConfigurationcomponents().keySet()) {
            if (properties.containsKey(componentId)) {
                com.vaadin.flow.component.Component c =
                    getConfigurationcomponents().get(componentId);
                if (c instanceof ComboBox<?>) {
                    ((ComboBox<String>) c).setValue(properties.getProperty(componentId));
                } else if (c instanceof Checkbox checkbox) {
                    checkbox.setValue(Boolean.valueOf(properties.getProperty(componentId)));
                } else if (c instanceof TextField textField) {
                    textField.setValue(properties.getProperty(componentId));
                }
            }
        }
    }
}
