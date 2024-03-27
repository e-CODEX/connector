package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class RoutingRuleEvaluationTest {
    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(
                        "&(equals(ServiceName, 'EPO_SERVICE'), |(equals(FromPartyId, 'gw01'), equals(FromPartyId, " +
                                "'gw02')))",
                        getMessage1()
                ),
                Arguments.of(
                        "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, 'Connector-TEST')), equals" +
                                "(ServiceType, 'urn:e-codex:services:'))",
                        getMessage2()
                )
        );
    }

    private static Stream<Arguments> provideNotMatchingParameters() {
        return Stream.of(
                Arguments.of(
                        "not(&(equals(ServiceName, 'EPO_SERVICE'), |(equals(FromPartyId, 'gw01'), equals(FromPartyId," +
                                " 'gw02'))))",
                        getMessage1()
                ),
                Arguments.of(
                        "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, 'Connector-TEST')), equals" +
                                "(ServiceType, 'urn:e-codex:services:'))",
                        getMessage1()
                )

        );
    }

    private static DomibusConnectorMessage getMessage1() {
        DomibusConnectorMessage message = new DomibusConnectorMessage();
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
        DomibusConnectorMessage message = new DomibusConnectorMessage();
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

    @ParameterizedTest
    @MethodSource("provideParameters")
    void testMatch_shouldEvaluateToTrue(String expression, DomibusConnectorMessage message) {
        // ExpressionParser expressionParser = new ExpressionParser(expression);
        RoutingRulePattern rulePattern = new RoutingRulePattern(expression);
        boolean result = rulePattern.matches(message);
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideNotMatchingParameters")
    void testMatch_shouldNotMatch(String expression, DomibusConnectorMessage message) {
        // ExpressionParser expressionParser = new ExpressionParser(expression);
        RoutingRulePattern rulePattern = new RoutingRulePattern(expression);
        boolean result = rulePattern.matches(message);
        assertThat(result).isFalse();
    }
}
