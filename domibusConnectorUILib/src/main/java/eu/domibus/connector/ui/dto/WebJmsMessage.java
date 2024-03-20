package eu.domibus.connector.ui.dto;

import lombok.Getter;

import javax.jms.Message;

@Getter
public class WebJmsMessage {
    private Message jmsMessage;
    private String jmsMessageId;
    private String connectorMessageId;
}
