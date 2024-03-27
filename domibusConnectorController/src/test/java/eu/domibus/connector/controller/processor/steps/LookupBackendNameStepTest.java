package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.controller.routing.DCRoutingRulesManagerImpl;
import eu.domibus.connector.controller.routing.RoutingRule;
import eu.domibus.connector.controller.routing.RoutingRulePattern;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;


class LookupBackendNameStepTest {
    // regel 1: ist ein backend in der msg gesetzt? (dann lasse es wie es ist)
    @Test
    void given_a_message_that_has_a_specified_backend_then_the_message_should_be_sent_there() {
        // Arrange
        final ConfigurationPropertyManagerService configMock = Mockito.mock(ConfigurationPropertyManagerService.class);
        final DCMessagePersistenceService peristenceMock = Mockito.mock(DCMessagePersistenceService.class);
        final DCRoutingRulesManagerImpl routingMock = Mockito.mock(DCRoutingRulesManagerImpl.class);

        final RoutingRule routingRule1 = new RoutingRule();
        routingRule1.setLinkName("fooLink");
        routingRule1.setMatchClause(new RoutingRulePattern(
                "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, 'Connector-TEST')), equals(ServiceType, " +
                        "'urn:e-codex:services:'))"));
        final HashMap<String, RoutingRule> routingRules = new HashMap<>();
        routingRules.put("foobarId", routingRule1);

        Mockito.when(routingMock.getBackendRoutingRules(any())).thenReturn(routingRules);
        Mockito.when(routingMock.getDefaultBackendName(any())).thenReturn("DEFAULT_BACKEND");
        Mockito.when(routingMock.isBackendRoutingEnabled(any())).thenReturn(true);

        final LookupBackendNameStep sut = new LookupBackendNameStep(routingMock, peristenceMock, configMock);

        final DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.getMessageDetails().setService(DomainEntityCreator.createServiceEPO());
        message.getMessageDetails().setConnectorBackendClientName("BACKEND_ON_THE_MESSAGE");

        // Act
        sut.executeStep(message);

        // Assert
        assertThat(message.getMessageDetails().getConnectorBackendClientName()).isEqualTo("BACKEND_ON_THE_MESSAGE");
    }


    // regel4: setze das default backend
    @Test
    void given_a_message_without_a_specified_backend_and_no_conversation_id_when_backend_routing_is_enabled_but_not_route_matches_then_the_message_is_sent_to_the_default_backend() {
        // Arrange
        final ConfigurationPropertyManagerService configMock = Mockito.mock(ConfigurationPropertyManagerService.class);
        final DCMessagePersistenceService peristenceMock = Mockito.mock(DCMessagePersistenceService.class);
        final DCRoutingRulesManagerImpl routingMock = Mockito.mock(DCRoutingRulesManagerImpl.class);

        final RoutingRule routingRule1 = new RoutingRule();
        routingRule1.setLinkName("fooLink");
        routingRule1.setMatchClause(new RoutingRulePattern(
                "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, 'Connector-TEST')), equals(ServiceType, " +
                        "'urn:e-codex:services:'))"));
        final HashMap<String, RoutingRule> routingRules = new HashMap<>();
        routingRules.put("foobarId", routingRule1);

        Mockito.when(routingMock.getBackendRoutingRules(any())).thenReturn(routingRules);
        Mockito.when(routingMock.getDefaultBackendName(any())).thenReturn("DEFAULT_BACKEND");
        Mockito.when(routingMock.isBackendRoutingEnabled(any())).thenReturn(true);

        final LookupBackendNameStep sut = new LookupBackendNameStep(routingMock, peristenceMock, configMock);

        final DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.getMessageDetails().setService(DomainEntityCreator.createServiceEPO());
        //        message.getMessageDetails().setConnectorBackendClientName("EPO_backend");

        // Act
        sut.executeStep(message);

        // Assert
        assertThat(message.getMessageDetails().getConnectorBackendClientName()).isEqualTo("DEFAULT_BACKEND");
    }

