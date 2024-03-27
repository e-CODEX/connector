package eu.domibus.connector.common.configuration;

import javax.validation.constraints.NotBlank;


public class StageConfigurationProperties {
    public static final String PREFIX = "connector.stage";

    @NotBlank
    private String name = "DEVELOPMENT";
    private String longName = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}
