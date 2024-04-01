package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.ui.enums.UserRole;

import java.util.HashMap;
import java.util.Properties;


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

    public Properties getProperties() {
        return properties;
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

    public void updateOnRole(UserRole userRole) {
        for (String componentId : getConfigurationcomponents().keySet()) {
            com.vaadin.flow.component.Component c = getConfigurationcomponents().get(componentId);
            boolean readonly = !userRole.equals(UserRole.ADMIN);
            if (c instanceof ComboBox<?>) {
                ((ComboBox<String>) c).setReadOnly(readonly);
            } else if (c instanceof Checkbox) {
                ((Checkbox) c).setReadOnly(readonly);
            } else if (c instanceof TextField) {
                ((TextField) c).setReadOnly(readonly);
            }
        }
    }

    public void updateConfigurationComponentsOnProperties(Properties properties) {
        for (String componentId : getConfigurationcomponents().keySet()) {
            if (properties.containsKey(componentId)) {
                com.vaadin.flow.component.Component c = getConfigurationcomponents().get(componentId);
                if (c instanceof ComboBox<?>) {
                    ((ComboBox<String>) c).setValue(properties.getProperty(componentId));
                } else if (c instanceof Checkbox) {
                    ((Checkbox) c).setValue(Boolean.valueOf(properties.getProperty(componentId)));
                } else if (c instanceof TextField) {
                    ((TextField) c).setValue(properties.getProperty(componentId));
                }
            }
        }
    }
}
