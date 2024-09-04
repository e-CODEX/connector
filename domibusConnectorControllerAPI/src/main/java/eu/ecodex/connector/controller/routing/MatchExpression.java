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

/**
 * Represents a match expression.
 * A match expression is used to match certain conditions for evaluating expressions.
 */
public abstract class MatchExpression extends Expression {
    public abstract TokenType getMatchOperator();

    public abstract TokenType getAs4Attribute();

    public abstract String getValueString();
}
