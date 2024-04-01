package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import eu.domibus.connector.controller.routing.MatchExpression;


public class MatchExpressionField extends CustomField<MatchExpression> {
    HorizontalLayout horizontalLayout;

    public MatchExpressionField() {
        horizontalLayout = new HorizontalLayout();
    }

    @Override
    protected MatchExpression generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(MatchExpression newPresentationValue) {
    }
}
