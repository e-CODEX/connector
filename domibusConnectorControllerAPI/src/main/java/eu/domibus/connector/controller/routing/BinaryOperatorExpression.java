package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;


public class BinaryOperatorExpression extends Expression {
    private final TokenType operand;
    private final Expression exp1;
    private final Expression exp2;

    public BinaryOperatorExpression(TokenType t, Expression exp1, Expression exp2) {
        this.operand = t;
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    @Override
    boolean evaluate(DomibusConnectorMessage message) {
        if (operand == TokenType.OR) {
            return exp1.evaluate(message) || exp2.evaluate(message);
        } else if (operand == TokenType.AND) {
            return exp1.evaluate(message) && exp2.evaluate(message);
        } else {
            throw new RuntimeException(String.format("Unsupported OPERAND %s", operand));
        }
    }

    public String toString() {
        return String.format("%s(%s, %s)", operand.toString(), exp1, exp2);
    }

    public TokenType getOperand() {
        return operand;
    }

    public Expression getExp1() {
        return exp1;
    }

    public Expression getExp2() {
        return exp2;
    }
}
