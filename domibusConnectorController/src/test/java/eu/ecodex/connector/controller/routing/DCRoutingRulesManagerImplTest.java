/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.routing;

import static eu.ecodex.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;

import eu.ecodex.connector.controller.test.util.ITCaseTestContext;
import eu.ecodex.connector.domain.enums.ConfigurationSource;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {ITCaseTestContext.class},
    properties = {"spring.jta.enabled=false" // "logging.level.eu.domibus=TRACE"
    }
)
@Commit
@ActiveProfiles({"ITCaseTestContext", STORAGE_DB_PROFILE_NAME, "test"})
class DCRoutingRulesManagerImplTest {
    @Autowired
    private DCRoutingRulesManager routingRulesManager;

    @Test
    void addBackendRoutingRule() {
        RoutingRule rr = new RoutingRule();
        rr.setRoutingRuleId("abc1");
        rr.setConfigurationSource(ConfigurationSource.ENV);
        rr.setLinkName("backend_bob");
        rr.setMatchClause(new RoutingRulePattern("equals(ServiceName, 'test')"));
        routingRulesManager.addBackendRoutingRule(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), rr);
    }

    @Test
    @Disabled("not implemented yet")
    void getBackendRoutingRules() {
    }

    @Test
    @Disabled("not implemented yet")
    void getDefaultBackendName() {
    }

    @Test
    @Disabled("not implemented yet")
    void isBackendRoutingEnabled() {
    }
}
