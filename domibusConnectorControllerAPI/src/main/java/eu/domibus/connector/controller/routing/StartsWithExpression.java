/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

/**
 * Represents a match expression that checks if a certain attribute starts with a specific string.
 */
public class StartsWithExpression extends MatchExpression {
    private final TokenType as4Attribute;
    private final String startsWithString;

    StartsWithExpression(TokenType as4Attribute, String startsWithString) {
        this.as4Attribute = as4Attribute;
        this.startsWithString = startsWithString;
    }

    @Override
    boolean evaluate(DomibusConnectorMessage message) {
        return RoutingRulePattern.extractAs4Value(message, as4Attribute)
            .startsWith(startsWithString);
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
        return startsWithString;
    }

    @Override
    public String toString() {
        return "%s(%s, '%s')".formatted(TokenType.STARTSWITH, as4Attribute, startsWithString);
    }
}
