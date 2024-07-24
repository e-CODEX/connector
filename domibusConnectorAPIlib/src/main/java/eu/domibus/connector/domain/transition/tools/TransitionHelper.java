/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.transition.tools;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import lombok.experimental.UtilityClass;

/**
 * TransitionHelper is a utility class containing helper methods for handling transitions.
 */
@UtilityClass
public class TransitionHelper {
    public static boolean isConfirmationMessage(DomibusConnectorMessageType message) {
        return message.getMessageContent() == null && !message.getMessageConfirmations().isEmpty();
    }
}
