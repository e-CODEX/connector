/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.util;

import eu.domibus.connector.ui.component.LumoCheckbox;

/**
 * The ConfigurationItemCheckboxDiv class is a subclass of ConfigurationItemDiv that represents a
 * checkbox configuration item.
 */
public class ConfigurationItemCheckboxDiv extends ConfigurationItemDiv {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param configurationItem       the LumoCheckbox component representing the configuration
     *                                item
     * @param labels                  the ConfigurationLabel instance containing label values
     * @param initialValue            the initial value of the checkbox
     * @param configurationProperties the ConfigurationProperties instance containing configuration
     *                                properties
     */
    public ConfigurationItemCheckboxDiv(
        LumoCheckbox configurationItem, ConfigurationLabel labels, Boolean initialValue,
        ConfigurationProperties configurationProperties) {
        super(configurationItem, labels, initialValue, configurationProperties);
        configurationItem.setValue(initialValue);
        configurationItem.setLabel(labels.CONFIGURATION_ELEMENT_LABEL);
        configurationItem.addValueChangeListener(e -> configurationProperties.changeComponentValue(
            configurationItem, e.getValue().toString().toLowerCase())
        );
    }
}
