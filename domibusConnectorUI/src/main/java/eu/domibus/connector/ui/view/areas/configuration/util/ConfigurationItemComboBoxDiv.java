package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.combobox.ComboBox;

import java.util.Collection;


public class ConfigurationItemComboBoxDiv extends ConfigurationItemDiv {
    private static final long serialVersionUID = 1L;

    public ConfigurationItemComboBoxDiv(
            ComboBox<String> comboBox,
            ConfigurationLabel labels,
            Collection<String> items,
            String initialValue,
            ConfigurationProperties configurationProperties) {
        super(comboBox, labels, initialValue, configurationProperties);
        comboBox.setItems(items);
        comboBox.setWidth("600px");
        comboBox.setLabel(labels.CONFIGURATION_ELEMENT_LABEL);
        comboBox.setValue(initialValue);
        comboBox.addValueChangeListener(e -> {
            configurationProperties.changeComponentValue(comboBox, e.getValue());
        });
    }
}
