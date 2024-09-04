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
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDetails;
import eu.ecodex.connector.domain.model.DomibusConnectorParty;
import eu.ecodex.connector.domain.model.DomibusConnectorService;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RoutingRuleEvaluationTest {
    @ParameterizedTest
    @MethodSource("provideParameters")
    void testMatch_shouldEvaluateToTrue(String expression, DomibusConnectorMessage message) {
        RoutingRulePattern rulePattern = new RoutingRulePattern(expression);
        boolean result = rulePattern.matches(message);
        assertThat(result).isTrue();
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
            Arguments.of(
                "&(equals(ServiceName, 'EPO_SERVICE'), |(equals(FromPartyId, 'gw01'), "
                    + "equals(FromPartyId, 'gw02')))",
                getMessage1()
            ),
            Arguments.of(
                "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, "
                    + "'Connector-TEST')), equals(ServiceType, 'urn:e-codex:services:'))",
                getMessage2()
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideNotMatchingParameters")
    void testMatch_shouldNotMatch(String expression, DomibusConnectorMessage message) {
        RoutingRulePattern rulePattern = new RoutingRulePattern(expression);
        boolean result = rulePattern.matches(message);
        assertThat(result).isFalse();
    }

    private static Stream<Arguments> provideNotMatchingParameters() {
        return Stream.of(
            Arguments.of(
                "not(&(equals(ServiceName, 'EPO_SERVICE'), |(equals(FromPartyId, "
                    + "'gw01'), equals(FromPartyId, 'gw02'))))",
                getMessage1()
            ),
            Arguments.of(
                "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, "
                    + "'Connector-TEST')), equals(ServiceType, 'urn:e-codex:services:'))",
                getMessage1()
            )

        );
    }

    private static DomibusConnectorMessage getMessage1() {
        var message = new DomibusConnectorMessage();
        message.setMessageDetails(new DomibusConnectorMessageDetails());
        message.getMessageDetails().setAction(new DomibusConnectorAction());
        message.getMessageDetails().getAction().setAction("OtherAction");
        message.getMessageDetails().setService(new DomibusConnectorService());
        message.getMessageDetails().getService().setService("EPO_SERVICE");
        message.getMessageDetails().getService().setServiceType("urn:e-codex:services:");
        message.getMessageDetails().setFromParty(new DomibusConnectorParty());
        message.getMessageDetails().getFromParty().setPartyId("gw01");
        return message;
    }

    private static DomibusConnectorMessage getMessage2() {
        var message = new DomibusConnectorMessage();
        message.setMessageDetails(new DomibusConnectorMessageDetails());
        message.getMessageDetails().setAction(new DomibusConnectorAction());
        message.getMessageDetails().getAction().setAction("ConTest_Form");
        message.getMessageDetails().setService(new DomibusConnectorService());
        message.getMessageDetails().getService().setService("Connector-TEST");
        message.getMessageDetails().getService().setServiceType("urn:e-codex:services:");
        message.getMessageDetails().setFromParty(new DomibusConnectorParty());
        message.getMessageDetails().getFromParty().setPartyId("gw01");
        return message;
    }
}
