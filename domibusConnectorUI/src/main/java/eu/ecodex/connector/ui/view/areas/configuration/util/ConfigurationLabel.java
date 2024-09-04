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
