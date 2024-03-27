package eu.domibus.connector.persistence.service;

import eu.domibus.connector.controller.routing.RoutingRule;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;

import java.util.List;


public interface DCRoutingRulePersistenceService {
    void createRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, RoutingRule rr);

    void deleteRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String ruleId);

    void updateRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, RoutingRule ruleId);

    List<RoutingRule> getAllRoutingRules(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);

    void setDefaultBackendName(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String backendName);
}
