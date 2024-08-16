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
