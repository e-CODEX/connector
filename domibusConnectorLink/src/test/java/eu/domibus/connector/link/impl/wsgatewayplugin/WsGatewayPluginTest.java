package eu.domibus.connector.link.impl.wsgatewayplugin;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.testdata.TransitionCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.SocketUtils;
import test.eu.domibus.connector.link.LinkTestContext;
import test.eu.domibus.connector.link.wsgatewayplugin.TestGW;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static eu.domibus.connector.link.service.DCLinkPluginConfiguration.LINK_PLUGIN_PROFILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static test.eu.domibus.connector.link.LinkTestContext.SUBMIT_TO_CONNECTOR_QUEUE;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {LinkTestContext.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {

        }
)
@ActiveProfiles({"wsgatewayplugin-test", "test", LINK_PLUGIN_PROFILE_NAME, "plugin-wsgatewayplugin"})
@Disabled
class WsGatewayPluginTest {

    private static final Logger LOGGER = LogManager.getLogger(WsGatewayPluginTest.class);

    private static Integer PORT;

    /**
     *  find free tcp port on first call and then always return this port
     * @return a free tcp port
     */
    public static int GET_PORT() {
        if (PORT == null) {
            PORT = SocketUtils.findAvailableTcpPort();
        }
        return PORT;
    }

    /**
     * Register the correct gw-address within the spring-test-context
     * @param registry
     */
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("connector.link.gateway.link-config.properties.gw-address", () -> "http://localhost:" + GET_PORT() + "/services/submission");
    }

    @LocalServerPort
    int localServerPort;

    @Autowired
    @Qualifier(SUBMIT_TO_CONNECTOR_QUEUE)
    public BlockingQueue<DomibusConnectorMessage> toConnectorSubmittedMessages;


    static TestGW testGwWebService;

    @BeforeAll
    public static void setupTestGwContext() {

    }

    @BeforeEach
    public void beforeEach() {
        if (testGwWebService == null) {
            testGwWebService = TestGW.startTestGw("http://localhost:" + localServerPort + "/services/gateway", GET_PORT());
        }
    }

    @Autowired
    DCActiveLinkManagerService linkManagerService;

//    @Autowired
//    DCPluginBasedGatewaySubmissionService gatewaySubmissionService;

    @Test
    public void testPluginIsLoaded() {
        List<LinkPlugin> availableLinkPlugins = linkManagerService.getAvailableLinkPlugins();
        assertThat(availableLinkPlugins).extracting(LinkPlugin::getPluginName).contains("wsgatewayplugin");
    }

    @Test
    public void testPluginConfigs() {
        Collection<ActiveLinkPartner> activeLinkPartners = linkManagerService.getActiveLinkPartners();
        assertThat(activeLinkPartners).hasSize(1);
    }


    @Test
    public void testSubmitToConnector() throws InterruptedException {
        DomibusConnectorMessageType message = TransitionCreator.createEpoMessage();
        DomibsConnectorAcknowledgementType domibsConnectorAcknowledgementType = testGwWebService.getConnectorDeliveryClient().deliverMessage(message);

        assertThat(domibsConnectorAcknowledgementType.isResult()).isTrue();


        DomibusConnectorMessage poll = toConnectorSubmittedMessages.poll(30, TimeUnit.SECONDS);

        assertThat(poll).isNotNull();


    }

    @Test
    public void testSubmitToGw() throws Exception {
        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
//        gatewaySubmissionService.submitToGateway(message);

        DomibusConnectorMessageType msg = testGwWebService.deliveredMessagesList().poll(30, TimeUnit.SECONDS);
        assertThat(msg).isNotNull();

    }

}