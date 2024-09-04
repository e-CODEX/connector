/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.transition.tools;

import eu.ecodex.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 *
 */
public class TransitionHelperTest {

    @Test
    public void testIsConfirmationMessage_messageWithContent_shouldBeFalse() {
        DomibusConnectorMessageType messageType = new DomibusConnectorMessageType();
        messageType.setMessageContent(new DomibusConnectorMessageContentType());

        assertThat(TransitionHelper.isConfirmationMessage(messageType)).isFalse();
    }

    @Test
    public void testIsConfirmationMessage_messageWithContentAndConfirmation_shouldBeFalse() {
        DomibusConnectorMessageType messageType = new DomibusConnectorMessageType();
        messageType.setMessageContent(new DomibusConnectorMessageContentType());
        messageType.getMessageConfirmations().add(new DomibusConnectorMessageConfirmationType());

        assertThat(TransitionHelper.isConfirmationMessage(messageType)).isFalse();
    }

    @Test
    public void testIsConfirmationMessage_messageWithNoContentAndConfirmation_shouldBeTrue() {
        DomibusConnectorMessageType messageType = new DomibusConnectorMessageType();
        messageType.getMessageConfirmations().add(new DomibusConnectorMessageConfirmationType());

        assertThat(TransitionHelper.isConfirmationMessage(messageType)).isTrue();
    }



}
