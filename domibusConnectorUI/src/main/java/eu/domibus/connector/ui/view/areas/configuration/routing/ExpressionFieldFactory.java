package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.customfield.CustomField;
import eu.domibus.connector.controller.routing.BinaryOperatorExpression;
import eu.domibus.connector.controller.routing.Expression;
import eu.domibus.connector.controller.routing.MatchExpression;
import org.springframework.stereotype.Component;


@Component
public class ExpressionFieldFactory {
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
