package test.eu.domibus.connector.link.wsgatewayplugin;

import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWebService;
import eu.domibus.connector.ws.gateway.submission.webservice.DomibusConnectorGatewaySubmissionWebService;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

/**
 * IMPLEMENTATION OF THE GW WEB SERVICE INTERFACE FOR TESTING PURPOSE.
 */
@SpringBootApplication(
    scanBasePackageClasses = {TestGW.class}, exclude = {DataSourceAutoConfiguration.class}
)
@ImportResource("classpath:/test/eu/domibus/connector/link/wsgatewayplugin/TestGatewayContext.xml")
@Profile("testgw")
public class TestGW {
    public static final String TO_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME =
        "toGwSubmittedMessagesBlockingQueue";

    /**
     * Start the TestGW component with the given connector address and server port.
     *
     * @param connectorAddress The address of the connector.
     * @param serverPort       The port on which the server is running.
     * @return The TestGW object.
     */
    public static TestGW startTestGw(String connectorAddress, int serverPort) {
        Properties props = new Properties();
        props.put("server.port", serverPort);
        props.put("connector.address", connectorAddress);

        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        SpringApplication springApp = builder.sources(TestGW.class)
                                             .web(WebApplicationType.SERVLET)
                                             .bannerMode(Banner.Mode.OFF)
                                             .profiles("testgw")
                                             .properties(props)
                                             .build();
        ConfigurableApplicationContext ctx = springApp.run();
        return ctx.getBean(TestGW.class);
    }

    /**
     * Retrieves the LinkedBlockingQueue of DomibusConnectorMessageType objects that represents the
     * submitted messages to the Gateway in the Gw component.
     *
     * @param context The ConfigurableApplicationContext from which to retrieve the bean.
     * @return The LinkedBlockingQueue of DomibusConnectorMessageType objects representing the
     *      submitted messages.
     */
    public static LinkedBlockingQueue<DomibusConnectorMessageType> getToGwSubmittedMessages(
        ConfigurableApplicationContext context) {
        return (LinkedBlockingQueue<DomibusConnectorMessageType>) context.getBean(
            TO_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME
        );
    }

    public DomibusConnectorGatewayDeliveryWebService getConnectorDeliveryClient() {
        return (DomibusConnectorGatewayDeliveryWebService) applicationContext.getBean(
            "connectorDeliveryClient");
    }

    public static String getSubmitAddress(ConfigurableApplicationContext ctx) {
        String port = ctx.getEnvironment().getRequiredProperty("local.server.port");
        return "http://localhost:" + port + "/services/submission";
    }

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @Bean(TO_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME)
    public LinkedBlockingQueue<DomibusConnectorMessageType> deliveredMessagesList() {
        return new LinkedBlockingQueue<>(20);
    }

    /**
     * Submits a message to the Domibus Connector Gateway for testing purposes.
     *
     * @return The acknowledgement response indicating the status of the submission.
     * @throws RuntimeException if the delivery request cannot be added to the queue.
     */
    @Bean("testGwSubmissionService")
    public DomibusConnectorGatewaySubmissionWebService testGwSubmissionService() {
        return deliverMessageRequest -> {
            LinkedBlockingQueue<DomibusConnectorMessageType> queue = deliveredMessagesList();

            if (!queue.offer(deliverMessageRequest)) {
                throw new RuntimeException("Could not add element to queue " + queue);
            }

            DomibsConnectorAcknowledgementType acknowledgementType =
                new DomibsConnectorAcknowledgementType();

            String messageId = UUID.randomUUID() + "_TESTGW";

            acknowledgementType.setResultMessage("resultMessage");
            acknowledgementType.setResult(true);
            acknowledgementType.setMessageId(messageId);

            return acknowledgementType;
        };
    }
}
