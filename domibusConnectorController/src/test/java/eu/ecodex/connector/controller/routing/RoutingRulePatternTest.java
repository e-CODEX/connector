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

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorService;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import org.junit.jupiter.api.Test;

class RoutingRulePatternTest {
    @Test
    void matchesServiceName() {
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        DomibusConnectorService service = new DomibusConnectorService("serviceName", "serviceType");
        epoMessage.getMessageDetails().setService(service);

        RoutingRulePattern pattern = new RoutingRulePattern("equals(ServiceName, 'serviceName')");
        assertThat(pattern.matches(epoMessage)).isTrue();
    }

    @Test
    void matchesStartWithServiceName() {
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        DomibusConnectorService service = new DomibusConnectorService("serviceName", "serviceType");
        epoMessage.getMessageDetails().setService(service);

        RoutingRulePattern pattern = new RoutingRulePattern("startswith(ServiceName, 'serv')");
        assertThat(pattern.matches(epoMessage)).isTrue();
    }

    @Test
    void matchesStartWithServiceName_noMatch() {
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        DomibusConnectorService service = new DomibusConnectorService("serviceName", "serviceType");
        epoMessage.getMessageDetails().setService(service);

        RoutingRulePattern pattern = new RoutingRulePattern("startswith(ServiceName, 'aserv')");
        assertThat(pattern.matches(epoMessage)).isFalse();
    }

    @Test
    void matchesServiceName_shouldFail() {
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        DomibusConnectorService service = new DomibusConnectorService("serviceName", "serviceType");
        epoMessage.getMessageDetails().setService(service);

        RoutingRulePattern pattern = new RoutingRulePattern("equals(ServiceName, 'service')");
        assertThat(pattern.matches(epoMessage)).isFalse();
    }

    @Test
    void matchesServiceName_withAnd() {
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        DomibusConnectorService service =
            new DomibusConnectorService("serviceName", "s:ervice-Type");
        epoMessage.getMessageDetails().setService(service);

        RoutingRulePattern pattern = new RoutingRulePattern(
            "&(equals(ServiceName, 'serviceName'),equals(ServiceType, 's:ervice-Type'))");
        assertThat(pattern.matches(epoMessage)).isTrue();
    }

    @Test
    void matchesServiceName_withOr() {
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        DomibusConnectorService service = new DomibusConnectorService("serviceName", "serviceType");
        epoMessage.getMessageDetails().setService(service);

        RoutingRulePattern pattern = new RoutingRulePattern(
            "|(equals(ServiceName, 'serviceName'),equals(ServiceName, 'serName'))");
        assertThat(pattern.matches(epoMessage)).isTrue();
    }

    @Test
    void testAction() {
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        DomibusConnectorAction action = new DomibusConnectorAction("Connector-TEST");
        epoMessage.getMessageDetails().setAction(action);

        RoutingRulePattern pattern = new RoutingRulePattern("equals(Action, 'Connector-TEST')");
        assertThat(pattern.matches(epoMessage)).isTrue();
    }
}
