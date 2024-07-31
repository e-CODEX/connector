/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import java.util.Map;

/**
 * DCRoutingRulesManager interface provides methods to manage routing rules for
 * backend connectors in Domibus.
 */
public interface DCRoutingRulesManager {
    void addBackendRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
                               RoutingRule routingRule);

    RoutingRule persistBackendRoutingRule(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, RoutingRule routingRule);

    void deleteBackendRoutingRuleFromPersistence(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String routingRuleId);

    void deleteBackendRoutingRule(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
                                  String routingRuleId);

    Map<String, RoutingRule> getBackendRoutingRules(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);

    String getDefaultBackendName(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);

    void setDefaultBackendName(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
                               String backendName);

    boolean isBackendRoutingEnabled(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);
}
