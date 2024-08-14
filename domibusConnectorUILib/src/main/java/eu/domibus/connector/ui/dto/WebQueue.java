/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dto;

import java.util.ArrayList;
import java.util.List;
import javax.jms.Message;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * The WebQueue class represents a web queue that contains messages. It provides methods to get and
 * set the name of the queue, as well as get and set the messages and dead letter queue (DLQ)
 * messages associated with the queue.
 */
public class WebQueue {
    @Setter
    private String name;
    @Getter
    @Setter
    private List<Message> messages = new ArrayList<>();
    @Getter
    @Setter
    private List<Message> dlqMessages = new ArrayList<>();
    @Getter
    @Setter
    private int msgsOnQueue;
    @Getter
    @Setter
    private int msgsOnDlq;

    public String getName() {
        return StringUtils.capitalize(name).replace("/([A-Z])/g", "$1").trim();
    }
}