    // regel4: setze das default backend
    @Test
    void given_a_message_without_a_specified_backend_and_no_conversation_id_when_backend_routing_is_disabled_then_the_message_is_sent_to_the_default_backend() {
        // Arrange
        final ConfigurationPropertyManagerService configMock = Mockito.mock(ConfigurationPropertyManagerService.class);
        final DCMessagePersistenceService peristenceMock = Mockito.mock(DCMessagePersistenceService.class);
        final DCRoutingRulesManagerImpl routingMock = Mockito.mock(DCRoutingRulesManagerImpl.class);

        final RoutingRule routingRule1 = new RoutingRule();
        routingRule1.setLinkName("fooLink");
        routingRule1.setMatchClause(new RoutingRulePattern(
                "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, 'Connector-TEST')), equals(ServiceType, " +
                        "'urn:e-codex:services:'))"));
        final HashMap<String, RoutingRule> routingRules = new HashMap<>();
        routingRules.put("foobarId", routingRule1);

        Mockito.when(routingMock.getBackendRoutingRules(any())).thenReturn(routingRules);
        Mockito.when(routingMock.getDefaultBackendName(any())).thenReturn("DEFAULT_BACKEND");
        Mockito.when(routingMock.isBackendRoutingEnabled(any())).thenReturn(false);

        final LookupBackendNameStep sut = new LookupBackendNameStep(routingMock, peristenceMock, configMock);

        final DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.getMessageDetails().setService(DomainEntityCreator.createServiceEPO());
        //        message.getMessageDetails().setConnectorBackendClientName("EPO_backend");

        // Act
        sut.executeStep(message);

        // Assert
        assertThat(message.getMessageDetails().getConnectorBackendClientName()).isEqualTo("DEFAULT_BACKEND");
    }

    // regel2: gibt es eine Nachricht mit der gleichen ConversationId, die ein Backend gesetzt hat? Wenn ja nimm das.
    @Test
    void given_a_message_without_a_specified_backend_but_with_a_conversation_id_when_processed_then_the_message_is_sent_to_the_backend_that_was_configured_for_that_conversation_id() {
        // Arrange
        final ConfigurationPropertyManagerService configMock = Mockito.mock(ConfigurationPropertyManagerService.class);

        final DCMessagePersistenceService peristenceMock = Mockito.mock(DCMessagePersistenceService.class);
        List<DomibusConnectorMessage> messagesByConversationId = new ArrayList<>();
        final DomibusConnectorMessage otherMessageWithConvId = DomainEntityCreator.createMessage();
        otherMessageWithConvId.getMessageDetails().setConversationId("fooConvId");
        otherMessageWithConvId.getMessageDetails()
                              .setConnectorBackendClientName("BACKEND_OF_ANOTHER_MSG_WITH_SAME_CONV_ID");
        messagesByConversationId.add(otherMessageWithConvId);
        Mockito.when(peristenceMock.findMessagesByConversationId(any())).thenReturn(messagesByConversationId);

        final DCRoutingRulesManagerImpl routingMock = Mockito.mock(DCRoutingRulesManagerImpl.class);

        final RoutingRule routingRule1 = new RoutingRule();
        routingRule1.setLinkName("fooLink");
        routingRule1.setMatchClause(new RoutingRulePattern(
                "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, 'Connector-TEST')), equals(ServiceType, " +
                        "'urn:e-codex:services:'))"));
        final HashMap<String, RoutingRule> routingRules = new HashMap<>();
        routingRules.put("foobarId", routingRule1);

        Mockito.when(routingMock.getBackendRoutingRules(any())).thenReturn(routingRules);
        Mockito.when(routingMock.getDefaultBackendName(any())).thenReturn("DEFAULT_BACKEND");
        Mockito.when(routingMock.isBackendRoutingEnabled(any())).thenReturn(false);

        final LookupBackendNameStep sut = new LookupBackendNameStep(routingMock, peristenceMock, configMock);

        final DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.getMessageDetails().setService(DomainEntityCreator.createServiceEPO());

        // Act
        sut.executeStep(message);

        // Assert
        assertThat(message.getMessageDetails().getConnectorBackendClientName()).isEqualTo(
                "BACKEND_OF_ANOTHER_MSG_WITH_SAME_CONV_ID");
    }

