package eu.domibus.connector.ui.view.areas.configuration.util;

import eu.domibus.connector.ui.component.LumoCheckbox;


public class ConfigurationItemCheckboxDiv extends ConfigurationItemDiv {
    private static final long serialVersionUID = 1L;

    public ConfigurationItemCheckboxDiv(
            LumoCheckbox configurationItem,
            ConfigurationLabel labels,
            Boolean initialValue,
            ConfigurationProperties configurationProperties) {
        super(configurationItem, labels, initialValue, configurationProperties);
        configurationItem.setValue(initialValue);
        configurationItem.setLabel(labels.CONFIGURATION_ELEMENT_LABEL);
        configurationItem.addValueChangeListener(e -> {
            configurationProperties.changeComponentValue(configurationItem, e.getValue().toString().toLowerCase());
        });
    }
}
