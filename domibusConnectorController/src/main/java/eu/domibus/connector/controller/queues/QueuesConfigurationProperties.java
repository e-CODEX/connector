/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
