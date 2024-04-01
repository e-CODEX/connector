package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.textfield.TextField;


public class ConfigurationItemTextFieldDiv extends ConfigurationItemDiv {
    private static final long serialVersionUID = 1L;

    public ConfigurationItemTextFieldDiv(
            TextField configurationItem,
            ConfigurationLabel labels,
            String initialValue,
            ConfigurationProperties configurationProperties) {
        super(configurationItem, labels, initialValue, configurationProperties);
        configurationItem.setValue(initialValue);
        configurationItem.setLabel(labels.CONFIGURATION_ELEMENT_LABEL);
        configurationItem.addValueChangeListener(e -> {
            configurationProperties.changeComponentValue(configurationItem, e.getValue());
        });
    }
}
