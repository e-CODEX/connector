/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.domibus.connector.common.annotations.DomainModelJsonObjectMapper;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;
import eu.domibus.connector.persistence.dao.DomibusConnectorTransportStepDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorTransportStep;
import eu.domibus.connector.persistence.model.PDomibusConnectorTransportStepStatusUpdate;
import eu.domibus.connector.persistence.service.TransportStepPersistenceService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the TransportStepPersistenceService interface. Provides methods for creating,
 * updating, and retrieving Transport Steps.
 */
@Service
@Transactional
public class TransportStepPersistenceServiceImpl implements TransportStepPersistenceService {
    private static final Logger LOGGER =
        LogManager.getLogger(TransportStepPersistenceServiceImpl.class);
    private final DomibusConnectorTransportStepDao transportStepDao;
    private final ObjectMapper objectMapper;

    public TransportStepPersistenceServiceImpl(
        DomibusConnectorTransportStepDao transportStepDao,
        @DomainModelJsonObjectMapper ObjectMapper objectMapper) {
        this.transportStepDao = transportStepDao;
        this.objectMapper = objectMapper;
    }

    @Override
    public DomibusConnectorTransportStep createNewTransportStep(
        DomibusConnectorTransportStep transportStep) {
        if (transportStep.getLinkPartnerName() == null || StringUtils.isEmpty(
            transportStep.getLinkPartnerName().toString())) {
            throw new IllegalArgumentException("LinkPartner name must be set!");
        }
        if (transportStep.getTransportedMessage().map(msg -> msg.getConnectorMessageId() == null)
                         .orElse(true)) {
            throw new IllegalArgumentException(
                "TransportedMessage and ConnectorMessageId of message must be set!");
        }
        if (transportStep.getConnectorMessageId() == null) {
            throw new IllegalArgumentException(
                "The connectorMessageId on DomibusConnectorTransportStep is not allowed to be null!"
            );
        }

        var msgId =
            transportStep.getTransportedMessage().get().getConnectorMessageId().toString();
        var linkPartnerName = transportStep.getLinkPartnerName();

        Optional<Integer> highestAttemptBy =
            transportStepDao.getHighestAttemptBy(msgId, linkPartnerName);
        int nextAttempt = highestAttemptBy.orElse(0) + 1;
        transportStep.setAttempt(nextAttempt);

        transportStep.setTransportId(new TransportStateService.TransportId(
            msgId + "_" + linkPartnerName + "_" + nextAttempt));

        PDomibusConnectorTransportStep dbStep = mapTransportStepToDb(transportStep);
        PDomibusConnectorTransportStep savedDbStep = transportStepDao.save(dbStep);

        return transportStep;
    }

    @Override
    public DomibusConnectorTransportStep getTransportStepByTransportId(
        TransportStateService.TransportId transportId) {
        Optional<PDomibusConnectorTransportStep> foundTransport =
            transportStepDao.findByTransportId(transportId);
        if (foundTransport.isPresent()) {
            return mapTransportStepToDomain(foundTransport.get());
        } else {
            throw new RuntimeException(
                java.lang.String.format("No Transport found with transport id [%s]", transportId));
        }
    }

    @Override
    public DomibusConnectorTransportStep update(DomibusConnectorTransportStep transportStep) {
        PDomibusConnectorTransportStep dbTransportStep = mapTransportStepToDb(transportStep);
        dbTransportStep = transportStepDao.save(dbTransportStep);
        transportStep = mapTransportStepToDomain(dbTransportStep);
        return transportStep;
    }

    @Override
    public List<DomibusConnectorTransportStep> findPendingStepBy(
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {

        var states = new String[] {TransportState.PENDING.getDbName()};
        var linkNames = new DomibusConnectorLinkPartner.LinkPartnerName[] {linkPartnerName};
        var p = Pageable.unpaged();
        return transportStepDao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                                   states, linkNames, p).stream()
                               .map(this::mapTransportStepToDomain)
                               .toList();
    }

    @Override
    public Optional<DomibusConnectorTransportStep> findStepById(
        TransportStateService.TransportId transportId) {
        Optional<PDomibusConnectorTransportStep> byTransportId =
            transportStepDao.findByTransportId(transportId);
        return byTransportId.map(this::mapTransportStepToDomain);
    }

