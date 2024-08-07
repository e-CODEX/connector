package eu.domibus.connector.ui.dto;

import lombok.Getter;

import jakarta.jms.Message;

@Getter
public class WebJmsMessage {
    private Message jmsMessage;
    private String jmsMessageId;
    private String connectorMessageId;
}
