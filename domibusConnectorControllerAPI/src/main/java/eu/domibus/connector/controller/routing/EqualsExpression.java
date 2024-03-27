package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;


public class EqualsExpression extends MatchExpression {
    private final TokenType as4Attribute;
    private final String valueString;

    public EqualsExpression(TokenType as4Attribute, String valueString) {
        this.as4Attribute = as4Attribute;
        this.valueString = valueString;
    }

    @Override
    boolean evaluate(DomibusConnectorMessage message) {
        return valueString.equals(RoutingRulePattern.extractAs4Value(message, as4Attribute));
    }

    @Override
    public TokenType getMatchOperator() {
        return TokenType.EQUALS;
    }

    @Override
    public TokenType getAs4Attribute() {
        return as4Attribute;
    }

    @Override
    public String getValueString() {
        return valueString;
    }

    public String toString() {
        return String.format("%s(%s, '%s')", TokenType.EQUALS, as4Attribute, valueString);
    }
}
