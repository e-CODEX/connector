package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;


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

    public Expression getExp1() {
        return exp1;
    }

    public Token getTokenTypeAndValue() {
        return tokenTypeAndValue;
    }

    public String toString() {
        return tokenTypeAndValue.tokenType.getHumanString() + "(" + exp1.toString() + ")";
    }
}
