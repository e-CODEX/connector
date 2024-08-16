/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dbtables;

import java.util.List;
import java.util.stream.Stream;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The DbTableServiceConfigurationProperties class represents the configuration properties for
 * enabling the DbTableService for a specified set of database tables.
 */
@Data
@ConfigurationProperties(prefix = DbTableServiceConfigurationProperties.PREFIX)
public class DbTableServiceConfigurationProperties {
    public static final String PREFIX = "connector.ui.dbtable";
    private boolean enabled;
    private List<String> tables = Stream.of(
        "DC_LINK_CONFIGURATION",
        "DC_LINK_CONFIG_PROPERTY",
        "DC_LINK_PARTNER",
        "DC_LINK_PARTNER_PROPERTY",
        "DC_MESSAGE_LANE",
        "DC_MESSAGE_LANE_PROPERTY"
    ).toList();
}
