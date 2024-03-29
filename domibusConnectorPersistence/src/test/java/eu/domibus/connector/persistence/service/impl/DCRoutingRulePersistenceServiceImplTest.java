package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.controller.routing.RoutingRule;
import eu.domibus.connector.controller.routing.RoutingRulePattern;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.dao.CommonPersistenceTest;
import eu.domibus.connector.persistence.service.DCRoutingRulePersistenceService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


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
        routingRulePersistenceService.createRoutingRule(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), rr);
    }

    @Test
    void updateRoutingRule() {
        RoutingRule rr = new RoutingRule();
        rr.setRoutingRuleId("abcd");
        rr.setLinkName("backend_bob");
        rr.setMatchClause(new RoutingRulePattern("equals(ServiceName, 'test')"));
        routingRulePersistenceService.createRoutingRule(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), rr);

        List<RoutingRule> allRoutingRules =
                routingRulePersistenceService.getAllRoutingRules(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        RoutingRule abcd = allRoutingRules.stream().filter(r -> r.getRoutingRuleId().equals("abcd"))
                                          .findFirst()
                                          .get();
        assertThat(abcd.getLinkName()).isEqualTo("backend_bob");

        RoutingRule rrU = new RoutingRule();
        rrU.setRoutingRuleId("abcd");
        rrU.setLinkName("backend_alice");
        rrU.setMatchClause(new RoutingRulePattern("equals(ServiceName, 'test')"));

        routingRulePersistenceService.updateRoutingRule(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), rrU);

        allRoutingRules =
                routingRulePersistenceService.getAllRoutingRules(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        abcd = allRoutingRules.stream().filter(r -> r.getRoutingRuleId().equals("abcd"))
                              .findFirst()
                              .get();
        assertThat(abcd.getLinkName()).isEqualTo("backend_alice");
    }

    @Test
    @Order(200)
    void getAllRoutingRules() {
        List<RoutingRule> allRoutingRules =
                routingRulePersistenceService.getAllRoutingRules(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        assertThat(allRoutingRules).hasSize(1);
    }

    @Test
    @Order(300)
    void deleteRoutingRule() {
        RoutingRule rr = new RoutingRule();
        rr.setRoutingRuleId("xyz");
        rr.setLinkName("backend_bob");
        rr.setMatchClause(new RoutingRulePattern("equals(ServiceName, 'test')"));
        routingRulePersistenceService.createRoutingRule(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), rr);

        List<RoutingRule> allRoutingRules =
                routingRulePersistenceService.getAllRoutingRules(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        assertThat(allRoutingRules).hasSize(2);

        routingRulePersistenceService.deleteRoutingRule(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                "xyz"
        );

        allRoutingRules =
                routingRulePersistenceService.getAllRoutingRules(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        assertThat(allRoutingRules).hasSize(1);
    }
}
