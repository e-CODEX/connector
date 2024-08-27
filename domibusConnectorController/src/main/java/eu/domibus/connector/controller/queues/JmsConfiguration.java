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

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.domibus.connector.common.annotations.DomainModelJsonObjectMapper;
import jakarta.jms.Queue;
import jakarta.validation.Validator;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * This class provides configuration for JMS queues in the application.
 */
@EnableJms
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(QueuesConfigurationProperties.class)
public class JmsConfiguration {
    public static final String TO_CONNECTOR_QUEUE_BEAN = "toConnectorQueueBean";
    public static final String TO_CONNECTOR_DEAD_LETTER_QUEUE_BEAN =
        "toConnectorDeadLetterQueueBean";
    public static final String TO_LINK_QUEUE_BEAN = "toLinkQueueBean";
    public static final String TO_LINK_DEAD_LETTER_QUEUE_BEAN = "toLinkDeadLetterQueueBean";
    public static final String TO_CLEANUP_QUEUE_BEAN = "toCleanupQueueBean";
    public static final String TO_CLEANUP_DEAD_LETTER_QUEUE_BEAN = "toCleanupDeadLetterQueueBean";

    /**
     * Creates a MessageConverter bean using Jackson and JMS.
     * The method uses a ValidationMappingJackson2MessageConverter that performs bean validation
     * before serializing the object.
     * The object mapper and validator parameters are required dependencies.
     *
     * @param objectMapper The object mapper used for serialization/deserialization.
     *                     Must be annotated with @DomainModelJsonObjectMapper.
     * @param validator    The Validator used for bean validation.
     * @return The created MessageConverter bean.
     */
    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter(
        @DomainModelJsonObjectMapper ObjectMapper objectMapper,
        final Validator validator) {
        // using a message converter which calls bean validation before serialisation object
        return new ValidationMappingJackson2MessageConverter(objectMapper, validator);
    }

    @Bean(TO_CONNECTOR_QUEUE_BEAN)
    public Queue toConnectorQueue(QueuesConfigurationProperties config) {
        return new ActiveMQQueue(config.getToConnectorControllerQueue());
    }

    @Bean(TO_CONNECTOR_DEAD_LETTER_QUEUE_BEAN)
    public Queue toConnectorErrorQueueBean(QueuesConfigurationProperties config) {
        return new ActiveMQQueue(config.getToConnectorControllerDeadLetterQueue());
    }

    @Bean(TO_LINK_QUEUE_BEAN)
    public Queue toLinkQueue(QueuesConfigurationProperties config) {
        return new ActiveMQQueue(config.getToLinkQueue());
    }

    @Bean(TO_LINK_DEAD_LETTER_QUEUE_BEAN)
    public Queue toLinkErrorQueue(QueuesConfigurationProperties config) {
        return new ActiveMQQueue(config.getToLinkDeadLetterQueue());
    }

    @Bean(TO_CLEANUP_QUEUE_BEAN)
    public Queue toCleanupQueue(QueuesConfigurationProperties config) {
        return new ActiveMQQueue(config.getCleanupQueue());
    }

    @Bean(TO_CLEANUP_DEAD_LETTER_QUEUE_BEAN)
    public Queue toCleanupErrorQueue(QueuesConfigurationProperties config) {
        return new ActiveMQQueue(config.getCleanupDeadLetterQueue());
    }

    /**
     * Configures embedded Artemis Broker specific settings for queues.
     *
     * @param prop The QueuesConfigurationProperties object containing the queue properties.
     * @return An ArtemisConfigurationCustomizer that modifies the Artemis configuration.
     */
    @Bean
    ArtemisConfigurationCustomizer artemisCustomizer(QueuesConfigurationProperties prop) {
        return configuration -> {
            // Configure Queue Settings, DLQ, Expiry, ...
            var addressSettings1 = basicAddressConfig();
            addressSettings1.setDeadLetterAddress(
                new SimpleString(prop.getToLinkDeadLetterQueue()));
            configuration.getAddressesSettings()
                .put(prop.getToLinkQueue(), addressSettings1);

            var addressSettings2 = basicAddressConfig();
            addressSettings2.setDeadLetterAddress(
                new SimpleString(prop.getToConnectorControllerDeadLetterQueue()));
            configuration.getAddressesSettings()
                .put(prop.getToConnectorControllerQueue(), addressSettings2);

            var addressSettings3 = basicAddressConfig();
            addressSettings3.setDeadLetterAddress(
                new SimpleString(prop.getCleanupDeadLetterQueue()));
            configuration.getAddressesSettings()
                .put(prop.getCleanupQueue(), addressSettings3);

            // set config for DLQ.# addresses
            var dlqAddressSettings = new AddressSettings();
            dlqAddressSettings.setAutoDeleteAddresses(false);
            dlqAddressSettings.setAutoDeleteCreatedQueues(false);
            dlqAddressSettings.setAutoCreateAddresses(true);
            configuration.getAddressesSettings()
                .put(QueuesConfigurationProperties.DLQ_PREFIX + "#", dlqAddressSettings);
        };
    }

    private AddressSettings basicAddressConfig() {
        var addressSettings = new AddressSettings();
        addressSettings.setAutoCreateQueues(true);
        addressSettings.setDefaultAddressRoutingType(RoutingType.ANYCAST);
        addressSettings.setAutoCreateDeadLetterResources(true);
        addressSettings.setAutoCreateAddresses(true);
        addressSettings.setAutoCreateExpiryResources(true);
        addressSettings.setAutoDeleteQueues(false);
        addressSettings.setAutoDeleteAddresses(false);
        addressSettings.setDeadLetterAddress(new SimpleString("DLA"));
        addressSettings.setExpiryAddress(new SimpleString("expiry"));
        addressSettings.setMaxDeliveryAttempts(5);
        addressSettings.setRedeliveryDelay(60000); // set 60s redelivery delay
        addressSettings.setRedeliveryMultiplier(2);
        return addressSettings;
    }
}
