/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.transition.tools;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import lombok.experimental.UtilityClass;

/**
 * The TransitionHelper class provides helper methods related to transitions in the
 * DomibusConnectorMessageType class.
 */
@UtilityClass
public class TransitionHelper {

    /**
     * Determines if a given message is a confirmation message.
     *
     * @param message The message to check.
     * @return boolean True if the message is a confirmation message, false otherwise.
     */
    public static boolean isConfirmationMessage(DomibusConnectorMessageType message) {
        return message.getMessageContent() == null && !message.getMessageConfirmations().isEmpty();
    }
}
