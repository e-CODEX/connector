/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
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
