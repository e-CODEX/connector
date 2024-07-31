/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import eu.domibus.connector.common.annotations.MapNested;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import java.util.Comparator;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
