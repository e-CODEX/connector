/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.queues;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for queues.
 */
@Data
@ConfigurationProperties(prefix = QueuesConfigurationProperties.PREFIX)
public class QueuesConfigurationProperties {
    public static final String PREFIX = "connector.queues";
    public static final String QUEUE_PREFIX = "connector.queues.";
    public static final String DLQ_PREFIX = "DLQ.";
    private String toConnectorControllerQueue = QUEUE_PREFIX + "toConnectorControllerQueue";
    private String toConnectorControllerDeadLetterQueue = DLQ_PREFIX + toConnectorControllerQueue;
    private String toLinkQueue = QUEUE_PREFIX + "submitToLinkQueue";
    private String toLinkDeadLetterQueue = DLQ_PREFIX + toLinkQueue;
    private String cleanupQueue = QUEUE_PREFIX + "cleanUpQueue";
    private String cleanupDeadLetterQueue = DLQ_PREFIX + cleanupQueue;
}
