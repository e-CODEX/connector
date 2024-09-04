/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.utils;

import eu.ecodex.connector.controller.routing.RoutingRule;
import eu.ecodex.connector.controller.routing.RoutingRulePattern;
import eu.ecodex.connector.domain.enums.ConfigurationSource;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Converts Connector42 routing rules to Connector43 routing rules configuration.
 */
@Lazy
@Component
public class Connector42RoutingRulesTo43RoutingRulesConfigConverter {
    private static final Logger LOGGER =
        LogManager.getLogger(Connector42RoutingRulesTo43RoutingRulesConfigConverter.class);
    private final JdbcTemplate jdbcTemplate;

    public Connector42RoutingRulesTo43RoutingRulesConfigConverter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retrieves the list of routing rules from the database.
     *
     * @return A list of RoutingRule objects representing the routing rules in the database.
     */
    public List<RoutingRule> getRoutingRules() {
        return jdbcTemplate
            .query(
                "SELECT s.DOMIBUS_CONNECTOR_SERVICE_ID, b.BACKEND_NAME FROM "
                    + "DOMIBUS_CONNECTOR_BACK_2_S s LEFT JOIN DOMIBUS_CONNECTOR_BACKEND_INFO b "
                    + "ON s.DOMIBUS_CONNECTOR_BACKEND_ID = b.ID",
                (rs, rowNum) -> {
                    try {
                        var routingRule = new RoutingRule();
                        var backendName = rs.getString(2);
                        var serviceName = rs.getString(1);
                        routingRule.setConfigurationSource(ConfigurationSource.DB);
                        routingRule.setDescription("From old config imported routing rule");
                        routingRule.setLinkName(backendName);
                        routingRule.setPriority(20);
                        var matchPattern = String.format("equals(ServiceName, '%s')", serviceName);
                        routingRule.setMatchClause(new RoutingRulePattern(matchPattern));
                        LOGGER.info("Created imported routing rule [{}]", routingRule);
                        return routingRule;
                    } catch (Exception e) {
                        LOGGER.warn("Failed to import a routing rule from old config", e);
                        // Ignore
                    }
                    return null;
                }
            ).stream()
            .filter(Objects::nonNull)
            .toList();
    }
}
