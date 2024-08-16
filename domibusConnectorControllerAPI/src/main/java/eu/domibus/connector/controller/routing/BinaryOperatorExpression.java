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
            default -> throw new RuntimeException(String.format("Unsupported OPERAND %s", operand));
        };
    }

    public String toString() {
        return String.format("%s(%s, %s)", operand.toString(), exp1, exp2);
    }
}
