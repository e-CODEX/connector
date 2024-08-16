/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

/**
 * Represents an equality expression used for matching.
 */
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
