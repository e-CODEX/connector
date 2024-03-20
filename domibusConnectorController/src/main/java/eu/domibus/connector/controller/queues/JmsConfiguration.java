package eu.domibus.connector.controller.queues;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.domibus.connector.common.annotations.DomainModelJsonObjectMapper;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.config.CoreAddressConfiguration;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.Queue;
import javax.validation.Validator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableJms
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(QueuesConfigurationProperties.class)
public class JmsConfiguration {

    public static final String TO_CONNECTOR_QUEUE_BEAN = "toConnectorQueueBean";
    public static final String TO_CONNECTOR_DEAD_LETTER_QUEUE_BEAN = "toConnectorDeadLetterQueueBean";
    public static final String TO_LINK_QUEUE_BEAN = "toLinkQueueBean";
    public static final String TO_LINK_DEAD_LETTER_QUEUE_BEAN = "toLinkDeadLetterQueueBean";
    public static final String TO_CLEANUP_QUEUE_BEAN = "toCleanupQueueBean";
    public static final String TO_CLEANUP_DEAD_LETTER_QUEUE_BEAN = "toCleanupDeadLetterQueueBean";

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter(
            @DomainModelJsonObjectMapper ObjectMapper objectMapper,
            final Validator validator
            ) {
        //using a message converter which calls bean validation before serialisation object
        MessageConverter converter = new ValidationMappingJackson2MessageConverter(objectMapper, validator);
        return converter;
    }


    @Bean(TO_CONNECTOR_QUEUE_BEAN)
    public Queue toConnectorQueue(
            QueuesConfigurationProperties config
    ) {
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(config.getToConnectorControllerQueue());
        return activeMQQueue;
    }

    @Bean(TO_CONNECTOR_DEAD_LETTER_QUEUE_BEAN)
    public Queue toConnectorErrorQueueBean(
            QueuesConfigurationProperties config
    ) {
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(config.getToConnectorControllerDeadLetterQueue());
        return activeMQQueue;
    }


    @Bean(TO_LINK_QUEUE_BEAN)
    public Queue toLinkQueue(
            QueuesConfigurationProperties config
    ) {
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(config.getToLinkQueue());
        return activeMQQueue;
    }

    @Bean(TO_LINK_DEAD_LETTER_QUEUE_BEAN)
    public Queue toLinkErrorQueue(
            QueuesConfigurationProperties config
    ) {
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(config.getToLinkDeadLetterQueue());
        return activeMQQueue;
    }

    @Bean(TO_CLEANUP_QUEUE_BEAN)
    public Queue toCleanupQueue(
            QueuesConfigurationProperties config
    ) {
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(config.getCleanupQueue());
        return activeMQQueue;
    }

    @Bean(TO_CLEANUP_DEAD_LETTER_QUEUE_BEAN)
    public Queue toCleanupErrorQueue(
            QueuesConfigurationProperties config
    ) {
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(config.getCleanupDeadLetterQueue());
        return activeMQQueue;
    }


    /**
     * Configure embedded artemis Broker
     * @param prop
     * @return
     */
    @Bean
    ArtemisConfigurationCustomizer artemisCustomizer(QueuesConfigurationProperties prop) {
        return configuration -> {

            //Configure Queue Settings, DLQ, Expiry, ...
            AddressSettings addressSettings1 = basicAddressConfig();
            addressSettings1.setDeadLetterAddress(new SimpleString(prop.getToLinkDeadLetterQueue()));
            configuration.getAddressesSettings()
                    .put(prop.getToLinkQueue(), addressSettings1);

            AddressSettings addressSettings2 = basicAddressConfig();
            addressSettings2.setDeadLetterAddress(new SimpleString(prop.getToConnectorControllerDeadLetterQueue()));
            configuration.getAddressesSettings()
                    .put(prop.getToConnectorControllerQueue(), addressSettings2);

            AddressSettings addressSettings3 = basicAddressConfig();
            addressSettings3.setDeadLetterAddress(new SimpleString(prop.getCleanupDeadLetterQueue()));
            configuration.getAddressesSettings()
                    .put(prop.getCleanupQueue(), addressSettings3);

            //set config for DLQ.# addresses
            AddressSettings dlqAdressSettings = new AddressSettings();
            dlqAdressSettings.setAutoDeleteAddresses(false);
            dlqAdressSettings.setAutoDeleteCreatedQueues(false);
            dlqAdressSettings.setAutoCreateAddresses(true);
            configuration.getAddressesSettings()
                    .put(QueuesConfigurationProperties.DLQ_PREFIX + "#", dlqAdressSettings);


        };
    }

    private AddressSettings basicAddressConfig() {
        AddressSettings addressSettings = new AddressSettings();
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
        addressSettings.setRedeliveryDelay(60000); //set 60s redelivery delay
        addressSettings.setRedeliveryMultiplier(2);
        return addressSettings;
    }

}
