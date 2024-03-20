package eu.domibus.connector.controller.routing;

import eu.domibus.connector.common.annotations.MapNested;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import org.springframework.core.style.ToStringCreator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.UUID;

@MapNested
public class RoutingRule {

    public static int HIGH_PRIORITY = -2000;
    public static int LOW_PRIORITY = 2000;

    private ConfigurationSource configurationSource = ConfigurationSource.ENV;

    @NotBlank
    private String linkName;

    @NotNull
    private RoutingRulePattern matchClause;

    private String description = "";

    /**
     * higher numbers mean higher priority
     */
    private int priority = 0;

    private boolean deleted = false;

    private String routingRuleId = generateID();

    public static String generateID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public RoutingRulePattern getMatchClause() {
        return matchClause;
    }

    public void setMatchClause(RoutingRulePattern matchClause) {
        this.matchClause = matchClause;
    }

    public ConfigurationSource getConfigurationSource() {
        return configurationSource;
    }

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoutingRuleId() {
        return routingRuleId;
    }

    public void setRoutingRuleId(String routingRuleId) {
        this.routingRuleId = routingRuleId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoutingRule)) return false;

        RoutingRule that = (RoutingRule) o;

        return routingRuleId != null ? routingRuleId.equals(that.routingRuleId) : that.routingRuleId == null;
    }

    @Override
    public int hashCode() {
        return routingRuleId != null ? routingRuleId.hashCode() : 0;
    }

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
