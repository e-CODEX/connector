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

import com.vaadin.flow.component.customfield.CustomField;
import eu.domibus.connector.controller.routing.BinaryOperatorExpression;
import eu.domibus.connector.controller.routing.Expression;
import eu.domibus.connector.controller.routing.MatchExpression;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for creating different types of {@link CustomField} based on the given
 * {@link Expression} object.
 */
@Component
@SuppressWarnings("squid:S1135")
public class ExpressionFieldFactory {
    /**
     * Creates a custom field of type {@link CustomField} based on the given {@link Expression}.
     *
     * @param expression The expression for which the custom field needs to be created.
     * @return A custom field of type {@link CustomField} based on the given expression.
     */
    public CustomField<? extends Expression> createField(Expression expression) {
        if (expression instanceof BinaryOperatorExpression) {
            return new BinaryOperatorExpressionField(this);
        } else if (expression instanceof MatchExpression) {
            return new MatchExpressionField();
        } else {
            // TODO: error?
            return null;
        }
    }
}
