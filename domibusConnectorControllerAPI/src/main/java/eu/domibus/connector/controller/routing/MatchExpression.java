/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

/**
 * Represents a match expression.
 * A match expression is used to match certain conditions for evaluating expressions.
 */
public abstract class MatchExpression extends Expression {
    public abstract TokenType getMatchOperator();

    public abstract TokenType getAs4Attribute();

    public abstract String getValueString();
}
