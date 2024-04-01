package eu.domibus.connector.ui.view.areas.configuration.util;

public class ConfigurationLabel {
    public final String CONFIGURATION_ELEMENT_LABEL;
    public final String PROPERTY_NAME_LABEL;
    public final String[] INFO_LABEL;

    public ConfigurationLabel(String tfLabel, String prpNameLabel, String... infoLabel) {
        CONFIGURATION_ELEMENT_LABEL = tfLabel;
        PROPERTY_NAME_LABEL = prpNameLabel;
        INFO_LABEL = infoLabel;
    }
}
