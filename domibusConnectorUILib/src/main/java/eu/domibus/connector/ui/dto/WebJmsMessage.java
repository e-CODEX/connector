/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dto;

import javax.jms.Message;
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
