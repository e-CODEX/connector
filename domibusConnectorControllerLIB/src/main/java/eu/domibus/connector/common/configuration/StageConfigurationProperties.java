/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.configuration;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Represents the configuration for a stage in the connector.
 */
@Data
public class StageConfigurationProperties {
    public static final String PREFIX = "connector.stage";
    @NotBlank
    private String name = "DEVELOPMENT";
    private String longName = "";
}
