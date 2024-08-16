/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import eu.domibus.connector.controller.routing.MatchExpression;

/**
 * Represents a custom field for match expressions. This field allows users to create and edit match
 * expressions.
 */
@SuppressWarnings("squid:S1135")
public class MatchExpressionField extends CustomField<MatchExpression> {
    HorizontalLayout horizontalLayout;

    /**
     * Constructor.
     */
    public MatchExpressionField() {
        horizontalLayout = new HorizontalLayout();
    }

    @Override
    protected MatchExpression generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(MatchExpression newPresentationValue) {
        // TODO see why this method body is empty
    }
}
