/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.dto;

import jakarta.jms.Message;
import lombok.Getter;

/**
 * The WebJmsMessage class represents a wrapper for JMS messages that are being sent or received in
 * a web application.
 */
@Getter
public class WebJmsMessage {
    private Message jmsMessage;
    private String jmsMessageId;
    private String connectorMessageId;
}
