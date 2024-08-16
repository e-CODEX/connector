/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorPModeSet;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.persistence.dao.DomibusConnectorKeystoreDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorPModeSetDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageLane;
import eu.domibus.connector.persistence.model.PDomibusConnectorPModeSet;
import eu.domibus.connector.persistence.model.PDomibusConnectorParty;
import eu.domibus.connector.persistence.model.PDomibusConnectorService;
import eu.domibus.connector.persistence.service.DomibusConnectorPModeService;
import eu.domibus.connector.persistence.service.exceptions.IncorrectResultSizeException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides persistence services for managing the PMode configuration of a Domibus
 * connector.
 */
@Service
public class DomibusConnectorPModePersistenceService implements DomibusConnectorPModeService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorPModePersistenceService.class);
    public static final String MESSAGE_LANE_ID_IS_NOT_ALLOWED_TO_BE_NULL =
        "MessageLaneId is not allowed to be null!";
    private final DomibusConnectorPModeSetDao domibusConnectorPModeSetDao;
    private final DomibusConnectorBusinessDomainDao messageLaneDao;
    private final DomibusConnectorKeystoreDao keystoreDao;

    /**
     * The DomibusConnectorPModePersistenceService class is responsible for managing the persistence
     * of Domibus Connector PMode Sets. It provides methods to interact with the underlying database
     * and perform CRUD operations.
     */
    public DomibusConnectorPModePersistenceService(
        DomibusConnectorPModeSetDao domibusConnectorPModeSetDao,
        DomibusConnectorBusinessDomainDao messageLaneDao,
        DomibusConnectorKeystoreDao keystoreDao) {
        this.domibusConnectorPModeSetDao = domibusConnectorPModeSetDao;
        this.messageLaneDao = messageLaneDao;
        this.keystoreDao = keystoreDao;
    }

    @Override
    public Optional<DomibusConnectorService> getConfiguredSingle(
        DomibusConnectorBusinessDomain.BusinessDomainId lane,
        DomibusConnectorService searchService) {
        return getConfiguredSingleDB(lane, ServiceMapper.mapServiceToPersistence(searchService))
            .map(ServiceMapper::mapServiceToDomain);
    }

    @Override
    public Optional<DomibusConnectorAction> getConfiguredSingle(
        DomibusConnectorBusinessDomain.BusinessDomainId lane, DomibusConnectorAction searchAction) {
        return getConfiguredSingleDB(lane, ActionMapper.mapActionToPersistence(searchAction))
            .map(ActionMapper::mapActionToDomain);
    }

    @Override
    public Optional<DomibusConnectorParty> getConfiguredSingle(
        DomibusConnectorBusinessDomain.BusinessDomainId lane, DomibusConnectorParty searchParty)
        throws IncorrectResultSizeException {
        return getConfiguredSingleDB(lane, PartyMapper.mapPartyToPersistence(searchParty))
            .map(PartyMapper::mapPartyToDomain);
    }

    @Override
    public List<DomibusConnectorAction> findByExample(
        DomibusConnectorBusinessDomain.BusinessDomainId lane, DomibusConnectorAction searchAction) {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (currentPModeSetOptional.isEmpty()) {
            return new ArrayList<>();
        }
        return findByExample(
            ActionMapper.mapActionToPersistence(searchAction),
            currentPModeSetOptional
        )
            .map(ActionMapper::mapActionToDomain)
            .toList();
    }

    @Override
    public List<DomibusConnectorParty> findByExample(
        DomibusConnectorBusinessDomain.BusinessDomainId lane, DomibusConnectorParty exampleParty)
        throws IncorrectResultSizeException {
        PDomibusConnectorParty searchParty = PartyMapper.mapPartyToPersistence(exampleParty);
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (currentPModeSetOptional.isEmpty()) {
            return new ArrayList<>();
        }
        Stream<PDomibusConnectorParty> stream =
            findByExampleStream(searchParty, currentPModeSetOptional);

        return stream.map(PartyMapper::mapPartyToDomain)
                     .toList();
    }

    @Override
    public List<DomibusConnectorService> findByExample(
        DomibusConnectorBusinessDomain.BusinessDomainId lane,
        DomibusConnectorService searchService) {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (currentPModeSetOptional.isEmpty()) {
            return new ArrayList<>();
        }
        return findByExampleStream(
            ServiceMapper.mapServiceToPersistence(searchService),
            currentPModeSetOptional
        )
            .map(ServiceMapper::mapServiceToDomain)
            .toList();
    }

    private Stream<PDomibusConnectorAction> findByExample(
        PDomibusConnectorAction searchAction,
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional) {
        return currentPModeSetOptional
            .get()
            .getActions()
            .stream()
            .filter(action -> {
                var result = true;
                if (result && searchAction.getAction() != null) {
                    result = result && searchAction.getAction().equals(action.getAction());
                }
                return result;
            });
    }

    /**
     * Returns the configured single PDomibusConnectorAction for the given lane and search action.
     * If no matching action is found, an empty Optional is returned. If multiple actions are found,
     * an IncorrectResultSizeException is thrown.
     *
     * @param lane         The business domain lane ID.
     * @param searchAction The PDomibusConnectorAction to search for.
     * @return Optional PDomibusConnectorAction The configured single PDomibusConnectorAction, if
     *      found.
     * @throws IncorrectResultSizeException If multiple actions are found.
     */
    public Optional<PDomibusConnectorAction> getConfiguredSingleDB(
        DomibusConnectorBusinessDomain.BusinessDomainId lane,
        PDomibusConnectorAction searchAction) {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (currentPModeSetOptional.isEmpty()) {
            return Optional.empty();
        }
        List<PDomibusConnectorAction> foundActions =
            findByExample(searchAction, currentPModeSetOptional)
                .toList();
        if (foundActions.size() > 1) {
            throw new IncorrectResultSizeException(
                String.format("Found %d Actions which match Action [%s] in MessageLane [%s]",
                              foundActions.size(), searchAction, lane
                ));
        }
        if (foundActions.isEmpty()) {
            LOGGER.debug(
                "Found no Actions which match Action [{}] in MessageLane [{}]", searchAction, lane);
            return Optional.empty();
        }
        return Optional.of(foundActions.getFirst());
    }

    /**
     * Returns the configured single PDomibusConnectorService for the given lane and search service.
     * If no matching service is found, an empty Optional is returned. If multiple services are
     * found, an IncorrectResultSizeException is thrown.
     *
     * @param lane          The business domain lane ID.
     * @param searchService The PDomibusConnectorService to search for.
     * @return Optional PDomibusConnectorService The configured single PDomibusConnectorService, if
     *      found.
     * @throws IncorrectResultSizeException If multiple services are found.
     */
    public Optional<PDomibusConnectorService> getConfiguredSingleDB(
        DomibusConnectorBusinessDomain.BusinessDomainId lane,
        PDomibusConnectorService searchService) {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (currentPModeSetOptional.isEmpty()) {
            return Optional.empty();
        }
        List<PDomibusConnectorService> foundServices =
            findByExampleStream(searchService, currentPModeSetOptional)
                .toList();
        if (foundServices.size() > 1) {
            throw new IncorrectResultSizeException(
                String.format("Found %d Services which match Service [%s] in MessageLane [%s]",
                              foundServices.size(), searchService, lane
                ));
        }
        if (foundServices.isEmpty()) {
            LOGGER.debug(
                "Found no Services which match Service [{}] in MessageLane [{}]", searchService,
                lane
            );
            return Optional.empty();
        }
        return Optional.of(foundServices.getFirst());
    }

    /**
     * Returns the configured single PDomibusConnectorParty for the given lane and search party. If
     * no matching party is found, an empty Optional is returned. If multiple parties are found, an
     * IncorrectResultSizeException is thrown.
     *
     * @param lane        The business domain lane ID.
     * @param searchParty The PDomibusConnectorParty to search for.
     * @return Optional PDomibusConnectorParty The configured single PDomibusConnectorParty, if
     *      found.
     * @throws IncorrectResultSizeException If multiple parties are found.
     */
    public Optional<PDomibusConnectorParty> getConfiguredSingleDB(
        DomibusConnectorBusinessDomain.BusinessDomainId lane, PDomibusConnectorParty searchParty)
        throws IncorrectResultSizeException {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (currentPModeSetOptional.isEmpty()) {
            return Optional.empty();
        }
        List<PDomibusConnectorParty> foundParties =
            findByExampleStream(searchParty, currentPModeSetOptional).toList();

        if (foundParties.size() > 1) {
            throw new IncorrectResultSizeException(
                String.format("Found %d Parties which match Party [%s] in MessageLane [%s]",
                              foundParties.size(), searchParty, lane
                ));
        }
        if (foundParties.isEmpty()) {
            LOGGER.debug(
                "Found no Parties which match Party [{}] in MessageLane [{}]", searchParty, lane);
            return Optional.empty();
        }
        return Optional.of(foundParties.getFirst());
    }

    private Stream<PDomibusConnectorService> findByExampleStream(
        PDomibusConnectorService searchService,
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional) {
        return currentPModeSetOptional
            .get()
            .getServices()
            .stream()
            .filter(service -> {
                var result = true;
                if (result && searchService.getService() != null) {
                    result = result && searchService.getService().equals(service.getService());
                }
                if (result && searchService.getServiceType()
                    != null) { // check for null an uri can be an empty string
                    result =
                        result && searchService.getServiceType().equals(service.getServiceType());
                }
                return result;
            });
    }

    private Stream<PDomibusConnectorParty> findByExampleStream(
        PDomibusConnectorParty searchParty,
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional) {
        return currentPModeSetOptional
            .get()
            .getParties()
            .stream()
            .filter(party -> {
                var result = true;
                if (result && searchParty.getPartyId() != null) {
                    result = result && searchParty.getPartyId().equals(party.getPartyId());
                }
                if (result && searchParty.getRole() != null) {
                    result = result && searchParty.getRole().equals(party.getRole());
                }
                if (result && searchParty.getRoleType() != null) {
                    result = result && searchParty.getRoleType().equals(party.getRoleType());
                }
                if (result && searchParty.getPartyIdType() != null) {
                    result = result && searchParty.getPartyIdType().equals(party.getPartyIdType());
                }
                return result;
            });
    }

    @Override
    @Cacheable
    public List<DomibusConnectorPModeSet> getInactivePModeSets(
        DomibusConnectorBusinessDomain.BusinessDomainId lane) {
        if (lane == null) {
            throw new IllegalArgumentException(MESSAGE_LANE_ID_IS_NOT_ALLOWED_TO_BE_NULL);
        }

        List<DomibusConnectorPModeSet> result = new ArrayList<>();

        List<PDomibusConnectorPModeSet> inactivePModeSets =
            domibusConnectorPModeSetDao.getInactivePModeSets(lane);

        inactivePModeSets.forEach(s -> result.add(mapToDomain(s)));

        return result;
    }

    @Override
    @CacheEvict
    @Transactional
    public void updatePModeConfigurationSet(DomibusConnectorPModeSet connectorPModeSet) {
        if (connectorPModeSet == null) {
            throw new IllegalArgumentException("connectorPMode Set is not allowed to be null!");
        }
        DomibusConnectorBusinessDomain.BusinessDomainId lane = connectorPModeSet.getMessageLaneId();
        if (lane == null) {
            throw new IllegalArgumentException(MESSAGE_LANE_ID_IS_NOT_ALLOWED_TO_BE_NULL);
        }
        if (connectorPModeSet.getConnectorstore() == null) {
            throw new IllegalArgumentException("connectorStoreUUID is not allowed to be null!");
        }

        connectorPModeSet.getParties().forEach(p -> p.setDbKey(null));
        connectorPModeSet.getActions().forEach(a -> a.setDbKey(null));
        connectorPModeSet.getServices().forEach(s -> s.setDbKey(null));

        Optional<PDomibusConnectorMessageLane> messageLaneOptional =
            messageLaneDao.findByName(lane);
        PDomibusConnectorMessageLane connectorMessageLane = messageLaneOptional.orElseThrow(
            () -> new RuntimeException(
                String.format("No message lane found with name [%s]", lane))
        );

        var dbPmodeSet = new PDomibusConnectorPModeSet();
        dbPmodeSet.setDescription(connectorPModeSet.getDescription());
        dbPmodeSet.setCreated(Timestamp.from(Instant.now()));

        dbPmodeSet.setPmodes(connectorPModeSet.getpModes());

        dbPmodeSet.setMessageLane(connectorMessageLane);
        dbPmodeSet.setActions(mapActionListToDb(connectorPModeSet.getActions()));
        dbPmodeSet.setServices(mapServiceListToDb(connectorPModeSet.getServices()));
        dbPmodeSet.setParties(mapPartiesListToDb(connectorPModeSet.getParties()));
        dbPmodeSet.setActive(true);

        if (connectorPModeSet.getConnectorstore() == null) {
            throw new IllegalArgumentException("You must provide a already persisted keystore!");
        }
        Optional<PDomibusConnectorKeystore> connectorstore =
            keystoreDao.findByUuid(connectorPModeSet.getConnectorstore().getUuid());
        if (connectorstore.isEmpty()) {
            var error = String.format(
                "There is no JavaKeyStore with id [%s]",
                connectorPModeSet.getConnectorstore()
            );
            throw new IllegalArgumentException(error);
        }

        dbPmodeSet.setConnectorstore(connectorstore.get());

        List<PDomibusConnectorPModeSet> currentActivePModeSet =
            this.domibusConnectorPModeSetDao.getCurrentActivePModeSet(lane);
        currentActivePModeSet.forEach(s -> s.setActive(false));
        this.domibusConnectorPModeSetDao.saveAll(currentActivePModeSet);
        this.domibusConnectorPModeSetDao.save(dbPmodeSet);
    }

    @Override
    @Transactional
    public void updateActivePModeSetDescription(DomibusConnectorPModeSet connectorPModeSet) {
        DomibusConnectorBusinessDomain.BusinessDomainId lane = connectorPModeSet.getMessageLaneId();
        if (lane == null) {
            throw new IllegalArgumentException(MESSAGE_LANE_ID_IS_NOT_ALLOWED_TO_BE_NULL);
        }

        List<PDomibusConnectorPModeSet> currentActivePModeSet =
            this.domibusConnectorPModeSetDao.getCurrentActivePModeSet(lane);

        currentActivePModeSet.forEach(s -> {
            if (s.isActive()) {
                s.setDescription(connectorPModeSet.getDescription());
                this.domibusConnectorPModeSetDao.save(s);
            }
        });
    }

    private Set<PDomibusConnectorParty> mapPartiesListToDb(List<DomibusConnectorParty> parties) {
        return parties.stream()
                      .map(PartyMapper::mapPartyToPersistence)
                      .collect(Collectors.toSet());
    }

    private Set<PDomibusConnectorService> mapServiceListToDb(
        List<DomibusConnectorService> services) {
        return services.stream()
                       .map(ServiceMapper::mapServiceToPersistence)
                       .collect(Collectors.toSet());
    }

    private Set<PDomibusConnectorAction> mapActionListToDb(List<DomibusConnectorAction> actions) {
        return actions.stream()
                      .map(ActionMapper::mapActionToPersistence)
                      .collect(Collectors.toSet());
    }

    @Override
    @Cacheable
    @Transactional(readOnly = true)
    public Optional<DomibusConnectorPModeSet> getCurrentPModeSet(
        DomibusConnectorBusinessDomain.BusinessDomainId lane) {
        return getCurrentDBPModeSet(lane).map(this::mapToDomain);
    }

    /**
     * Returns the current active PDomibusConnectorPModeSet for the given lane.
     *
     * @param lane The business domain lane ID.
     * @return Optional PDomibusConnectorPModeSet  The current active PDomibusConnectorPModeSet,
     *      if found.
     */
    public Optional<PDomibusConnectorPModeSet> getCurrentDBPModeSet(
        DomibusConnectorBusinessDomain.BusinessDomainId lane) {
        List<PDomibusConnectorPModeSet> currentActivePModeSet =
            domibusConnectorPModeSetDao.getCurrentActivePModeSet(lane);
        if (currentActivePModeSet.isEmpty()) {
            LOGGER.debug(
                "getCurrentDBPModeSet# no active pMode Set found for message lane [{}]", lane);
        }
        return currentActivePModeSet
            .stream()
            .findFirst();
    }

    /**
     * Maps the given PDomibusConnectorPModeSet object to a DomibusConnectorPModeSet object.
     *
     * @param dbPmodes The PDomibusConnectorPModeSet object to be mapped.
     * @return A mapped DomibusConnectorPModeSet object.
     */
    public DomibusConnectorPModeSet mapToDomain(PDomibusConnectorPModeSet dbPmodes) {
        var connectorPModeSet = new DomibusConnectorPModeSet();
        connectorPModeSet.setCreateDate(dbPmodes.getCreated());
        connectorPModeSet.setDescription(dbPmodes.getDescription());
        connectorPModeSet.setMessageLaneId(dbPmodes.getMessageLane().getName());
        connectorPModeSet.setpModes(dbPmodes.getPmodes());

        connectorPModeSet.setConnectorstore(
            KeystoreMapper.mapKeystoreToDomain(dbPmodes.getConnectorstore()));

        connectorPModeSet.setParties(
            dbPmodes.getParties()
                    .stream()
                    .map(PartyMapper::mapPartyToDomain)
                    .toList()
        );
        connectorPModeSet.setActions(
            dbPmodes.getActions()
                    .stream()
                    .map(ActionMapper::mapActionToDomain)
                    .toList()
        );
        connectorPModeSet.setServices(
            dbPmodes.getServices()
                    .stream()
                    .map(ServiceMapper::mapServiceToDomain)
                    .toList()
        );

        return connectorPModeSet;
    }
}
