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
import java.util.ArrayList;
import java.util.List;
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
