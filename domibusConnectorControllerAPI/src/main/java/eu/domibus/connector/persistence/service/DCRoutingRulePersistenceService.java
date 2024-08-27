/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
