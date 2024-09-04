/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.routing;

import eu.ecodex.connector.common.annotations.MapNested;
import eu.ecodex.connector.domain.enums.ConfigurationSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;

/**
 * Represents a routing rule that determines how messages should be routed.
 */
@Data
@NoArgsConstructor
@MapNested
public class RoutingRule {
    public static final int HIGH_PRIORITY = -2000;
    public static final int LOW_PRIORITY = 2000;
    private ConfigurationSource configurationSource = ConfigurationSource.ENV;
    @NotBlank
    private String linkName;
    @NotNull
    private RoutingRulePattern matchClause;
    private String description = "";
    /**
     * higher numbers mean higher priority.
     */
    private int priority = 0;
    private boolean deleted = false;
    private String routingRuleId = generateID();

    public static String generateID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoutingRule that)) {
            return false;
        }

        return routingRuleId != null ? routingRuleId.equals(that.routingRuleId) :
            that.routingRuleId == null;
    }

    @Override
    public int hashCode() {
        return routingRuleId != null ? routingRuleId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("ruleId", routingRuleId)
            .append("linkPartnerName", linkName)
            .append("priority", priority)
            .append("rule", matchClause)
            .toString();
    }

    // sorts by ascending priority, means 0 comes before -2000.
    public static Comparator<RoutingRule> getComparator() {
        return (r1, r2) -> Integer.compare(r2.priority, r1.priority);
    }
}
