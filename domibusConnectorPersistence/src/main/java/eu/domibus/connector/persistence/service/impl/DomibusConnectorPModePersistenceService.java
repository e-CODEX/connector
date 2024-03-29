package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.persistence.dao.DomibusConnectorKeystoreDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorPModeSetDao;
import eu.domibus.connector.persistence.model.*;
import eu.domibus.connector.persistence.service.DomibusConnectorPModeService;
import eu.domibus.connector.persistence.service.exceptions.IncorrectResultSizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class DomibusConnectorPModePersistenceService implements DomibusConnectorPModeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorPModePersistenceService.class);

    private final DomibusConnectorPModeSetDao domibusConnectorPModeSetDao;
    private final DomibusConnectorBusinessDomainDao messageLaneDao;
    private final DomibusConnectorKeystoreDao keystoreDao;

    public DomibusConnectorPModePersistenceService(
            DomibusConnectorPModeSetDao domibusConnectorPModeSetDao,
            DomibusConnectorBusinessDomainDao messageLaneDao,
            DomibusConnectorKeystoreDao keystoreDao) {
        this.domibusConnectorPModeSetDao = domibusConnectorPModeSetDao;
        this.messageLaneDao = messageLaneDao;
        this.keystoreDao = keystoreDao;
    }

    @Override
    public List<DomibusConnectorAction> findByExample(
            DomibusConnectorBusinessDomain.BusinessDomainId lane, DomibusConnectorAction searchAction) {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (!currentPModeSetOptional.isPresent()) {
            return new ArrayList<>();
        }
        List<DomibusConnectorAction> foundActions =
                findByExample(ActionMapper.mapActionToPersistence(searchAction), currentPModeSetOptional)
                        .map(ActionMapper::mapActionToDomain).collect(Collectors.toList());
        return foundActions;
    }

    @Override
    public Optional<DomibusConnectorAction> getConfiguredSingle(
            DomibusConnectorBusinessDomain.BusinessDomainId lane, DomibusConnectorAction searchAction) {
        return getConfiguredSingleDB(
                lane,
                ActionMapper.mapActionToPersistence(searchAction)
        ).map(ActionMapper::mapActionToDomain);
    }

    @Override
    public List<DomibusConnectorService> findByExample(
            DomibusConnectorBusinessDomain.BusinessDomainId lane,
            DomibusConnectorService searchService) {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (!currentPModeSetOptional.isPresent()) {
            return new ArrayList<>();
        }
        List<DomibusConnectorService> foundServices =
                findByExampleStream(ServiceMapper.mapServiceToPersistence(searchService), currentPModeSetOptional)
                        .map(ServiceMapper::mapServiceToDomain).collect(Collectors.toList());
        return foundServices;
    }

    @Override
    public Optional<DomibusConnectorService> getConfiguredSingle(
            DomibusConnectorBusinessDomain.BusinessDomainId lane, DomibusConnectorService searchService) {
        return getConfiguredSingleDB(
                lane,
                ServiceMapper.mapServiceToPersistence(searchService)
        ).map(ServiceMapper::mapServiceToDomain);
    }

    @Override
    public List<DomibusConnectorParty> findByExample(
            DomibusConnectorBusinessDomain.BusinessDomainId lane,
            DomibusConnectorParty exampleParty) throws IncorrectResultSizeException {
        PDomibusConnectorParty searchParty = PartyMapper.mapPartyToPersistence(exampleParty);
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (!currentPModeSetOptional.isPresent()) {
            return new ArrayList<>();
        }
        Stream<PDomibusConnectorParty> stream = findByExampleStream(searchParty, currentPModeSetOptional);
        List<DomibusConnectorParty> foundParties =
                stream.map(PartyMapper::mapPartyToDomain).collect(Collectors.toList());

        return foundParties;
    }

    @Override
    public Optional<DomibusConnectorParty> getConfiguredSingle(
            DomibusConnectorBusinessDomain.BusinessDomainId lane,
            DomibusConnectorParty searchParty) throws IncorrectResultSizeException {
        return getConfiguredSingleDB(
                lane,
                PartyMapper.mapPartyToPersistence(searchParty)
        ).map(PartyMapper::mapPartyToDomain);
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
            throw new IllegalArgumentException("MessageLaneId is not allowed to be null!");
        }
        if (connectorPModeSet.getConnectorstore() == null) {
            throw new IllegalArgumentException("connectorStoreUUID is not allowed to be null!");
        }

        connectorPModeSet.getParties().forEach(p -> p.setDbKey(null));
        connectorPModeSet.getActions().forEach(a -> a.setDbKey(null));
        connectorPModeSet.getServices().forEach(s -> s.setDbKey(null));

        Optional<PDomibusConnectorMessageLane> messageLaneOptional = messageLaneDao.findByName(lane);
        PDomibusConnectorMessageLane pDomibusConnectorMessageLane =
                messageLaneOptional.orElseThrow(() -> new RuntimeException(String.format(
                        "No message lane found with name [%s]",
                        lane
                )));

        PDomibusConnectorPModeSet dbPmodeSet = new PDomibusConnectorPModeSet();
        dbPmodeSet.setDescription(connectorPModeSet.getDescription());
        dbPmodeSet.setCreated(Timestamp.from(Instant.now()));

        dbPmodeSet.setPmodes(connectorPModeSet.getpModes());

        dbPmodeSet.setMessageLane(pDomibusConnectorMessageLane);
        dbPmodeSet.setActions(mapActionListToDb(connectorPModeSet.getActions()));
        dbPmodeSet.setServices(mapServiceListToDb(connectorPModeSet.getServices()));
        dbPmodeSet.setParties(mapPartiesListToDb(connectorPModeSet.getParties()));
        dbPmodeSet.setActive(true);

        if (connectorPModeSet.getConnectorstore() == null) {
            throw new IllegalArgumentException("You must provide a already persisted keystore!");
        }
        Optional<PDomibusConnectorKeystore> connectorstore =
                keystoreDao.findByUuid(connectorPModeSet.getConnectorstore().getUuid());
        if (!connectorstore.isPresent()) {
            String error =
                    String.format("There is no JavaKeyStore with id [%s]", connectorPModeSet.getConnectorstore());
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
    @Cacheable
    @Transactional(readOnly = true)
    public Optional<DomibusConnectorPModeSet> getCurrentPModeSet(DomibusConnectorBusinessDomain.BusinessDomainId lane) {
        return getCurrentDBPModeSet(lane).map(this::mapToDomain);
    }

    @Override
    @Transactional
    public void updateActivePModeSetDescription(DomibusConnectorPModeSet connectorPModeSet) {
        DomibusConnectorBusinessDomain.BusinessDomainId lane = connectorPModeSet.getMessageLaneId();
        if (lane == null) {
            throw new IllegalArgumentException("MessageLaneId is not allowed to be null!");
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

    @Override
    @Cacheable
    public List<DomibusConnectorPModeSet> getInactivePModeSets(DomibusConnectorBusinessDomain.BusinessDomainId lane) {
        if (lane == null) {
            throw new IllegalArgumentException("MessageLaneId is not allowed to be null!");
        }
        List<DomibusConnectorPModeSet> result = new ArrayList<DomibusConnectorPModeSet>();
        List<PDomibusConnectorPModeSet> inactivePModeSets = domibusConnectorPModeSetDao.getInactivePModeSets(lane);
        inactivePModeSets.forEach(s -> result.add(mapToDomain(s)));

        return result;
    }

    public Optional<PDomibusConnectorAction> getConfiguredSingleDB(
            DomibusConnectorBusinessDomain.BusinessDomainId lane, PDomibusConnectorAction searchAction) {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (!currentPModeSetOptional.isPresent()) {
            return Optional.empty();
        }
        List<PDomibusConnectorAction> foundActions =
                findByExample(searchAction, currentPModeSetOptional).collect(Collectors.toList());
        if (foundActions.size() > 1) {
            throw new IncorrectResultSizeException(String.format(
                    "Found %d Actions which match Action [%s] in MessageLane [%s]",
                    foundActions.size(),
                    searchAction,
                    lane
            ));
        }
        if (foundActions.isEmpty()) {
            LOGGER.debug("Found no Actions which match Action [{}] in MessageLane [{}]", searchAction, lane);
            return Optional.empty();
        }
        return Optional.of(foundActions.get(0));
    }

    private Stream<PDomibusConnectorAction> findByExample(
            PDomibusConnectorAction searchAction, Optional<PDomibusConnectorPModeSet> currentPModeSetOptional) {
        return currentPModeSetOptional.get().getActions().stream().filter(action -> {
            boolean result = true;
            if (result && searchAction.getAction() != null) {
                result = result && searchAction.getAction().equals(action.getAction());
            }
            return result;
        });
    }

    public Optional<PDomibusConnectorService> getConfiguredSingleDB(
            DomibusConnectorBusinessDomain.BusinessDomainId lane, PDomibusConnectorService searchService) {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (!currentPModeSetOptional.isPresent()) {
            return Optional.empty();
        }
        List<PDomibusConnectorService> foundServices =
                findByExampleStream(searchService, currentPModeSetOptional).collect(Collectors.toList());
        if (foundServices.size() > 1) {
            throw new IncorrectResultSizeException(String.format(
                    "Found %d Services which match Service [%s] in MessageLane [%s]",
                    foundServices.size(),
                    searchService,
                    lane
            ));
        }
        if (foundServices.isEmpty()) {
            LOGGER.debug("Found no Services which match Service [{}] in MessageLane [{}]", searchService, lane);
            return Optional.empty();
        }
        return Optional.of(foundServices.get(0));
    }

    private Stream<PDomibusConnectorService> findByExampleStream(
            PDomibusConnectorService searchService, Optional<PDomibusConnectorPModeSet> currentPModeSetOptional) {
        return currentPModeSetOptional.get().getServices().stream().filter(service -> {
            boolean result = true;
            if (result && searchService.getService() != null) {
                result = result && searchService.getService().equals(service.getService());
            }
            if (result && searchService.getServiceType() != null) { // check for null a uri can be a empty
                // string
                result = result && searchService.getServiceType().equals(service.getServiceType());
            }
            return result;
        });
    }

    public Optional<PDomibusConnectorParty> getConfiguredSingleDB(
            DomibusConnectorBusinessDomain.BusinessDomainId lane,
            PDomibusConnectorParty searchParty) throws IncorrectResultSizeException {
        Optional<PDomibusConnectorPModeSet> currentPModeSetOptional = getCurrentDBPModeSet(lane);
        if (!currentPModeSetOptional.isPresent()) {
            return Optional.empty();
        }
        List<PDomibusConnectorParty> foundParties =
                findByExampleStream(searchParty, currentPModeSetOptional).collect(Collectors.toList());

        if (foundParties.size() > 1) {
            throw new IncorrectResultSizeException(String.format(
                    "Found %d Parties which match Party [%s] in MessageLane [%s]",
                    foundParties.size(),
                    searchParty,
                    lane
            ));
        }
        if (foundParties.isEmpty()) {
            LOGGER.debug("Found no Parties which match Party [{}] in MessageLane [{}]", searchParty, lane);
            return Optional.empty();
        }
        return Optional.of(foundParties.get(0));
    }

    private Stream<PDomibusConnectorParty> findByExampleStream(
            PDomibusConnectorParty searchParty, Optional<PDomibusConnectorPModeSet> currentPModeSetOptional) {
        return currentPModeSetOptional.get().getParties().stream().filter(party -> {
            boolean result = true;
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

    private Set<PDomibusConnectorParty> mapPartiesListToDb(List<DomibusConnectorParty> parties) {
        return parties.stream().map(PartyMapper::mapPartyToPersistence).collect(Collectors.toSet());
    }

    private Set<PDomibusConnectorService> mapServiceListToDb(List<DomibusConnectorService> services) {
        return services.stream().map(ServiceMapper::mapServiceToPersistence).collect(Collectors.toSet());
    }

    private Set<PDomibusConnectorAction> mapActionListToDb(List<DomibusConnectorAction> actions) {
        return actions.stream().map(ActionMapper::mapActionToPersistence).collect(Collectors.toSet());
    }

    public Optional<PDomibusConnectorPModeSet> getCurrentDBPModeSet(DomibusConnectorBusinessDomain.BusinessDomainId lane) {
        List<PDomibusConnectorPModeSet> currentActivePModeSet =
                domibusConnectorPModeSetDao.getCurrentActivePModeSet(lane);
        if (currentActivePModeSet.isEmpty()) {
            LOGGER.debug("getCurrentDBPModeSet# no active pMode Set found for message lane [{}]", lane);
        }
        return currentActivePModeSet.stream().findFirst();
    }

    public DomibusConnectorPModeSet mapToDomain(PDomibusConnectorPModeSet dbPmodes) {
        DomibusConnectorPModeSet pModeSet = new DomibusConnectorPModeSet();
        pModeSet.setCreateDate(dbPmodes.getCreated());
        pModeSet.setDescription(dbPmodes.getDescription());
        pModeSet.setMessageLaneId(dbPmodes.getMessageLane().getName());
        pModeSet.setpModes(dbPmodes.getPmodes());

        pModeSet.setConnectorstore(KeystoreMapper.mapKeystoreToDomain(dbPmodes.getConnectorstore()));

        pModeSet.setParties(dbPmodes.getParties().stream().map(PartyMapper::mapPartyToDomain)
                                    .collect(Collectors.toList()));
        pModeSet.setActions(dbPmodes.getActions().stream().map(ActionMapper::mapActionToDomain)
                                    .collect(Collectors.toList()));
        pModeSet.setServices(dbPmodes.getServices().stream().map(ServiceMapper::mapServiceToDomain)
                                     .collect(Collectors.toList()));

        return pModeSet;
    }
}
