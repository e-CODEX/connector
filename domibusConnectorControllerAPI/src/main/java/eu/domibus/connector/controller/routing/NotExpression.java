/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
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
