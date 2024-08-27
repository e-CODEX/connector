/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import eu.domibus.connector.controller.routing.BinaryOperatorExpression;
import eu.domibus.connector.controller.routing.Expression;
import eu.domibus.connector.controller.routing.TokenType;

/**
 * Represents a custom field for binary operator expressions. This field allows users to create and
 * edit binary operator expressions. It consists of two expression fields and an operator select
 * box. The created binary operator expression can be accessed through the model value of this
 * field.
 */
public class BinaryOperatorExpressionField extends CustomField<BinaryOperatorExpression> {
    private final ExpressionFieldFactory expressionFieldFactory;
    private final HorizontalLayout horizontalLayout;
    private final Select<TokenType> operatorSelectBox = new Select<>();
    private Component exp1Field;
    private Component exp2Field;

    /**
     * Constructor.
     *
     * @param expressionFieldFactory The {@link ExpressionFieldFactory} used to create custom fields
     *                               for expressions
     */
    public BinaryOperatorExpressionField(ExpressionFieldFactory expressionFieldFactory) {
        this.expressionFieldFactory = expressionFieldFactory;
        horizontalLayout = new HorizontalLayout();
        this.add(horizontalLayout);

        operatorSelectBox.setItems(TokenType.AND, TokenType.OR);
        operatorSelectBox.setTextRenderer(Enum::name);
        operatorSelectBox.setWidth("0.5em");
        operatorSelectBox.setMaxWidth("0.5em");

        exp1Field = createExp1Expression();
        exp2Field = createExp2Expression();

        horizontalLayout.add(exp1Field);
        horizontalLayout.add(operatorSelectBox);
        horizontalLayout.add(exp2Field);
    }

    private Component createExp1Expression() {
        return new Button("Create Exp1 Exp");
    }

    private Component createExp2Expression() {
        return new Button("Create Exp2 Exp");
    }

    @Override
    protected BinaryOperatorExpression generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(BinaryOperatorExpression newPresentationValue) {
        Expression exp1 = newPresentationValue.getExp1();
        Component newExp1Field;
        if (exp1 != null) {
            newExp1Field = expressionFieldFactory.createField(exp1);
        } else {
            newExp1Field = createExp1Expression();
        }
        horizontalLayout.replace(exp1Field, newExp1Field);
        exp1Field = newExp1Field;

        Expression exp2 = newPresentationValue.getExp2();
        Component newExp2Field;
        if (exp2 != null) {
            newExp2Field = expressionFieldFactory.createField(exp2);
        } else {
            newExp2Field = createExp2Expression();
        }
        horizontalLayout.replace(exp2Field, newExp2Field);
        exp2Field = newExp2Field;

        operatorSelectBox.setValue(newPresentationValue.getOperand());
    }
}
