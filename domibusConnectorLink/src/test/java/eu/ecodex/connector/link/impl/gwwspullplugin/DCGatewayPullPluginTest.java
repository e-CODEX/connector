/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.impl.gwwspullplugin;

import static eu.ecodex.connector.link.LinkTestContext.SUBMIT_TO_CONNECTOR_QUEUE;
import static eu.ecodex.connector.link.service.DCLinkPluginConfiguration.LINK_PLUGIN_PROFILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.link.LinkTestContext;
import eu.ecodex.connector.link.api.ActiveLinkPartner;
import eu.ecodex.connector.link.api.LinkPlugin;
import eu.ecodex.connector.link.service.DCActiveLinkManagerService;
import eu.ecodex.connector.testdata.TransitionCreator;
import eu.ecodex.connector.ws.gateway.webservice.GetMessageByIdRequest;
import eu.ecodex.connector.ws.gateway.webservice.ListPendingMessageIdsResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.TestSocketUtils;

@ExtendWith({SpringExtension.class})
@SpringBootTest(
    classes = {LinkTestContext.class},
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles({"gwwspullplugin-test", "test", LINK_PLUGIN_PROFILE_NAME, "plugin-gwwspullplugin"})
@Disabled
@SuppressWarnings({"checkstyle:LocalVariableName", "checkstyle:MethodName"})
class DCGatewayPullPluginTest {
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
     * @param registry the DynamicPropertyRegistry to register the properties
     */
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add(
            "connector.link.gateway.link-config.properties.gw-address",
            () -> "http://localhost:" + GET_PORT() + "/services/pullservice"
        );
    }

    static TestGwWebService testGwWebService;

    @SuppressWarnings("checkstyle:LocalVariableName")
    @BeforeAll
    public static void setupTestGwContext() {
        HashMap props = new HashMap<>();
        props.put("server.port", GET_PORT());
        var CTX = TestGwWebService.startContext(props);
        testGwWebService = CTX.getBean(TestGwWebService.class);
    }

    @Autowired
    DCActiveLinkManagerService linkManagerService;
    @Autowired
    @Qualifier(SUBMIT_TO_CONNECTOR_QUEUE)
    public BlockingQueue<DomibusConnectorMessage> toConnectorSubmittedMessages;

    @Test
    void testPluginIsLoaded() {
        List<LinkPlugin> availableLinkPlugins = linkManagerService.getAvailableLinkPlugins();
        assertThat(availableLinkPlugins).extracting(LinkPlugin::getPluginName)
                                        .contains("gwwspullplugin");
    }

    @Test
    void testPluginConfigs() {
        Collection<ActiveLinkPartner> activeLinkPartners =
            linkManagerService.getActiveLinkPartners();
        assertThat(activeLinkPartners).hasSize(1);
    }

    @Test
    void testPullFromTestGw() throws Exception {
        ListPendingMessageIdsResponse response = new ListPendingMessageIdsResponse();
        response.getMessageIds().add("id1");
        response.getMessageIds().add("id2");

        Mockito.when(testGwWebService.listPendingMessagesMock().listPendingMessageIds())
               .thenReturn(response);

        DomibusConnectorMessageType msg1 = TransitionCreator.createMessage();
        msg1.getMessageDetails().setEbmsMessageId("ebms1");
        DomibusConnectorMessageType msg2 = TransitionCreator.createMessage();
        msg2.getMessageDetails().setEbmsMessageId("ebms2");

        Mockito.reset(testGwWebService.getMessageByIdMock());
        Mockito.when(testGwWebService.getMessageByIdMock()
                                     .getMessageById(Mockito.any(GetMessageByIdRequest.class)))
               .thenReturn(msg1, msg2);

        DomibusConnectorMessage poll1 = toConnectorSubmittedMessages.poll(120, TimeUnit.SECONDS);
        assertThat(poll1).isNotNull();
        DomibusConnectorMessage poll2 = toConnectorSubmittedMessages.poll(30, TimeUnit.SECONDS);
        assertThat(poll2).isNotNull();
    }

    @Test
    void testSubmitToGw() throws Exception {
        DomibusConnectorMessageType msg =
            testGwWebService.submittedMessagesList().poll(30, TimeUnit.SECONDS);
        assertThat(msg).isNotNull();
    }
}
