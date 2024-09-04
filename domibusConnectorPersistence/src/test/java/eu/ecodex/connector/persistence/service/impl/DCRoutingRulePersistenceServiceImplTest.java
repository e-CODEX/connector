/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.controller.routing.RoutingRule;
import eu.ecodex.connector.controller.routing.RoutingRulePattern;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.persistence.dao.CommonPersistenceTest;
import eu.ecodex.connector.persistence.service.DCRoutingRulePersistenceService;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

@CommonPersistenceTest

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class DCRoutingRulePersistenceServiceImplTest {
    @Autowired
    DCRoutingRulePersistenceService routingRulePersistenceService;

    @Test
    @Order(100)
    void createRoutingRule() {
        RoutingRule rr = new RoutingRule();
        rr.setRoutingRuleId("abc");
        rr.setLinkName("backend_bob");
        rr.setMatchClause(new RoutingRulePattern("equals(ServiceName, 'test')"));
        routingRulePersistenceService.createRoutingRule(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), rr);
    }

    @Test
    void updateRoutingRule() {
        RoutingRule rr = new RoutingRule();
        rr.setRoutingRuleId("abcd");
        rr.setLinkName("backend_bob");
        rr.setMatchClause(new RoutingRulePattern("equals(ServiceName, 'test')"));
        routingRulePersistenceService.createRoutingRule(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), rr);

        List<RoutingRule> allRoutingRules = routingRulePersistenceService.getAllRoutingRules(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        RoutingRule abcd = allRoutingRules.stream().filter(r -> r.getRoutingRuleId().equals("abcd"))
                                          .findFirst()
                                          .get();
        assertThat(abcd.getLinkName()).isEqualTo("backend_bob");

        RoutingRule rrU = new RoutingRule();
        rrU.setRoutingRuleId("abcd");
        rrU.setLinkName("backend_alice");
        rrU.setMatchClause(new RoutingRulePattern("equals(ServiceName, 'test')"));

        routingRulePersistenceService.updateRoutingRule(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), rrU);

        allRoutingRules = routingRulePersistenceService.getAllRoutingRules(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        abcd = allRoutingRules.stream().filter(r -> r.getRoutingRuleId().equals("abcd"))
                              .findFirst()
                              .get();
        assertThat(abcd.getLinkName()).isEqualTo("backend_alice");
    }

    @Test
    @Order(200)
    void getAllRoutingRules() {
        List<RoutingRule> allRoutingRules = routingRulePersistenceService.getAllRoutingRules(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        assertThat(allRoutingRules).hasSize(1);
    }

    @Test
    @Order(300)
    void deleteRoutingRule() {
        RoutingRule rr = new RoutingRule();
        rr.setRoutingRuleId("xyz");
        rr.setLinkName("backend_bob");
        rr.setMatchClause(new RoutingRulePattern("equals(ServiceName, 'test')"));
        routingRulePersistenceService.createRoutingRule(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), rr);

        List<RoutingRule> allRoutingRules = routingRulePersistenceService.getAllRoutingRules(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        assertThat(allRoutingRules).hasSize(2);

        routingRulePersistenceService.deleteRoutingRule(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), "xyz");

        allRoutingRules = routingRulePersistenceService.getAllRoutingRules(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        assertThat(allRoutingRules).hasSize(1);
    }
}
