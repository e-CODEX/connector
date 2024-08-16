/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
