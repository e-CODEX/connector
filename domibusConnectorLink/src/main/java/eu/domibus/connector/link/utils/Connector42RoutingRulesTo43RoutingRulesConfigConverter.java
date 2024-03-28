package eu.domibus.connector.link.utils;

import eu.domibus.connector.controller.routing.RoutingRule;
import eu.domibus.connector.controller.routing.RoutingRulePattern;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Lazy
@Component
public class Connector42RoutingRulesTo43RoutingRulesConfigConverter {
    private static final Logger LOGGER =
            LogManager.getLogger(Connector42RoutingRulesTo43RoutingRulesConfigConverter.class);

    private final JdbcTemplate jdbcTemplate;

    public Connector42RoutingRulesTo43RoutingRulesConfigConverter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RoutingRule> getRoutingRules() {
        return jdbcTemplate.query(
                "SELECT s.DOMIBUS_CONNECTOR_SERVICE_ID, b.BACKEND_NAME FROM " + "DOMIBUS_CONNECTOR_BACK_2_S s LEFT " +
                        "JOIN DOMIBUS_CONNECTOR_BACKEND_INFO b ON s.DOMIBUS_CONNECTOR_BACKEND_ID = b.ID",
                (rs, rowNum) -> {
                    try {
                        RoutingRule rr = new RoutingRule();
                        String backendName = rs.getString(2);
                        String serviceName = rs.getString(1);
                        rr.setConfigurationSource(ConfigurationSource.DB);
                        rr.setDescription("From old config imported routing rule");
                        rr.setLinkName(backendName);
                        rr.setPriority(20);
                        String matchPattern = String.format("equals(ServiceName, '%s')", serviceName);
                        rr.setMatchClause(new RoutingRulePattern(matchPattern));
                        LOGGER.info("Created imported routing rule [{}]", rr);
                        return rr;
                    } catch (Exception e) {
                        LOGGER.warn("Failed to import a routing rule from old config", e);
                        // Ignore
                    }
                    return null;
                }
        ).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
