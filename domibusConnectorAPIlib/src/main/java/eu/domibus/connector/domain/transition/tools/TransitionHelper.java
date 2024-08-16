/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
