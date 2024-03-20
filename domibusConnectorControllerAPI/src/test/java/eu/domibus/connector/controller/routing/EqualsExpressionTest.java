package eu.domibus.connector.controller.routing;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EqualsExpressionTest {

    @Test
    public void equalsExpressionTest_shouldMatch() {
        String ACTION = "Action1";
        EqualsExpression equalsExpression = new EqualsExpression(TokenType.AS4_ACTION, ACTION);

        DomibusConnectorMessage message = new DomibusConnectorMessage();
        message.setMessageDetails(new DomibusConnectorMessageDetails());
        message.getMessageDetails().setAction(new DomibusConnectorAction());
        message.getMessageDetails().getAction().setAction(ACTION);

        boolean result = equalsExpression.evaluate(message);
        assertThat(result).isTrue();
    }

    @Test
    public void equalsExpressionTest_shouldNotMatch() {
        String ACTION = "Action1";
        EqualsExpression equalsExpression = new EqualsExpression(TokenType.AS4_ACTION, ACTION);

        DomibusConnectorMessage message = new DomibusConnectorMessage();
        message.setMessageDetails(new DomibusConnectorMessageDetails());
        message.getMessageDetails().setAction(new DomibusConnectorAction());
        message.getMessageDetails().getAction().setAction("OtherAction");

        boolean result = equalsExpression.evaluate(message);
        assertThat(result).isFalse();
    }

}