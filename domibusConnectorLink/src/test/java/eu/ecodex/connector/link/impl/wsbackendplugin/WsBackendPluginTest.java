/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.impl.wsbackendplugin;

import static eu.ecodex.connector.link.LinkTestContext.SUBMIT_TO_CONNECTOR_QUEUE;
import static eu.ecodex.connector.link.service.DCLinkPluginConfiguration.LINK_PLUGIN_PROFILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.controller.service.TransportStateService;
import eu.ecodex.connector.domain.enums.TransportState;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDetails;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.model.DomibusConnectorTransportStep;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessagesType;
import eu.ecodex.connector.link.LinkTestContext;
import eu.ecodex.connector.link.api.ActiveLinkPartner;
import eu.ecodex.connector.link.api.LinkPlugin;
import eu.ecodex.connector.link.service.DCActiveLinkManagerService;
import eu.ecodex.connector.link.wsbackendplugin.ConnectorClientTestBackend;
import eu.ecodex.connector.persistence.service.DCMessagePersistenceService;
import eu.ecodex.connector.testdata.TransitionCreator;
import eu.ecodex.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.ecodex.connector.ws.backend.webservice.EmptyRequestType;
import eu.ecodex.connector.ws.backend.webservice.GetMessageByIdRequest;
import eu.ecodex.connector.ws.backend.webservice.ListPendingMessageIdsResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith({SpringExtension.class})
@SpringBootTest(
    classes = {LinkTestContext.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles({"wsbackendplugin-test", "plugin-test", "test", LINK_PLUGIN_PROFILE_NAME})
@Log4j2
class WsBackendPluginTest {
    @LocalServerPort
    int localServerPort;
    @Autowired
    DCActiveLinkManagerService linkManagerService;
    @Autowired
    @Qualifier(SUBMIT_TO_CONNECTOR_QUEUE)
    public BlockingQueue<DomibusConnectorMessage> toConnectorSubmittedMessages;
    @Autowired
    DCMessagePersistenceService messagePersistenceServiceMock;
    @Autowired
    TransportStateService transportStateServiceMock;
    @MockBean
    PlatformTransactionManager txManager;

    @Test
    void testPluginIsLoaded() {
        List<LinkPlugin> availableLinkPlugins = linkManagerService.getAvailableLinkPlugins();
        assertThat(availableLinkPlugins).extracting(LinkPlugin::getPluginName)
                                        .contains("wsbackendplugin");
    }

    @Test
    void testPluginConfigs() {
        Collection<ActiveLinkPartner> activeLinkPartners =
            linkManagerService.getActiveLinkPartners();
        assertThat(activeLinkPartners).hasSize(2); // 1 backend is configured...
    }

    // @Test
    public void testSleep() throws InterruptedException {
        log.info("Port is: [{}]", localServerPort);
        Thread.sleep(100000);
    }

    @Test
    void submitMessageToBackend() throws InterruptedException {
        String clientName = "bob";
        String connectorAddress = getServerAddress();

        ConnectorClientTestBackend connectorClientTestBackend =
            ConnectorClientTestBackend.startContext(clientName, connectorAddress, -1);
        DomibusConnectorBackendWebService domibusConnectorBackendWebService =
            connectorClientTestBackend.backendConnectorClientProxy();

        DomibusConnectorMessageType msg = TransitionCreator.createEpoMessage();
        msg.getMessageDetails().setEbmsMessageId("Ebms1234");
        domibusConnectorBackendWebService.submitMessage(msg);

        DomibusConnectorMessage poll = toConnectorSubmittedMessages.poll(20, TimeUnit.SECONDS);

        assertThat(poll).isNotNull();
        assertThat(poll)
            .extracting(DomibusConnectorMessage::getMessageDetails)
            .extracting(DomibusConnectorMessageDetails::getEbmsMessageId)
            .isEqualTo("Ebms1234");
    }

    /**
     * Test passive backend -) start a test connector_client -) connector client pulls message from
     * (passive) connector backend.
     */
    @Test
    void testPassiveBackend_requestMessages() {
        DomibusConnectorMessage epoMessage1 = DomainEntityCreator.createEpoMessage();
        epoMessage1.getMessageDetails().setConnectorBackendClientName("backend_bob");
        epoMessage1.setConnectorMessageId(new DomibusConnectorMessageId("con1"));
        epoMessage1.getMessageDetails().setEbmsMessageId("ebms1");
        DomibusConnectorTransportStep step1 = new DomibusConnectorTransportStep();
        step1.setTransportedMessage(epoMessage1);

        DomibusConnectorMessage epoMessage2 = DomainEntityCreator.createEpoMessage();
        epoMessage2.getMessageDetails().setConnectorBackendClientName("backend_bob");
        epoMessage2.setConnectorMessageId(new DomibusConnectorMessageId("con2"));
        epoMessage2.getMessageDetails().setEbmsMessageId("ebms2");
        DomibusConnectorTransportStep step2 = new DomibusConnectorTransportStep();
        step2.setTransportedMessage(epoMessage2);

        var backendName = new DomibusConnectorLinkPartner.LinkPartnerName("backend_bob");
        // return the 2 message steps, when plugin asks for it
        Mockito.when(
                   transportStateServiceMock.getPendingTransportsForLinkPartner(backendName)
               )
               .thenReturn(Stream.of(step1, step2).toList());

        var clientName = "bob";
        var connectorAddress = getServerAddress();
        ConnectorClientTestBackend connectorClientTestBackend =
            ConnectorClientTestBackend.startContext(clientName, connectorAddress, -1);
        DomibusConnectorBackendWebService domibusConnectorBackendWebService =
            connectorClientTestBackend.backendConnectorClientProxy();

        EmptyRequestType emptyRequestType = new EmptyRequestType();
        DomibusConnectorMessagesType domibusConnectorMessagesType =
            domibusConnectorBackendWebService.requestMessages(emptyRequestType);

        List<DomibusConnectorMessageType> messages = domibusConnectorMessagesType.getMessages();

        assertThat(messages).hasSize(2);

        connectorClientTestBackend.shutdown();
    }

    /**
     * Test passive backend -) start a test connector_client -) connector client requests pending
     * message ids -) downloads messages -) acks each message.
     */
    @Test
    void testPassiveBackend_requestMsgIds_and_Download() {
        DomibusConnectorMessage epoMessage1 = DomainEntityCreator.createEpoMessage();
        epoMessage1.getMessageDetails().setConnectorBackendClientName("backend_bob");
        epoMessage1.setConnectorMessageId(new DomibusConnectorMessageId("con01"));
        epoMessage1.getMessageDetails().setEbmsMessageId("ebms1");
        DomibusConnectorTransportStep step1 = new DomibusConnectorTransportStep();
        step1.setTransportedMessage(epoMessage1);
        TransportStateService.TransportId tid1 = new TransportStateService.TransportId("t1");
        step1.setTransportId(tid1);
        DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate update1 =
            new DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate();
        update1.setTransportState(TransportState.PENDING);
        step1.addTransportStatus(update1);

        DomibusConnectorMessage epoMessage2 = DomainEntityCreator.createEpoMessage();
        epoMessage2.getMessageDetails().setConnectorBackendClientName("backend_bob");
        epoMessage2.setConnectorMessageId(new DomibusConnectorMessageId("con02"));
        epoMessage2.getMessageDetails().setEbmsMessageId("ebms2");
        DomibusConnectorTransportStep step2 = new DomibusConnectorTransportStep();
        step2.setTransportedMessage(epoMessage2);
        TransportStateService.TransportId tid2 = new TransportStateService.TransportId("t2");
        step2.setTransportId(tid2);
        DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate update2 =
            new DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate();
        update2.setTransportState(TransportState.PENDING);
        step2.addTransportStatus(update2);

        var backendName = new DomibusConnectorLinkPartner.LinkPartnerName("backend_bob");
        // return the 2 pending message ids, when plugin asks for it
        Mockito.when(
                   transportStateServiceMock.getPendingTransportsForLinkPartner(backendName))
               .thenReturn(Stream.of(step1, step2).toList());
        // return the corresponding step
        Mockito.when(transportStateServiceMock.getTransportStepById(tid1))
               .thenReturn(Optional.of(step1));
        Mockito.when(transportStateServiceMock.getTransportStepById(tid2))
               .thenReturn(Optional.of(step2));

        var clientName = "bob";
        var connectorAddress = getServerAddress();
        ConnectorClientTestBackend connectorClientTestBackend =
            ConnectorClientTestBackend.startContext(clientName, connectorAddress, -1);
        DomibusConnectorBackendWebService domibusConnectorBackendWebService =
            connectorClientTestBackend.backendConnectorClientProxy();

        ListPendingMessageIdsResponse listPendingMessageIdsResponseBeforeACK =
            domibusConnectorBackendWebService.listPendingMessageIds(new EmptyRequestType());

        List<DomibusConnectorMessageType> downloadedMessages = new ArrayList<>();
        for (String id : listPendingMessageIdsResponseBeforeACK.getMessageTransportIds()) {
            GetMessageByIdRequest req = new GetMessageByIdRequest();
            req.setMessageTransportId(id);

            DomibusConnectorMessageType messageById =
                domibusConnectorBackendWebService.getMessageById(req);
            downloadedMessages.add(messageById);
        }

        Assertions.assertAll(
            "",
            () -> assertThat(listPendingMessageIdsResponseBeforeACK.getMessageTransportIds())
                .hasSize(2),
            () -> assertThat(downloadedMessages).hasSize(2)
        );

        connectorClientTestBackend.shutdown();
    }

    private String getServerAddress() {
        return "http://localhost:" + localServerPort + "/services/backend";
    }
}
