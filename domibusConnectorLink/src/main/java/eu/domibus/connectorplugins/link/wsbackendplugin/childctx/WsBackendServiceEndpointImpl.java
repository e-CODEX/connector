/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connectorplugins.link.wsbackendplugin.childctx;

import eu.domibus.connector.controller.exception.DomibusConnectorBackendDeliveryException;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;
import eu.domibus.connector.ws.backend.webservice.GetMessageByIdRequest;
import eu.domibus.connector.ws.backend.webservice.ListPendingMessageIdsResponse;
import eu.domibus.connectorplugins.link.wsbackendplugin.WsBackendPluginActiveLinkPartner;
import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Handles transmitting messages (push/pull) from and to backendClients over webservice pushing
 * messages to backendClients are handled in different service.
 */
@SuppressWarnings("squid:S6813")
public class WsBackendServiceEndpointImpl implements DomibusConnectorBackendWebService {
    private static final Logger LOGGER = LogManager.getLogger(WsBackendServiceEndpointImpl.class);
    private WebServiceContext webServiceContext;
    @Autowired
    SubmitToConnector submitToConnector;
    @Autowired
    DomibusConnectorDomainMessageTransformerService transformerService;
    @Autowired
    DCMessagePersistenceService messagePersistenceService;
    @Autowired
    DCActiveLinkManagerService linkManager;
    @Autowired
    TransportStateService transportStateService;
    @Autowired
    DomibusConnectorMessageIdGenerator messageIdGenerator;
    @Autowired
    WsActiveLinkPartnerManager wsActiveLinkPartnerManager;
    @Autowired
    PlatformTransactionManager txManager;

    @Resource
    public void setWsContext(WebServiceContext webServiceContext) {
        this.webServiceContext = webServiceContext;
    }

    @Override
    public DomibusConnectorMessagesType requestMessages(EmptyRequestType requestMessagesRequest) {
        var getMessagesResponse = new DomibusConnectorMessagesType();
        try {
            Optional<DomibusConnectorLinkPartner> backendClientInfoByName = checkBackendClient();
            if (backendClientInfoByName.isPresent()) {
                List<DomibusConnectorTransportStep> pendingTransportsForLinkPartner =
                    transportStateService.getPendingTransportsForLinkPartner(
                        backendClientInfoByName.get().getLinkPartnerName());
                List<DomibusConnectorMessageType> collect = pendingTransportsForLinkPartner
                    .stream()
                    .map(DomibusConnectorTransportStep::getTransportedMessage)
                    // java9 should handle this better: Optional::streamOf
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(msg -> transformerService.transformDomainToTransition(msg))
                    .toList();
                getMessagesResponse.getMessages().addAll(collect);

                registerCleanupTransportInterceptor(pendingTransportsForLinkPartner);
            } else {
                LOGGER.warn("No backend found, returning empty DomibusConnectorMessagesType!");
            }
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        LOGGER.debug(
            "#requestMessages returns messages: [{}]", getMessagesResponse.getMessages().size());
        return getMessagesResponse;
    }

    /**
     * Registers an interceptor which updates the transport state to ACCEPTED after the SOAP-message
     * has been sent to the client.
     *
     * @param pendingTransportsForLinkPartner - all transportIds to set to ACCEPTED
     */
    private void registerCleanupTransportInterceptor(
        List<DomibusConnectorTransportStep> pendingTransportsForLinkPartner) {
        var messageContext = webServiceContext.getMessageContext();
        WrappedMessageContext wmc = (WrappedMessageContext) messageContext;
        var interceptor = new ProcessMessageAfterDeliveredToBackendInterceptor(
            pendingTransportsForLinkPartner.stream()
                                           .map(DomibusConnectorTransportStep::getTransportId)
                                           .toList()
        );
        wmc.getWrappedMessage().getInterceptorChain().add(interceptor);
    }

    @Override
    public DomibsConnectorAcknowledgementType submitMessage(
        DomibusConnectorMessageType submitMessageRequest) {
        var domibusConnectorMessageId = messageIdGenerator.generateDomibusConnectorMessageId();
        try (var conId = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
            domibusConnectorMessageId.getConnectorMessageId()
        )
        ) {
            var answer = new DomibsConnectorAcknowledgementType();
            try {
                LOGGER.debug("#submitMessage: message: [{}]", submitMessageRequest);

                Optional<DomibusConnectorLinkPartner> backendClientInfoByName =
                    checkBackendClient();

                if (backendClientInfoByName.isPresent()) {
                    DomibusConnectorLinkPartner linkPartner = backendClientInfoByName.get();
                    try (var mdc = MDC.putCloseable(
                        LoggingMDCPropertyNames.MDC_LINK_PARTNER_NAME,
                        linkPartner.getLinkPartnerName().getLinkName()
                    )) {

                        DomibusConnectorMessage msg =
                            transformerService.transformTransitionToDomain(
                                submitMessageRequest,
                                domibusConnectorMessageId
                            );
                        msg.getMessageDetails().setConnectorBackendClientName(
                            linkPartner.getLinkPartnerName().getLinkName());
                        LOGGER.debug(
                            "#submitMessage: setConnectorBackendClientName to [{}]", linkPartner);

                        submitToConnector.submitToConnector(msg, linkPartner);
                        answer.setResult(true);
                        answer.setMessageId(msg.getConnectorMessageIdAsString());
                    }
                } else {
                    java.lang.String error =
                        "The requested backend user is not available on connector!\n"
                            + "Check server logs for details!";
                    throw new RuntimeException(error);
                }
            } catch (Exception e) {
                LOGGER.error("Exception occured during submitMessage from backend", e);
                answer.setResult(false);
                answer.setMessageId(submitMessageRequest.getMessageDetails().getBackendMessageId());
                answer.setResultMessage(e.getMessage());
            }
            return answer;
        }
    }

