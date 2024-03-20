package eu.domibus.connector.persistence.service;

import eu.domibus.connector.controller.routing.RoutingRule;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;

import java.util.List;

public interface DCRoutingRulePersistenceService {

    public void createRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, RoutingRule rr);

    public void deleteRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String ruleId);

    public void updateRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, RoutingRule ruleId);

    public List<RoutingRule> getAllRoutingRules(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);

    public void setDefaultBackendName(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String backendName);
}
