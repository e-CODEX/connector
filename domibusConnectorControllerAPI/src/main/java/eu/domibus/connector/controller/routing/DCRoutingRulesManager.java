package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;

import java.util.Map;


public interface DCRoutingRulesManager {
    void addBackendRoutingRule(
            DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
            RoutingRule routingRule);

    RoutingRule persistBackendRoutingRule(
            DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
            RoutingRule routingRule);

    void deleteBackendRoutingRuleFromPersistence(
            DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
            String routingRuleId);

    void deleteBackendRoutingRule(
            DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
            String routingRuleId);

    Map<String, RoutingRule> getBackendRoutingRules(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);

    String getDefaultBackendName(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);

    void setDefaultBackendName(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String backendName);

    boolean isBackendRoutingEnabled(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);
}