    @Override
    public ListPendingMessageIdsResponse listPendingMessageIds(
        EmptyRequestType listPendingMessageIdsRequest) {
        var listPendingMessageIdsResponse = new ListPendingMessageIdsResponse();
        try {
            Optional<DomibusConnectorLinkPartner> backendClientInfoByName = checkBackendClient();
            if (backendClientInfoByName.isPresent()) {
                List<DomibusConnectorTransportStep> pendingTransportsForLinkPartner =
                    transportStateService.getPendingTransportsForLinkPartner(
                        backendClientInfoByName.get().getLinkPartnerName());

                List<String> pendingIds = pendingTransportsForLinkPartner
                    .stream()
                    .map(DomibusConnectorTransportStep::getTransportId)
                    .map(TransportStateService.TransportId::getTransportId)
                    .filter(Objects::nonNull)
                    .toList();

                listPendingMessageIdsResponse.getMessageTransportIds().addAll(pendingIds);
            } else {
                LOGGER.warn("No backend found, returning empty DomibusConnectorMessagesType!");
            }
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        LOGGER.debug(
            "#listPendingMessageIds returns pending message ids: [{}]",
            listPendingMessageIdsResponse.getMessageTransportIds().size()
        );
        return listPendingMessageIdsResponse;
    }

    @Override
    public DomibusConnectorMessageType getMessageById(GetMessageByIdRequest getMessageByIdRequest) {
        String messageTransportId = getMessageByIdRequest.getMessageTransportId();
        var transportId = new TransportStateService.TransportId(messageTransportId);
        Optional<DomibusConnectorTransportStep> transportStepById =
            transportStateService.getTransportStepById(transportId);

        if (transportStepById.isPresent()) {
            var domibusConnectorTransportStep = transportStepById.get();
            if (domibusConnectorTransportStep.isInPendingState()) {
                if (domibusConnectorTransportStep.getTransportedMessage().isPresent()) {
                    DomibusConnectorMessage msg =
                        domibusConnectorTransportStep.getTransportedMessage().get();

                    // add post invoke message processor
                    var messageContext = webServiceContext.getMessageContext();
                    WrappedMessageContext wmc = (WrappedMessageContext) messageContext;
                    var interceptor = new ProcessMessageAfterDownloaded(transportId);
                    wmc.getWrappedMessage().getInterceptorChain().add(interceptor);

                    return transformerService.transformDomainToTransition(msg);
                } else {
                    throw new IllegalStateException(String.format(
                        "The message with transport id [%s] is not readable anymore!",
                        messageTransportId
                    ));
                }
            } else {
                throw new IllegalArgumentException(String.format(
                    "The message with transport id [%s] is not in pending state!",
                    messageTransportId
                ));
            }
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "The provided transport id [%s] is not available!",
                    messageTransportId
                ));
        }
    }

    @Override
    public EmptyRequestType acknowledgeMessage(DomibusConnectorMessageResponseType ack) {
        var transportState = new TransportStateService.DomibusConnectorTransportState();
        if (ack.isResult()) {
            transportState.setStatus(TransportState.ACCEPTED);
        } else {
            transportState.setStatus(TransportState.FAILED);
        }
        transformerService.transformTransitionToDomain(ack.getMessageErrors());
        transportState.setRemoteMessageId(ack.getAssignedMessageId());
        transportState.setText(ack.getResultMessage());

        var transportId = new TransportStateService.TransportId(ack.getResponseForMessageId());
        transportStateService.updateTransportToBackendClientStatus(transportId, transportState);
        return new EmptyRequestType();
    }

    private Optional<DomibusConnectorLinkPartner> checkBackendClient()
        throws DomibusConnectorBackendDeliveryException {
        if (this.webServiceContext == null) {
            throw new RuntimeException("No webServiceContext found");
        }
        var userPrincipal = webServiceContext.getUserPrincipal();
        java.lang.String certificateDn = userPrincipal == null ? null : userPrincipal.getName();
        if (userPrincipal == null || certificateDn == null) {
            java.lang.String error = java.lang.String.format(
                "checkBackendClient: Cannot handle request because userPrincipal is [%s] the "
                    + "userName is [%s]. Cannot identify backend!",
                userPrincipal, certificateDn
            );
            LOGGER.error("#checkBackendClient: Throwing Exception: {}", error);
            throw new DomibusConnectorBackendDeliveryException(error);
        }
        Optional<WsBackendPluginActiveLinkPartner> linkPartner =
            wsActiveLinkPartnerManager.getDomibusConnectorLinkPartnerByDn(certificateDn);
        if (linkPartner.isEmpty()) {
            LOGGER.warn("No backend with certificate dn [{}] found!", certificateDn);
        } else {
            LOGGER.debug("#checkBackendClient: returning link partner: [{}]", linkPartner.get());
        }
        return linkPartner.map(WsBackendPluginActiveLinkPartner::getLinkPartner);
    }

    private class ProcessMessageAfterDownloaded extends AbstractPhaseInterceptor<Message> {
        private final TransportStateService.TransportId transportId;
        private final TransactionTemplate txTemplate;

        ProcessMessageAfterDownloaded(TransportStateService.TransportId transport) {
            super(Phase.POST_INVOKE);
            this.transportId = transport;
            var txDef = new DefaultTransactionDefinition();
            txDef.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
            this.txTemplate = new TransactionTemplate(txManager, txDef);
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            txTemplate.executeWithoutResult(state -> {
                String trace = "ProcessMessageAfterDownloaded: handleMessage: invoking "
                    + "backendSubmissionService.processMessageAfterDownloaded setting transport"
                    + " set to " + TransportState.PENDING_DOWNLOADED;
                LOGGER.trace(trace);
                var transportState = new TransportStateService.DomibusConnectorTransportState();
                transportState.setStatus(TransportState.PENDING_DOWNLOADED);
                transportStateService.updateTransportToBackendClientStatus(
                    transportId, transportState);
            });
        }
    }

    private class ProcessMessageAfterDeliveredToBackendInterceptor
        extends AbstractPhaseInterceptor<Message> {
        private final List<TransportStateService.TransportId> transports;

        ProcessMessageAfterDeliveredToBackendInterceptor(
            List<TransportStateService.TransportId> transports) {
            super(Phase.POST_INVOKE);
            this.transports = transports;
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            LOGGER.trace(
                "ProcessMessageAfterDeliveredToBackendInterceptor: handleMessage: "
                    + "invoking backendSubmissionService.processMessageAfterDeliveredToBackend"
            );

            transports.forEach(transportId -> {
                var transportState = new TransportStateService.DomibusConnectorTransportState();
                transportState.setStatus(TransportState.ACCEPTED);
                transportStateService.updateTransportToBackendClientStatus(
                    transportId, transportState);
            });
        }
    }
}
