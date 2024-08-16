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
