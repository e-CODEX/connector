/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The routing rule grammar.
 * {@literal
 * ##BNF RoutingRulePattern
 * tag::BNF[]
 * <ROUTING_RULE_PATTERN> ::= <BOOLEAN_EXPRESSION> | <COMPARE_EXPRESSION> | <NOT_EXPRESSION>
 * <BOOLEAN_EXPRESSION> ::= <OPERAND>(<ROUTING_RULE_PATTERN>, <ROUTING_RULE_PATTERN>)
 * <COMPARE_EXPRESSION> ::= equals(<AS4_TYPE>, '<VALUE>') | startswith(<AS4_TYPE>, '<VALUE>')
 * <NOT_EXPRESSION> ::= not(<ROUTING_RULE_PATTERN>)
 * <OPERAND> ::= "&" | "|"
 * <AS4_TYPE> ::= ServiceType | ServiceName | FinalRecipient | Action | FromPartyId | FromPartyRole
 * | FromPartyIdType
 * <VALUE> ::= <VALUE><LETTER> | <LETTER>
 * <LETTER> can be every letter [a-z][A-Z][0-9] other printable characters might work, but
 * they untested! ['\|&)( will definitiv not work!]
 * <p>
 * end::BNF[]
 * ##BNF}
 */
@SuppressWarnings("squid:S1135")
@Getter
public class RoutingRulePattern {
    private static final Logger LOGGER = LogManager.getLogger(RoutingRulePattern.class);
    private final String matchRule;
    private Expression expression;

    public RoutingRulePattern(String pattern) {
        this.matchRule = pattern;
        createMatcher(pattern);
    }

    private void createMatcher(final String pattern) {
        // TODO: error handling...
        this.expression = new ExpressionParser(pattern).getParsedExpression().get();
    }

    public boolean matches(DomibusConnectorMessage message) {
        return this.expression.evaluate(message);
    }

    static String extractAs4Value(DomibusConnectorMessage message, TokenType as4Attribute) {
        DomibusConnectorMessageDetails details = message.getMessageDetails();
        return switch (as4Attribute) {
            case TokenType.AS4_SERVICE_NAME -> details.getService().getService();
            case TokenType.AS4_SERVICE_TYPE -> details.getService().getServiceType();
            case TokenType.AS4_ACTION -> details.getAction().getAction();
            case TokenType.AS4_FINAL_RECIPIENT -> details.getFinalRecipient();
            case TokenType.AS4_FROM_PARTY_ID_TYPE -> details.getFromParty().getPartyIdType();
            case TokenType.AS4_FROM_PARTY_ID -> details.getFromParty().getPartyId();
            case TokenType.AS4_FROM_PARTY_ROLE -> details.getFromParty().getRole();
            default -> throw new RuntimeException("Unsupported AS4 Attribute to match!");
        };
    }

    @Override
    public String toString() {
        return this.expression.toString();
    }
}
