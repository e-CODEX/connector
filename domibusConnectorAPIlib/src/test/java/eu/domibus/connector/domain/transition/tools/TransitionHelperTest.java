package eu.domibus.connector.domain.transition.tools;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 *
 */
class TransitionHelperTest {
    @Test
    void testIsConfirmationMessage_messageWithContent_shouldBeFalse() {
        DomibusConnectorMessageType messageType = new DomibusConnectorMessageType();
        messageType.setMessageContent(new DomibusConnectorMessageContentType());

        assertThat(TransitionHelper.isConfirmationMessage(messageType)).isFalse();
    }

    @Test
    void testIsConfirmationMessage_messageWithContentAndConfirmation_shouldBeFalse() {
        DomibusConnectorMessageType messageType = new DomibusConnectorMessageType();
        messageType.setMessageContent(new DomibusConnectorMessageContentType());
        messageType.getMessageConfirmations().add(new DomibusConnectorMessageConfirmationType());

        assertThat(TransitionHelper.isConfirmationMessage(messageType)).isFalse();
    }

    @Test
    void testIsConfirmationMessage_messageWithNoContentAndConfirmation_shouldBeTrue() {
        DomibusConnectorMessageType messageType = new DomibusConnectorMessageType();
        messageType.getMessageConfirmations().add(new DomibusConnectorMessageConfirmationType());

        assertThat(TransitionHelper.isConfirmationMessage(messageType)).isTrue();
    }
}
