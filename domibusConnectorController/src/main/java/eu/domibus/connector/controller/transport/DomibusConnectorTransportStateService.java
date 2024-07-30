/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.transport;

import static eu.domibus.connector.domain.model.helper.DomainModelHelper.isBusinessMessage;

import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.persistence.service.DomibusConnectorEvidencePersistenceService;
import eu.domibus.connector.persistence.service.DomibusConnectorMessageErrorPersistenceService;
import eu.domibus.connector.persistence.service.TransportStepPersistenceService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing transport state within DomibusConnector.
 */
@Service
@Transactional
public class DomibusConnectorTransportStateService implements TransportStateService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorTransportStateService.class);
    private final DCMessagePersistenceService messagePersistenceService;
    private final DomibusConnectorMessageErrorPersistenceService errorPersistenceService;
    private final TransportStepPersistenceService transportStepPersistenceService;
    private final DomibusConnectorEvidencePersistenceService evidencePersistenceService;

    /**
     * The DomibusConnectorTransportStateService class represents a service that manages the
     * transport state of messages in the
     * Domibus Connector. It provides methods for updating the transport state and creating
     * new transport instances.
     * This service relies on the DCMessagePersistenceService,
     * DomibusConnectorMessageErrorPersistenceService,
     * TransportStepPersistenceService, and DomibusConnectorEvidencePersistenceService to perform
     * its operations.
     *
     * @param messagePersistenceService       The service responsible for interacting with
     *                                        the database to manage message persistence.
     * @param errorPersistenceService         The service responsible for interacting with
     *                                        the database to manage error persistence.
     * @param transportStepPersistenceService The service responsible for interacting with
     *                                        the database to manage transport step persistence.
     * @param evidencePersistenceService      The service responsible for interacting with
     *                                        the database to manage evidence persistence.
     */
    public DomibusConnectorTransportStateService(
        DCMessagePersistenceService messagePersistenceService,
        DomibusConnectorMessageErrorPersistenceService errorPersistenceService,
        TransportStepPersistenceService transportStepPersistenceService,
        DomibusConnectorEvidencePersistenceService evidencePersistenceService) {
        this.messagePersistenceService = messagePersistenceService;
        this.errorPersistenceService = errorPersistenceService;
        this.transportStepPersistenceService = transportStepPersistenceService;
        this.evidencePersistenceService = evidencePersistenceService;
    }

    @Override
    @Transactional
    @SuppressWarnings("squid:S1135")
    public void updateTransportToGatewayStatus(TransportId transportId,
                                               DomibusConnectorTransportState transportState) {
        // TODO: if message is a business message and state is failed
        // trigger SUBMISSION_REJECTION
        // TODO: also send SUBMISSION_CONFIRMATION back here! Instead
        //  in ToGatewayBusinessMessageProcessor!

        this.updateTransportStatus(transportId, transportState, (DomibusConnectorMessage m) -> {
            if (isBusinessMessage(m)) {
                m.getMessageDetails().setEbmsMessageId(transportState.getRemoteMessageId());
                messagePersistenceService.updateMessageDetails(m);
                messagePersistenceService.setDeliveredToGateway(m);
                LOGGER.debug("Successfully updated business message [{}]", m);
            }
            m.getTransportedMessageConfirmations().forEach(c -> {
                try {
                    evidencePersistenceService.setConfirmationAsTransportedToGateway(c);
                } catch (Exception e) {
                    // any issue here should not prevent commit!
                    LOGGER.warn("Failed to set transport time stamp of confirmation!", e);
                }
            });
        });
    }

    @Override
    @Transactional
    @SuppressWarnings("squid:S1135")
    public void updateTransportToBackendClientStatus(
        TransportId transportId,
        DomibusConnectorTransportState transportState) {

        // TODO: if message is a business message and state is failed
        // trigger NON_DELIVERY
        // TODO: also send SUBMISSION_CONFIRMATION back here! Instead
        //  in ToGatewayBusinessMessageProcessor!

        this.updateTransportStatus(transportId, transportState, (DomibusConnectorMessage m) -> {
            if (isBusinessMessage(m)) {
                m.getMessageDetails().setBackendMessageId(transportState.getRemoteMessageId());
                messagePersistenceService.updateMessageDetails(m);
                messagePersistenceService.setMessageDeliveredToNationalSystem(m);
                LOGGER.debug("Successfully updated business message [{}]", m);
            }
            m.getTransportedMessageConfirmations().forEach(c -> {
                try {
                    evidencePersistenceService.setConfirmationAsTransportedToBackend(c);
                } catch (Exception e) {
                    // any issue here should not prevent commit!
                    LOGGER.warn("Failed to set transport time stamp of confirmation!", e);
                }
            });
        });
    }

    private void updateTransportStatus(TransportId transportId,
                                       DomibusConnectorTransportState transportState,
                                       SuccessHandler successHandler) {
        if (transportId == null) {
            throw new IllegalArgumentException("TransportId is not allowed to be null!");
        }
        transportState.setConnectorTransportId(transportId);
        if (transportState == null) {
            throw new IllegalArgumentException("TransportState is not allowed to be null!");
        }
        DomibusConnectorTransportStep transportStep =
            transportStepPersistenceService.getTransportStepByTransportId(transportId);

        if (StringUtils.isEmpty(transportStep.getRemoteMessageId())) {
            transportStep.setRemoteMessageId(transportState.getRemoteMessageId());
        }
        if (StringUtils.isEmpty(transportStep.getTransportSystemMessageId())) {
            transportStep.setTransportSystemMessageId(transportState.getTransportImplId());
        }
        var statusUpdate =
            new DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate();
        statusUpdate.setCreated(LocalDateTime.now());
        statusUpdate.setTransportState(transportState.getStatus());
        transportStep.addTransportStatus(statusUpdate);
        transportStepPersistenceService.update(transportStep);

        DomibusConnectorMessage message = transportStep.getTransportedMessage().orElse(null);

        if (message == null) {
            // cannot update transport for a null message maybe it's an evidence message,
            // but they don't have
            // a relation to connector message id yet...so cannot set transport state for them!
            LOGGER.debug(
                "#updateTransportToBackendStatus:: No message with transport id [{}] was found "
                    + "within database!",
                transportState.getConnectorTransportId()
            );
            return;
        }

        if (transportState.getStatus() == TransportState.ACCEPTED) {
            successHandler.success(message);
        } else if (transportState.getStatus() == TransportState.FAILED) {
            transportState.getMessageErrorList()
                .stream()
                .forEach(error ->
                             errorPersistenceService.persistMessageError(
                                 transportState
                                     .getConnectorTransportId()
                                     .getTransportId(),
                                 error
                             )
                );
        }
    }

    @Override
    public void updateTransportStatus(DomibusConnectorTransportState transportState) {
        this.updateTransportStatus(
            transportState.getConnectorTransportId(), transportState, m -> {});
    }

    @Override
    public TransportId createTransportFor(
        DomibusConnectorMessage message,
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {

        var transportStep = new DomibusConnectorTransportStep();
        transportStep.setLinkPartnerName(linkPartnerName);
        transportStep.setCreated(LocalDateTime.now());
        transportStep.setTransportedMessage(message);
        transportStep.setConnectorMessageId(message.getConnectorMessageId());

        transportStep = transportStepPersistenceService.createNewTransportStep(transportStep);
        LOGGER.debug(
            "#createTransportFor:: created new transport step within database with id [{}]",
            transportStep.getTransportId()
        );
        return transportStep.getTransportId();
    }

    @Override
    public List<DomibusConnectorTransportStep> getPendingTransportsForLinkPartner(
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {
        return transportStepPersistenceService.findPendingStepBy(linkPartnerName);
    }

    @Override
    public Optional<DomibusConnectorTransportStep> getTransportStepById(TransportId transportId) {
        return transportStepPersistenceService.findStepById(transportId);
    }

    private interface SuccessHandler {
        void success(DomibusConnectorMessage message);
    }
}
