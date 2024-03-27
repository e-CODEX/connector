package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;


/**
 * The routing rule grammar:
 * {@literal
 * ##BNF RoutingRulePattern
 * tag::BNF[]
 * <ROUTING_RULE_PATTERN> ::= <BOOLEAN_EXPRESSION> | <COMPARE_EXPRESSION> | <NOT_EXPRESSION>
 * <BOOLEAN_EXPRESSION> ::= <OPERAND>(<ROUTING_RULE_PATTERN>, <ROUTING_RULE_PATTERN>)
 * <COMPARE_EXPRESSION> ::= equals(<AS4_TYPE>, '<VALUE>') | startswith(<AS4_TYPE>, '<VALUE>')
 * <NOT_EXPRESSION> ::= not(<ROUTING_RULE_PATTERN>)
 * <OPERAND> ::= "&" | "|"
 * <AS4_TYPE> ::= ServiceType | ServiceName | FinalRecipient | Action | FromPartyId | FromPartyRole | FromPartyIdType
 * <VALUE> ::= <VALUE><LETTER> | <LETTER>
 * <LETTER> can be every letter [a-z][A-Z][0-9] other printable characters might work, but they untested! ['\|&)(
 * will definitiv not work!]
 * <p>
 * end::BNF[]
 * ##BNF}
 */
public class RoutingRulePattern {
    private final String matchRule;
    private Expression expression;

    public RoutingRulePattern(String pattern) {
        this.matchRule = pattern;
        createMatcher(pattern);
    }

    static String extractAs4Value(DomibusConnectorMessage message, TokenType as4Attribute) {
        DomibusConnectorMessageDetails details = message.getMessageDetails();
        if (as4Attribute == TokenType.AS4_SERVICE_NAME) {
            return details.getService().getService();
        } else if (as4Attribute == TokenType.AS4_SERVICE_TYPE) {
            return details.getService().getServiceType();
        } else if (as4Attribute == TokenType.AS4_ACTION) {
            return details.getAction().getAction();
        } else if (as4Attribute == TokenType.AS4_FINAL_RECIPIENT) {
            return details.getFinalRecipient();
        } else if (as4Attribute == TokenType.AS4_FROM_PARTY_ID_TYPE) {
            return details.getFromParty().getPartyIdType();
        } else if (as4Attribute == TokenType.AS4_FROM_PARTY_ID) {
            return details.getFromParty().getPartyId();
        } else if (as4Attribute == TokenType.AS4_FROM_PARTY_ROLE) {
            return details.getFromParty().getRole();
        } else {
            throw new RuntimeException("Unsupported AS4 Attribute to match!");
        }
    }

    private void createMatcher(final String pattern) {
        // TODO: error handling...
        this.expression = new ExpressionParser(pattern).getParsedExpression().get();
    }

    public boolean matches(DomibusConnectorMessage message) {
        return this.expression.evaluate(message);
    }

    public String toString() {
        return this.expression.toString();
    }

    public String getMatchRule() {
        return matchRule;
    }

    public Expression getExpression() {
        return expression;
    }
}
