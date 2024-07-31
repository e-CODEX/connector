/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.controller.routing.RoutingRule;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import java.util.List;

/**
 * The DCRoutingRulePersistenceService interface provides methods for managing routing rules in the
 * Domibus Connector.
 */
public interface DCRoutingRulePersistenceService {
    void createRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
                           RoutingRule rr);

    void deleteRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
                                  String ruleId);

    void updateRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
                                  RoutingRule ruleId);

    List<RoutingRule> getAllRoutingRules(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);

    void setDefaultBackendName(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String backendName);
}
