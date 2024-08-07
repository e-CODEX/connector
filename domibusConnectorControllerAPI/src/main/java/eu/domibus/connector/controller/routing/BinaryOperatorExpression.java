/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import lombok.Getter;

/**
 * Represents a binary operator expression.
 */
@Getter
public class BinaryOperatorExpression extends Expression {
    private final TokenType operand;
    private final Expression exp1;
    private final Expression exp2;

    /**
     * Represents a binary operator expression.
     *
     * @param t    The token type of the binary operator.
     * @param exp1 The first expression operand.
     * @param exp2 The second expression operand.
     */
    public BinaryOperatorExpression(TokenType t, Expression exp1, Expression exp2) {
        this.operand = t;
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    @Override
    boolean evaluate(DomibusConnectorMessage message) {
        return switch (operand) {
            case TokenType.OR -> exp1.evaluate(message) || exp2.evaluate(message);
            case TokenType.AND -> exp1.evaluate(message) && exp2.evaluate(message);
            default -> throw new RuntimeException("Unsupported OPERAND %s".formatted(operand));
        };
    }

    public String toString() {
        return "%s(%s, %s)".formatted(operand.toString(), exp1, exp2);
    }
}
