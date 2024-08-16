/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.util;

/**
 * The ConfigurationLabel class represents labels used for configuration elements.
 */
@SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:MemberName"})
public class ConfigurationLabel {
    public final String CONFIGURATION_ELEMENT_LABEL;
    public final String PROPERTY_NAME_LABEL;
    public final String[] INFO_LABEL;

    /**
     * Constructor.
     *
     * @param tfLabel      The label for the configuration element.
     * @param prpNameLabel The label for the property name.
     * @param infoLabel    An array of labels for additional information.
     */
    public ConfigurationLabel(String tfLabel, String prpNameLabel, String... infoLabel) {
        CONFIGURATION_ELEMENT_LABEL = tfLabel;
        PROPERTY_NAME_LABEL = prpNameLabel;
        INFO_LABEL = infoLabel;
    }
}