    @Override
    public Page<DomibusConnectorTransportStep> findLastAttemptStepByLastStateIsOneOf(
        Set<TransportState> states,
        Set<DomibusConnectorLinkPartner.LinkPartnerName> linkPartnerNames,
        Pageable pageable) {

        String[] stateStrings =
            states.stream().map(TransportState::getDbName).toArray(String[]::new);

        DomibusConnectorLinkPartner.LinkPartnerName[] linkPartnerArray;

        if (linkPartnerNames.isEmpty()) {
            linkPartnerArray = transportStepDao.findAllLinkPartnerNames().toArray(
                DomibusConnectorLinkPartner.LinkPartnerName[]::new);
        } else {
            linkPartnerArray =
                linkPartnerNames.toArray(DomibusConnectorLinkPartner.LinkPartnerName[]::new);
        }

        // The query needs non-empty parameters to work. Null or () won't work, but ("") or
        // (null) do.
        Page<PDomibusConnectorTransportStep> stepByLastState =
            transportStepDao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                stateStrings,
                linkPartnerArray,
                pageable
            );
        return stepByLastState.map(this::mapTransportStepToDomain);
    }

    @Override
    public List<DomibusConnectorTransportStep> findStepByConnectorMessageId(
        DomibusConnectorMessageId messageId) {
        return transportStepDao.findByConnectorMessageId(messageId.getConnectorMessageId())
                               .stream()
                               .map(this::mapTransportStepToDomain)
                               .toList();
    }

    @Override
    public List<DomibusConnectorLinkPartner.LinkPartnerName> findAllLinkPartners() {
        return transportStepDao.findAllLinkPartnerNames();
    }

    DomibusConnectorTransportStep mapTransportStepToDomain(
        PDomibusConnectorTransportStep dbTransportStep) {

        var step = new DomibusConnectorTransportStep();

        /*
            read message from db as json
            the message can be null if the format is not compatible to current connector version
            (old data)
         */
        if (dbTransportStep.getTransportedMessage() != null) {
            try (JsonParser parser = objectMapper.createParser(
                dbTransportStep.getTransportedMessage())) {
                DomibusConnectorMessage domibusConnectorMessage =
                    parser.readValueAs(DomibusConnectorMessage.class);
                if (domibusConnectorMessage.getConnectorMessageId() != null) {
                    step.setTransportedMessage(domibusConnectorMessage);
                }
            } catch (IOException e) {
                LOGGER.warn(
                    "Exception occured while reading domibus connector message from "
                        + "transport step table. Maybe the message is a older format.",
                    e
                );
            }
        } else {
            LOGGER.warn("The transported message was null for step {}", dbTransportStep);
        }

        step.setLinkPartnerName(dbTransportStep.getLinkPartnerName());
        step.setTransportId(dbTransportStep.getTransportId());
        step.setAttempt(dbTransportStep.getAttempt());
        step.setCreated(dbTransportStep.getCreated());
        step.setRemoteMessageId(dbTransportStep.getRemoteMessageId());
        step.setFinalStateReached(dbTransportStep.getFinalStateReached());
        step.setTransportSystemMessageId(dbTransportStep.getTransportSystemMessageId());
        step.setConnectorMessageId(
            new DomibusConnectorMessageId(dbTransportStep.getConnectorMessageId()));

        List<DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate>
            statusUpdates = dbTransportStep
            .getStatusUpdates()
            .stream()
            .map(this::mapTransportStepState)
            .toList();

        step.setStatusUpdates(statusUpdates);

        return step;
    }

    private DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate
    mapTransportStepState(
        PDomibusConnectorTransportStepStatusUpdate dbTransportUpdate) {
        var update = new DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate();
        update.setCreated(dbTransportUpdate.getCreated());
        update.setText(dbTransportUpdate.getText());
        update.setTransportState(dbTransportUpdate.getTransportState());
        return update;
    }

    private PDomibusConnectorTransportStep mapTransportStepToDb(
        DomibusConnectorTransportStep transportStep) {
        if (transportStep.getConnectorMessageId() == null) {
            throw new IllegalArgumentException(
                "The provided connector message id is not allowed to be null!");
        }

        var connectorMessageId = transportStep.getConnectorMessageId().toString();
        var partnerName = transportStep.getLinkPartnerName();

        Optional<PDomibusConnectorTransportStep> foundStep =
            transportStepDao.findbyMsgLinkPartnerAndAttempt(connectorMessageId, partnerName,
                                                            transportStep.getAttempt()
            );
        PDomibusConnectorTransportStep dbStep = foundStep.orElseGet(() -> {
            var s = new PDomibusConnectorTransportStep();
            s.setConnectorMessageId(connectorMessageId);
            s.setLinkPartnerName(partnerName);
            s.setTransportId(transportStep.getTransportId());
            return s;
        });

        transportStep.getTransportedMessage().ifPresent(msg -> {

            try {
                dbStep.setTransportedMessage(objectMapper.writeValueAsString(msg));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        dbStep.setFinalStateReached(transportStep.getFinalStateReached());
        dbStep.setAttempt(transportStep.getAttempt());
        dbStep.setRemoteMessageId(transportStep.getRemoteMessageId());
        dbStep.setTransportSystemMessageId(transportStep.getTransportSystemMessageId());

        Set<TransportState> updates = dbStep
            .getStatusUpdates()
            .stream()
            .map(
                PDomibusConnectorTransportStepStatusUpdate::getTransportState)
            .collect(Collectors.toSet());

        List<PDomibusConnectorTransportStepStatusUpdate> newStatus =
            transportStep.getStatusUpdates()
                         .stream()
                         // if db not already contains update add it
                         .filter(u -> !updates.contains(u.getTransportState()))
                         .map(u -> {
                             var update = new PDomibusConnectorTransportStepStatusUpdate();
                             update.setCreated(u.getCreated());
                             update.setTransportState(u.getTransportState());
                             update.setText(u.getText());
                             update.setTransportStep(dbStep);
                             return update;
                         }).toList();

        dbStep.getStatusUpdates().addAll(newStatus);

        return dbStep;
    }
}
