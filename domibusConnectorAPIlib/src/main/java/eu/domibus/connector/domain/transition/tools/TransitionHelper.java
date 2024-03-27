package eu.domibus.connector.domain.transition.tools;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;


public class TransitionHelper {
    public static boolean isConfirmationMessage(DomibusConnectorMessageType message) {
        return message.getMessageContent() == null && message.getMessageConfirmations().size() > 0;
    }
}
