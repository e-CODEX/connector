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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class TransportStepPersistenceServiceImpl implements TransportStepPersistenceService {
    private static final Logger LOGGER = LogManager.getLogger(TransportStepPersistenceServiceImpl.class);

    private final DomibusConnectorTransportStepDao transportStepDao;
    private final ObjectMapper objectMapper;

    public TransportStepPersistenceServiceImpl(
            DomibusConnectorTransportStepDao transportStepDao,
            @DomainModelJsonObjectMapper ObjectMapper objectMapper) {
        this.transportStepDao = transportStepDao;
        this.objectMapper = objectMapper;
    }

    @Override
    public DomibusConnectorTransportStep createNewTransportStep(DomibusConnectorTransportStep transportStep) {
        if (transportStep.getLinkPartnerName() == null || StringUtils.isEmpty(transportStep.getLinkPartnerName()
                                                                                           .toString())) {
            throw new IllegalArgumentException("LinkPartner name must be set!");
        }
        if (transportStep.getTransportedMessage().map(msg -> msg.getConnectorMessageId() == null).orElse(true)) {
            throw new IllegalArgumentException("TransportedMessage and ConnectorMessageId of message must be set!");
        }
        if (transportStep.getConnectorMessageId() == null) {
            throw new IllegalArgumentException(
                    "The connectorMessageId on DomibusConnectorTransportStep is not allowed to be null!");
        }

        java.lang.String msgId = transportStep.getTransportedMessage().get().getConnectorMessageId().toString();
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName = transportStep.getLinkPartnerName();

        Optional<Integer> nAttempt = transportStepDao.getHighestAttemptBy(msgId, linkPartnerName);
        int nextAttempt = nAttempt.orElse(0) + 1;
        transportStep.setAttempt(nextAttempt);

        transportStep.setTransportId(new TransportStateService.TransportId(msgId + "_" + linkPartnerName + "_" + nextAttempt));

        PDomibusConnectorTransportStep dbStep = mapTransportStepToDb(transportStep);
        PDomibusConnectorTransportStep savedDbStep = transportStepDao.save(dbStep);

        return transportStep;
    }

    @Override
    public DomibusConnectorTransportStep getTransportStepByTransportId(TransportStateService.TransportId transportId) {
        Optional<PDomibusConnectorTransportStep> foundTransport = transportStepDao.findByTransportId(transportId);
        if (foundTransport.isPresent()) {
            return mapTransportStepToDomain(foundTransport.get());
        } else {
            throw new RuntimeException(java.lang.String.format(
                    "No Transport found with transport id [%s]",
                    transportId
            ));
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
    public List<DomibusConnectorTransportStep> findPendingStepBy(DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {

        String[] states = new String[]{TransportState.PENDING.getDbName()};
        DomibusConnectorLinkPartner.LinkPartnerName[] linkNames =
                new DomibusConnectorLinkPartner.LinkPartnerName[]{linkPartnerName};
        Pageable p = Pageable.unpaged();
        return transportStepDao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(states, linkNames, p).stream()
                               .map(this::mapTransportStepToDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<DomibusConnectorTransportStep> findStepById(TransportStateService.TransportId transportId) {
        Optional<PDomibusConnectorTransportStep> byTransportId = transportStepDao.findByTransportId(transportId);
        return byTransportId.map(this::mapTransportStepToDomain);
    }

    @Override
    public Page<DomibusConnectorTransportStep> findLastAttemptStepByLastStateIsOneOf(
            Set<TransportState> states,
            Set<DomibusConnectorLinkPartner.LinkPartnerName> linkPartnerNames,
            Pageable pageable) {

        String[] stateStrings = states.stream().map(TransportState::getDbName).toArray(String[]::new);

        DomibusConnectorLinkPartner.LinkPartnerName[] linkPartnerArray;

        if (linkPartnerNames.size() == 0) {
            linkPartnerArray = transportStepDao.findAllLinkPartnerNames().stream()
                                               .toArray(DomibusConnectorLinkPartner.LinkPartnerName[]::new);
        } else {
            linkPartnerArray = linkPartnerNames.stream().toArray(DomibusConnectorLinkPartner.LinkPartnerName[]::new);
        }

        // The query needs non-empty parameters to work. Null or () won't work, but ("") or (null) do.
        Page<PDomibusConnectorTransportStep> stepByLastState =
                transportStepDao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                        stateStrings,
                        linkPartnerArray,
                        pageable
                );
        return stepByLastState.map(this::mapTransportStepToDomain);
    }

    @Override
    public List<DomibusConnectorTransportStep> findStepByConnectorMessageId(DomibusConnectorMessageId messageId) {
        return transportStepDao
                .findByConnectorMessageId(messageId.getConnectorMessageId()).stream()
                .map(this::mapTransportStepToDomain).collect(Collectors.toList());
    }

    @Override
    public List<DomibusConnectorLinkPartner.LinkPartnerName> findAllLinkPartners() {
        List<DomibusConnectorLinkPartner.LinkPartnerName> allLinkPartnerNames =
                transportStepDao.findAllLinkPartnerNames();
        return allLinkPartnerNames; //.stream().map(DomibusConnectorLinkPartner.LinkPartnerName::new).collect
        // (Collectors.toList());
    }

    DomibusConnectorTransportStep mapTransportStepToDomain(PDomibusConnectorTransportStep dbTransportStep) {
        DomibusConnectorTransportStep step = new DomibusConnectorTransportStep();

        /*
            read message from db as json
            the message can be null if the format is not compatible to current connector version (old data)
         */
        if (dbTransportStep.getTransportedMessage() != null) {
            try (JsonParser parser = objectMapper.createParser(dbTransportStep.getTransportedMessage())) {
                DomibusConnectorMessage domibusConnectorMessage = parser.readValueAs(DomibusConnectorMessage.class);
                if (domibusConnectorMessage.getConnectorMessageId() != null) {
                    step.setTransportedMessage(domibusConnectorMessage);
                }
            } catch (IOException e) {
                LOGGER.warn(
                        "Exception occured while reading domibus connector message from transport step table. Maybe " + "the message is a older format.",
                        e
                );
            }
        } else {
            LOGGER.warn("The transported message was null for step ", dbTransportStep);
        }

        step.setLinkPartnerName(dbTransportStep.getLinkPartnerName());
        step.setTransportId(dbTransportStep.getTransportId());
        step.setAttempt(dbTransportStep.getAttempt());
        step.setCreated(dbTransportStep.getCreated());
        step.setRemoteMessageId(dbTransportStep.getRemoteMessageId());
        step.setFinalStateReached(dbTransportStep.getFinalStateReached());
        step.setTransportSystemMessageId(dbTransportStep.getTransportSystemMessageId());
        step.setConnectorMessageId(new DomibusConnectorMessageId(dbTransportStep.getConnectorMessageId()));

        List<DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate> statusUpdates =
                dbTransportStep.getStatusUpdates().stream().map(this::mapTransportStepState)
                               .collect(Collectors.toList());

        step.setStatusUpdates(statusUpdates);

        return step;
    }

    private DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate mapTransportStepState(
            PDomibusConnectorTransportStepStatusUpdate dbTransportUpdate) {
        DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate update =
                new DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate();
        update.setCreated(dbTransportUpdate.getCreated());
        update.setText(dbTransportUpdate.getText());
        update.setTransportState(dbTransportUpdate.getTransportState());
        return update;
    }

    private PDomibusConnectorTransportStep mapTransportStepToDb(DomibusConnectorTransportStep transportStep) {
        if (transportStep.getConnectorMessageId() == null) {
            throw new IllegalArgumentException("The provided connector message id is not allowed to be null!");
        }

        java.lang.String msgId = transportStep.getConnectorMessageId().toString();
        DomibusConnectorLinkPartner.LinkPartnerName partnerName = transportStep.getLinkPartnerName();

        Optional<PDomibusConnectorTransportStep> foundStep =
                transportStepDao.findbyMsgLinkPartnerAndAttempt(msgId, partnerName, transportStep.getAttempt());
        PDomibusConnectorTransportStep dbStep = foundStep.orElseGet(() -> {
            PDomibusConnectorTransportStep s = new PDomibusConnectorTransportStep();
            s.setConnectorMessageId(msgId);
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

        Set<TransportState> updates =
                dbStep.getStatusUpdates().stream().map(u -> u.getTransportState()).collect(Collectors.toSet());

        List<PDomibusConnectorTransportStepStatusUpdate> newStatus = transportStep
                .getStatusUpdates().stream()
                // if db not alredy contains update
                // add it
                .filter(u -> !updates.contains(u.getTransportState()))
                .map(u -> {
                    PDomibusConnectorTransportStepStatusUpdate
                            update =
                            new PDomibusConnectorTransportStepStatusUpdate();
                    update.setCreated(u.getCreated());
                    update.setTransportState(u.getTransportState());
                    update.setText(u.getText());
                    update.setTransportStep(dbStep);
                    return update;
                }).collect(Collectors.toList());

        dbStep.getStatusUpdates().addAll(newStatus);

        return dbStep;
    }
}
