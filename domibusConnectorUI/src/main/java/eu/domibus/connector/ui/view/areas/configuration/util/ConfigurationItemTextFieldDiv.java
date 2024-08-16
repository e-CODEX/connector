/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.textfield.TextField;

/**
 * The ConfigurationItemTextFieldDiv class is a subclass of ConfigurationItemDiv that represents a
 * configuration item with a text field component.
 */
public class ConfigurationItemTextFieldDiv extends ConfigurationItemDiv {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param configurationItem       the text field component representing the configuration item
     * @param labels                  the configuration label and other related labels
     * @param initialValue            the initial value of the configuration item
     * @param configurationProperties the configuration properties object to track changes and
     *                                update values
     */
    public ConfigurationItemTextFieldDiv(
        TextField configurationItem, ConfigurationLabel labels, String initialValue,
        ConfigurationProperties configurationProperties) {
        super(configurationItem, labels, initialValue, configurationProperties);
        configurationItem.setValue(initialValue);
        configurationItem.setLabel(labels.CONFIGURATION_ELEMENT_LABEL);
        configurationItem.addValueChangeListener(
            e -> configurationProperties.changeComponentValue(configurationItem, e.getValue())
        );
    }
}
