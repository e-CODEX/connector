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

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import lombok.Getter;

/**
 * Represents a negation expression.
 */
@Getter
public class NotExpression extends Expression {
    private final Expression exp1;
    private final Token tokenTypeAndValue;

    public NotExpression(Expression exp1, Token tokenTypeAndValue) {
        this.exp1 = exp1;
        this.tokenTypeAndValue = tokenTypeAndValue;
    }

    @Override
    boolean evaluate(DomibusConnectorMessage message) {
        return !exp1.evaluate(message);
    }

    public String toString() {
        return tokenTypeAndValue.tokenType.getHumanString() + "(" + exp1.toString() + ")";
    }
}
