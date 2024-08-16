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

import com.vaadin.flow.component.combobox.ComboBox;
import java.util.Collection;

/**
 * Represents a configuration item that consists of a ComboBox.
 */
public class ConfigurationItemComboBoxDiv extends ConfigurationItemDiv {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param comboBox                the ComboBox to be displayed as the configuration item
     * @param labels                  the configuration label and other related labels
     * @param items                   the collection of items to be displayed in the ComboBox
     * @param initialValue            the initial value to be set for the ComboBox
     * @param configurationProperties the configuration properties object to track changes and
     *                                update values
     */
    public ConfigurationItemComboBoxDiv(
        ComboBox<String> comboBox, ConfigurationLabel labels, Collection<String> items,
        String initialValue, ConfigurationProperties configurationProperties) {
        super(comboBox, labels, initialValue, configurationProperties);
        comboBox.setItems(items);
        comboBox.setWidth("600px");
        comboBox.setLabel(labels.CONFIGURATION_ELEMENT_LABEL);
        comboBox.setValue(initialValue);
        comboBox.addValueChangeListener(
            e -> configurationProperties.changeComponentValue(comboBox, e.getValue())
        );
    }
}
