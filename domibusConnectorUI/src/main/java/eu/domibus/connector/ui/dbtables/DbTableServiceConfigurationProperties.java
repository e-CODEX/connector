package eu.domibus.connector.ui.dbtables;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    ).collect(Collectors.toList());

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }
}
