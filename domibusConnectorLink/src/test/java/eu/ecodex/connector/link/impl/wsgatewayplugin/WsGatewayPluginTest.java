/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.impl.wsgatewayplugin;

import static eu.ecodex.connector.link.LinkTestContext.SUBMIT_TO_CONNECTOR_QUEUE;
import static eu.ecodex.connector.link.service.DCLinkPluginConfiguration.LINK_PLUGIN_PROFILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.link.LinkTestContext;
import eu.ecodex.connector.link.api.ActiveLinkPartner;
import eu.ecodex.connector.link.api.LinkPlugin;
import eu.ecodex.connector.link.service.DCActiveLinkManagerService;
import eu.ecodex.connector.link.wsgatewayplugin.TestGW;
import eu.ecodex.connector.testdata.TransitionCreator;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.TestSocketUtils;

@ExtendWith({SpringExtension.class})
@SpringBootTest(
    classes = {LinkTestContext.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(
    {"wsgatewayplugin-test", "test", LINK_PLUGIN_PROFILE_NAME, "plugin-wsgatewayplugin"}
)
@Disabled
@SuppressWarnings({"checkstyle:LocalVariableName", "checkstyle:MethodName", "squid:S1135"})
class WsGatewayPluginTest {
    private static Integer PORT;

    /**
     * Find free tcp port on first call and then always return this port.
     *
     * @return a free tcp port
     */
    public static int GET_PORT() {
        if (PORT == null) {
            PORT = TestSocketUtils.findAvailableTcpPort();
        }
        return PORT;
    }

    /**
     * Register the correct gw-address within the spring-test-context.
     *
     * @param registry the DynamicPropertyRegistry to register the properties in
     */
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add(
            "connector.link.gateway.link-config.properties.gw-address",
            () -> "http://localhost:" + GET_PORT() + "/services/submission"
        );
    }

    @LocalServerPort
    int localServerPort;
    @Autowired
    @Qualifier(SUBMIT_TO_CONNECTOR_QUEUE)
    public BlockingQueue<DomibusConnectorMessage> toConnectorSubmittedMessages;
    static TestGW testGwWebService;

    @BeforeAll
    public static void setupTestGwContext() {
        // TODO see why this method is empty
    }

    @BeforeEach
    public void beforeEach() {
        if (testGwWebService == null) {
            testGwWebService =
                TestGW.startTestGw(
                    "http://localhost:" + localServerPort + "/services/gateway",
                    GET_PORT()
                );
        }
    }

    @Autowired
    DCActiveLinkManagerService linkManagerService;

    @Test
    void testPluginIsLoaded() {
        List<LinkPlugin> availableLinkPlugins = linkManagerService.getAvailableLinkPlugins();
        assertThat(availableLinkPlugins).extracting(LinkPlugin::getPluginName)
                                        .contains("wsgatewayplugin");
    }

    @Test
    void testPluginConfigs() {
        Collection<ActiveLinkPartner> activeLinkPartners =
            linkManagerService.getActiveLinkPartners();
        assertThat(activeLinkPartners).hasSize(1);
    }

    @Test
    void testSubmitToConnector() throws InterruptedException {
        DomibusConnectorMessageType message = TransitionCreator.createEpoMessage();
        DomibsConnectorAcknowledgementType domibsConnectorAcknowledgementType =
            testGwWebService.getConnectorDeliveryClient().deliverMessage(message);

        assertThat(domibsConnectorAcknowledgementType.isResult()).isTrue();

        DomibusConnectorMessage poll = toConnectorSubmittedMessages.poll(30, TimeUnit.SECONDS);

        assertThat(poll).isNotNull();
    }

    @Test
    void testSubmitToGw() throws Exception {
        DomibusConnectorMessageType msg =
            testGwWebService.deliveredMessagesList().poll(30, TimeUnit.SECONDS);
        assertThat(msg).isNotNull();
    }
}
