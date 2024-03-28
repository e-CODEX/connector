package test.eu.domibus.connector.link.wsgatewayplugin;


import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWebService;
import eu.domibus.connector.ws.gateway.submission.webservice.DomibusConnectorGatewaySubmissionWebService;
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

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * IMPLEMENTATION OF THE GW WEB SERIVCE INTERFACE
 * FOR TESTING PURPOSE
 */
@SpringBootApplication(scanBasePackageClasses = {TestGW.class}, exclude = {DataSourceAutoConfiguration.class})
@ImportResource("classpath:/test/eu/domibus/connector/link/wsgatewayplugin/TestGatewayContext.xml")
@Profile("testgw")
public class TestGW {
    public static final String TO_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME = "toGwSubmittedMessagesBlockingQueue";
    @Autowired
    ConfigurableApplicationContext applicationContext;

    public static TestGW startTestGw(String connectorAddress, int serverPort) {
        Properties props = new Properties();
        props.put("server.port", serverPort);
        props.put("connector.address", connectorAddress);

        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        SpringApplication springApp = builder
                .sources(TestGW.class)
                .web(WebApplicationType.SERVLET)
                .bannerMode(Banner.Mode.OFF)
                .profiles("testgw")
                .properties(props)
                .build();
        ConfigurableApplicationContext ctx = springApp.run();
        return ctx.getBean(TestGW.class);
    }

    public static LinkedBlockingQueue<DomibusConnectorMessageType> getToGwSubmittedMessages(
            ConfigurableApplicationContext context) {
        return (LinkedBlockingQueue<DomibusConnectorMessageType>) context.getBean(
                TO_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME);
    }

    public static String getSubmitAddress(ConfigurableApplicationContext ctx) {
        String port = ctx.getEnvironment().getRequiredProperty("local.server.port");
        return "http://localhost:" + port + "/services/submission";
    }

    public DomibusConnectorGatewayDeliveryWebService getConnectorDeliveryClient() {
        return (DomibusConnectorGatewayDeliveryWebService) applicationContext.getBean("connectorDeliveryClient");
    }

    @Bean(TO_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME)
    public LinkedBlockingQueue<DomibusConnectorMessageType> deliveredMessagesList() {

        //        return Collections.synchronizedList(new ArrayList<>());
        return new LinkedBlockingQueue<>(20);
    }

    @Bean("testGwSubmissionService")
    public DomibusConnectorGatewaySubmissionWebService testGwSubmissionService() {
        return new DomibusConnectorGatewaySubmissionWebService() {
            @Override
            public DomibsConnectorAcknowledgementType submitMessage(DomibusConnectorMessageType deliverMessageRequest) {
                LinkedBlockingQueue<DomibusConnectorMessageType> queue = deliveredMessagesList();

                // messageList.add(deliverMessageRequest);
                if (!queue.offer(deliverMessageRequest)) {
                    throw new RuntimeException("Could not add element to queue " + queue);
                }

                DomibsConnectorAcknowledgementType acknowledgementType = new DomibsConnectorAcknowledgementType();

                String messageId = UUID.randomUUID() + "_TESTGW";

                acknowledgementType.setResultMessage("resultMessage");
                acknowledgementType.setResult(true);
                acknowledgementType.setMessageId(messageId);

                return acknowledgementType;
            }
        };
    }
}