    // regel3: Werte die Routing Rules aus, die erste  Routing Rule die matched gewinnt. Matching geht nach routing
    // rule priorität.
    @Test
    void given_a_message_without_a_specified_backend_when_backend_routing_is_enabled_and_a_rule_matches_then_the_message_is_sent_to_the_backend_associated_with_that_rule() {
        // Arrange
        final ConfigurationPropertyManagerService configMock = Mockito.mock(ConfigurationPropertyManagerService.class);
        final DCMessagePersistenceService peristenceMock = Mockito.mock(DCMessagePersistenceService.class);
        final DCRoutingRulesManagerImpl routingMock = Mockito.mock(DCRoutingRulesManagerImpl.class);

        final RoutingRule routingRule1 = new RoutingRule();
        routingRule1.setLinkName("BACKEND_ASSOCIATED_WITH_RULE");
        routingRule1.setMatchClause(new RoutingRulePattern(
                "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, 'Connector-TEST')), equals(ServiceType, " +
                        "'urn:e-codex:services:'))"));
        final HashMap<String, RoutingRule> routingRules = new HashMap<>();
        routingRules.put("foobarId", routingRule1);

        Mockito.when(routingMock.getBackendRoutingRules(any())).thenReturn(routingRules);
        Mockito.when(routingMock.getDefaultBackendName(any())).thenReturn("DEFAULT_BACKEND");
        Mockito.when(routingMock.isBackendRoutingEnabled(any())).thenReturn(true);

        final LookupBackendNameStep sut = new LookupBackendNameStep(routingMock, peristenceMock, configMock);

        final DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.getMessageDetails().setAction(new DomibusConnectorAction("ConTest_Form"));
        message.getMessageDetails().setService(new DomibusConnectorService("Connector-TEST", "urn:e-codex:services:"));

        // Act
        sut.executeStep(message);

        // Assert
        assertThat(message.getMessageDetails()
                          .getConnectorBackendClientName()).isEqualTo("BACKEND_ASSOCIATED_WITH_RULE");
    }

    // regel3: Werte die Routing Rules aus, die erste  Routing Rule die matched gewinnt. Matching geht nach routing
    // rule priorität.
    @Test
    @DisplayName(
            "given_a_message_without_a_specified_backend_when_backend_routing_is_enabled_and_a_rule_matches_then_the_message_is_sent_to_the_backend_associated_with_that_rule"
    )
    void given_a_message_without_a_specified_backend_when_backend_routing_is_enabled_and_multiple_matching_rules_then_the_message_is_sent_to_the_backend_associated_with_that_rule() {
        // Arrange
        final ConfigurationPropertyManagerService configMock = Mockito.mock(ConfigurationPropertyManagerService.class);
        final DCMessagePersistenceService peristenceMock = Mockito.mock(DCMessagePersistenceService.class);
        final DCRoutingRulesManagerImpl routingMock = Mockito.mock(DCRoutingRulesManagerImpl.class);

        final HashMap<String, RoutingRule> routingRules = new HashMap<>();
        final RoutingRule routingRule1 = new RoutingRule();
        routingRule1.setLinkName("BACKEND_ASSOCIATED_WITH_RULE_LOWER_PRIORITY");
        routingRule1.setMatchClause(new RoutingRulePattern(
                "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, 'Connector-TEST')), equals(ServiceType, " +
                        "'urn:e-codex:services:'))"));
        routingRule1.setPriority(-2000);
        routingRules.put("rule1", routingRule1);

        final RoutingRule routingRule2 = new RoutingRule();
        routingRule2.setLinkName("BACKEND_ASSOCIATED_WITH_RULE_HIGHER_PRIORITY");
        routingRule2.setMatchClause(new RoutingRulePattern(
                "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, 'Connector-TEST')), equals(ServiceType, " +
                        "'urn:e-codex:services:'))"));
        routingRule2.setPriority(0);
        routingRules.put("rule2", routingRule2);

        Mockito.when(routingMock.getBackendRoutingRules(any())).thenReturn(routingRules);
        Mockito.when(routingMock.getDefaultBackendName(any())).thenReturn("DEFAULT_BACKEND");
        Mockito.when(routingMock.isBackendRoutingEnabled(any())).thenReturn(true);

        final LookupBackendNameStep sut = new LookupBackendNameStep(routingMock, peristenceMock, configMock);

        final DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.getMessageDetails().setAction(new DomibusConnectorAction("ConTest_Form"));
        message.getMessageDetails().setService(new DomibusConnectorService("Connector-TEST", "urn:e-codex:services:"));

        // Act
        sut.executeStep(message);

        // Assert
        assertThat(message.getMessageDetails().getConnectorBackendClientName()).isEqualTo(
                "BACKEND_ASSOCIATED_WITH_RULE_HIGHER_PRIORITY");
    }
}

