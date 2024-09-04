/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.routing;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDetails;
import org.junit.jupiter.api.Test;

class EqualsExpressionTest {
    @Test
    void equalsExpressionTest_shouldMatch() {
        final String ACTION = "Action1";

        DomibusConnectorMessage message = new DomibusConnectorMessage();
        message.setMessageDetails(new DomibusConnectorMessageDetails());
        message.getMessageDetails().setAction(new DomibusConnectorAction());
        message.getMessageDetails().getAction().setAction(ACTION);

        var equalsExpression = new EqualsExpression(TokenType.AS4_ACTION, ACTION);

        boolean result = equalsExpression.evaluate(message);
        assertThat(result).isTrue();
    }

    @Test
    void equalsExpressionTest_shouldNotMatch() {
        DomibusConnectorMessage message = new DomibusConnectorMessage();
        message.setMessageDetails(new DomibusConnectorMessageDetails());
        message.getMessageDetails().setAction(new DomibusConnectorAction());
        message.getMessageDetails().getAction().setAction("OtherAction");

        final String ACTION = "Action1";
        var equalsExpression = new EqualsExpression(TokenType.AS4_ACTION, ACTION);

        boolean result = equalsExpression.evaluate(message);
        assertThat(result).isFalse();
    }
}
