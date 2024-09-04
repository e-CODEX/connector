/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.dbtables;

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
