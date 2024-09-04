/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.util;

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
