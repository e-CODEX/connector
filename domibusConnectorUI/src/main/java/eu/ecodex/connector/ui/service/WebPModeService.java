/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.service;

import eu.domibus.configuration.Configuration;
import eu.domibus.configuration.Configuration.BusinessProcesses.Parties.PartyIdTypes.PartyIdType;
import eu.domibus.configuration.Configuration.BusinessProcesses.Roles.Role;
import eu.ecodex.connector.common.service.ConfigurationPropertyManagerService;
import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorKeystore;
import eu.ecodex.connector.domain.model.DomibusConnectorKeystore.KeystoreType;
import eu.ecodex.connector.domain.model.DomibusConnectorPModeSet;
import eu.ecodex.connector.domain.model.DomibusConnectorParty;
import eu.ecodex.connector.domain.model.DomibusConnectorParty.PartyRoleType;
import eu.ecodex.connector.domain.model.DomibusConnectorService;
import eu.ecodex.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import eu.ecodex.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.connector.persistence.service.DomibusConnectorKeystorePersistenceService;
import eu.ecodex.connector.persistence.service.DomibusConnectorPModeService;
import eu.ecodex.connector.persistence.service.DomibusConnectorPropertiesPersistenceService;
import eu.ecodex.connector.persistence.spring.DatabaseResourceLoader;
import eu.ecodex.connector.security.configuration.DCEcodexContainerProperties;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

/**
 * The WebPModeService class provides functionality for managing PMode configuration and related
 * entities in a web application.
 */
@Service
public class WebPModeService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WebPModeService.class);
    private final DomibusConnectorPropertiesPersistenceService propertiesPersistenceService;
    private final DomibusConnectorPModeService connectorPModeService;
    private final DomibusConnectorKeystorePersistenceService keystorePersistenceService;
    private final ConfigurationPropertyManagerService configurationPropertyManagerService;
    private final ApplicationContext ctx;

    /**
     * Constructor.
     *
     * @param propertiesPersistenceService        The service for loading, saving, and resetting
     *                                            connector properties.
     * @param connectorPModeService               The service for managing PMode configuration.
     * @param keystorePersistenceService          The service for managing connector keystores.
     * @param configurationPropertyManagerService The service for managing configuration property
     *                                            manager.
     * @param ctx                                 The application context.
     */
    public WebPModeService(
        DomibusConnectorPropertiesPersistenceService propertiesPersistenceService,
        DomibusConnectorPModeService connectorPModeService,
        DomibusConnectorKeystorePersistenceService keystorePersistenceService,
        ConfigurationPropertyManagerService configurationPropertyManagerService,
        ApplicationContext ctx) {
        this.propertiesPersistenceService = propertiesPersistenceService;
        this.connectorPModeService = connectorPModeService;
        this.keystorePersistenceService = keystorePersistenceService;
        this.configurationPropertyManagerService = configurationPropertyManagerService;
        this.ctx = ctx;
    }

    /**
     * Converts a byte array into an XML object using JAXB.
     *
     * @param xmlAsBytes            the byte array representing the XML content
     * @param instantiationClazz    the class to instantiate from the XML content
     * @param initializationClasses additional classes used for JAXB initialization
     * @return an object representing the XML content
     * @throws Exception if an exception occurs during the conversion process
     */
    public static Object byteArrayToXmlObject(
        final byte[] xmlAsBytes, final Class<?> instantiationClazz,
        final Class<?>... initializationClasses) throws Exception {

        try {
            var bis = new ByteArrayInputStream(xmlAsBytes);
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setNamespaceAware(true);
            var document = factory.newDocumentBuilder().parse(bis);
            var ctx = JAXBContext.newInstance(initializationClasses);
            var unmarshaller = ctx.createUnmarshaller();

            JAXBElement<?> jaxbElement = unmarshaller.unmarshal(document, instantiationClazz);
            return jaxbElement.getValue();
        } catch (JAXBException | SAXException | IOException | ParserConfigurationException e) {
            throw new Exception("Exception parsing byte[] to " + instantiationClazz.getName(), e);
        }
    }

    /**
     * Imports the PMode settings from a byte array into the system.
     *
     * @param contents    the byte array that contains the PMode settings to import
     * @param description the description of the PMode settings
     * @param store       the keystore for the Domibus connector
     * @return true if the import was successful, false otherwise
     * @throws IllegalArgumentException if the contents parameter is null or empty
     * @throws RuntimeException         if there was an error loading the PMode file or importing it
     *                                  into the database
     */
    @Transactional(readOnly = false)
    public boolean importPModes(
        byte[] contents, String description, DomibusConnectorKeystore store) {
        if (contents == null || contents.length < 1) {
            throw new IllegalArgumentException("pModes are not allowed to be null or empty!");
        }
        LOGGER.debug("Starting import of PModes");
        Configuration pmodes;
        try {
            pmodes = (Configuration) byteArrayToXmlObject(contents, Configuration.class,
                                                          Configuration.class
            );
        } catch (Exception e) {
            LOGGER.error("Cannot load provided pmode file!", e);
            throw new RuntimeException(e);
        }

        DomibusConnectorPModeSet connectorPModeSet;
        try {
            connectorPModeSet =
                mapPModeConfigurationToPModeSet(pmodes, contents, description, store);
            this.updatePModeSet(connectorPModeSet);
        } catch (Exception e) {
            LOGGER.error("Cannot import provided pmode file into database!", e);
            throw new RuntimeException(e);
        }

        try {
            updateSecurityConfiguration(store);
            updateHomePartyConfigurationProperties(pmodes);
        } catch (Exception e) {
            LOGGER.error("Error while updating properties", e);
            throw new RuntimeException(e);
        }

        return true;
    }

    /**
     * Update the security toolkit configuration settings within the current business domain.
     *
     * @param store - the StoreSettings to update
     */
    private void updateSecurityConfiguration(DomibusConnectorKeystore store) {
        var storeConfigurationProperties =
            new StoreConfigurationProperties();
        storeConfigurationProperties.setPassword(store.getPasswordPlain());
        storeConfigurationProperties.setType(store.getType().toString());
        storeConfigurationProperties.setPath(
            DatabaseResourceLoader.DB_URL_PREFIX + store.getUuid());
        var dcEcodexContainerProperties = configurationPropertyManagerService.loadConfiguration(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
            DCEcodexContainerProperties.class
        );
        dcEcodexContainerProperties.getSignatureValidation()
                                   .setTrustStore(storeConfigurationProperties);
        configurationPropertyManagerService.updateConfiguration(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), dcEcodexContainerProperties);
    }

    private void updateHomePartyConfigurationProperties(Configuration pmodes) {
        String homePartyName = pmodes.getParty();

        var homeParty = pmodes
            .getBusinessProcesses()
            .getParties()
            .getParty()
            .stream()
            .filter(p -> p.getName().equals(homePartyName))
            .findFirst()
            .get();

        EvidencesToolkitConfigurationProperties homePartyConfigurationProperties =
            configurationPropertyManagerService.loadConfiguration(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                EvidencesToolkitConfigurationProperties.class
            );
        homePartyConfigurationProperties.getIssuerInfo().getAs4Party()
                                        .setName(homeParty.getIdentifier().getFirst().getPartyId());
        homePartyConfigurationProperties.getIssuerInfo().getAs4Party()
                                        .setEndpointAddress(homeParty.getEndpoint());

        configurationPropertyManagerService.updateConfiguration(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
            homePartyConfigurationProperties
        );
    }

    /**
     * Returns the home party for the Domibus Connector. The home party is determined based on the
     * configuration properties loaded using the
     * {@link DomibusConnectorBusinessDomain#getDefaultMessageLaneId()} and
     * {@link EvidencesToolkitConfigurationProperties} classes. The method searches for the home
     * party in the current PMode set and returns it if found. If no home party is found, null is
     * returned.
     *
     * @return the home party for the Domibus Connector, or null if not found
     */
    public DomibusConnectorParty getHomeParty() {
        EvidencesToolkitConfigurationProperties homePartyConfigurationProperties =
            configurationPropertyManagerService.loadConfiguration(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                EvidencesToolkitConfigurationProperties.class
            );

        return getCurrentPModeSet(DomibusConnectorBusinessDomain.getDefaultMessageLaneId())
            .map(DomibusConnectorPModeSet::getParties)
            .flatMap(partiesList -> partiesList.stream()
                                               .filter(p -> (p.getPartyId().equals(
                                                   homePartyConfigurationProperties.getIssuerInfo()
                                                                                   .getAs4Party()
                                                                                   .getName())
                                                   && p.getRoleType()
                                                       .equals(PartyRoleType.INITIATOR)
                                               ))
                                               .findAny()
            ).orElse(null);
    }

    /**
     * Imports the connector store with the provided byte array, password, and store type.
     *
     * @param connectorstoreBytes The byte array representing the connector store data
     * @param password            The password used to secure the connector store
     * @param connectorstoreType  The type of the connector store (e.g., JKS, PKCS12)
     * @return The imported DomibusConnectorKeystore instance
     */
    @Transactional()
    public DomibusConnectorKeystore importConnectorstore(
        byte[] connectorstoreBytes, String password, KeystoreType connectorstoreType) {
        var connectorstore = new DomibusConnectorKeystore();

        var description = "Connectorstore uploaded with PMode-Set imported at " + new Date();
        connectorstore.setDescription(description);

        connectorstore.setKeystoreBytes(connectorstoreBytes);
        connectorstore.setPasswordPlain(password);
        connectorstore.setType(connectorstoreType);

        connectorstore = keystorePersistenceService.persistNewKeystore(connectorstore);

        return connectorstore;
    }

    private DomibusConnectorPModeSet mapPModeConfigurationToPModeSet(
        Configuration pmodes, byte[] contents, String description,
        DomibusConnectorKeystore connectorstore) {
        var connectorPModeSet = new DomibusConnectorPModeSet();
        connectorPModeSet.setDescription(description);
        connectorPModeSet.setpModes(contents);
        connectorPModeSet.setConnectorstore(connectorstore);

        connectorPModeSet.setServices(importServices(pmodes));
        connectorPModeSet.setActions(importActions(pmodes));
        connectorPModeSet.setParties(importParties(pmodes));
        connectorPModeSet.setCreateDate(new Date());
        return connectorPModeSet;
    }

    private List<DomibusConnectorService> importServices(Configuration pmodes) {
        return pmodes.getBusinessProcesses()
                     .getServices()
                     .getService()
                     .stream()
                     .map(s -> {
                         var service = new DomibusConnectorService();
                         service.setService(s.getValue());
                         service.setServiceType(s.getType());
                         return service;
                     })
                     .toList();
    }

    private List<DomibusConnectorAction> importActions(Configuration pmodes) {

        return pmodes.getBusinessProcesses()
                     .getActions()
                     .getAction()
                     .stream()
                     .map(a -> {
                         var connectorAction = new DomibusConnectorAction();
                         connectorAction.setAction(a.getValue());
                         return connectorAction;
                     })
                     .toList();
    }

    private List<DomibusConnectorParty> importParties(Configuration pmodes) {
        var homeParty = pmodes.getParty();

        Map<String, Role> roles = pmodes.getBusinessProcesses()
                                        .getRoles()
                                        .getRole()
                                        .stream()
                                        .collect(Collectors.toMap(
                                            Role::getName,
                                            Function.identity()
                                        ));

        Map<String, Configuration.BusinessProcesses.Parties.Party> parties = pmodes
            .getBusinessProcesses()
            .getParties()
            .getParty()
            .stream()
            .collect(Collectors.toMap(
                Configuration.BusinessProcesses.Parties.Party::getName,
                Function.identity()
            ));

        Map<String, PartyIdType> partyIdTypes = pmodes
            .getBusinessProcesses()
            .getParties()
            .getPartyIdTypes()
            .getPartyIdType()
            .stream()
            .collect(Collectors.toMap(PartyIdType::getName, Function.identity()));

        // remove duplicate parties
        return pmodes.getBusinessProcesses()
                     .getProcess()
                     .stream()
                     .map(process ->
                              Stream.of(
                                  process
                                      .getInitiatorParties()
                                      .getInitiatorParty()
                                      .stream()
                                      .map(
                                          initiatorParty -> this.createParty(
                                              partyIdTypes,
                                              parties,
                                              roles.get(
                                                  process.getInitiatorRole()),
                                              initiatorParty.getName(),
                                              PartyRoleType.INITIATOR
                                          )),
                                  process
                                      .getResponderParties()
                                      .getResponderParty()
                                      .stream()
                                      .map(responderParty -> this.createParty(
                                          partyIdTypes,
                                          parties, roles.get(
                                              process.getResponderRole()),
                                          responderParty.getName(),
                                          PartyRoleType.RESPONDER
                                      ))
                              ).flatMap(Function.identity())
                     ).flatMap(Function.identity())
                     .flatMap(Function.identity())
                     .distinct() // remove duplicate parties
                     .toList();
    }

    public List<DomibusConnectorParty> getPartyList() {
        return getCurrentPModeSetOrNewSet().getParties();
    }

    public List<DomibusConnectorAction> getActionList() {
        return getCurrentPModeSetOrNewSet().getActions();
    }

    /**
     * Returns a list of action names as strings.
     *
     * @return A list of action names as strings.
     */
    public List<String> getActionListString() {
        return this.getActionList()
                   .stream()
                   .map(DomibusConnectorAction::getAction)
                   .toList();
    }

    public List<DomibusConnectorService> getServiceList() {
        return getCurrentPModeSetOrNewSet().getServices();
    }

    /**
     * Returns a list of service names as strings.
     *
     * @return A list of service names as strings.
     */
    public List<String> getServiceListString() {
        return this.getServiceList()
                   .stream()
                   .map(DomibusConnectorService::getService)
                   .toList();
    }

    /**
     * Deletes a party from the current Connector PMode set.
     *
     * @param connectorParty The DomibusConnectorParty to be deleted. Must not be null.
     * @throws NullPointerException if the party is null.
     */
    @Transactional()
    public void deleteParty(DomibusConnectorParty connectorParty) {
        LOGGER.trace("#deleteParty: called, use partyDao to delete");
        var connectorPModeSet = this.getCurrentPModeSetOrNewSet();
        connectorPModeSet.getParties().remove(connectorParty);
        connectorPModeSet.setDescription(
            String.format("delete party %s clicked in UI", connectorParty));
        updatePModeSet(connectorPModeSet);
    }

    /**
     * Updates a party in the current Connector PMode set.
     *
     * @param oldParty     The original party to be updated. Must not be null.
     * @param updatedParty The updated party to replace the old party. Must not be null.
     * @return The updated party after the update.
     * @throws NullPointerException if either oldParty or updatedParty is null.
     */
    @Transactional(readOnly = false)
    public DomibusConnectorParty updateParty(
        DomibusConnectorParty oldParty, DomibusConnectorParty updatedParty) {
        LOGGER.trace(
            "#updateParty: called, update party [{}] to party [{}]", oldParty, updatedParty);
        var connectorPModeSet = this.getCurrentPModeSetOrNewSet();
        connectorPModeSet.getParties().remove(oldParty);
        connectorPModeSet.getParties().add(updatedParty);
        connectorPModeSet.setDescription(String.format("updated party %s in UI", updatedParty));
        // find party in new p-modes by equals
        return updatePModeSet(connectorPModeSet)
            .getParties()
            .stream()
            .filter(p -> p.equals(updatedParty))
            .findAny()
            .get();
    }

    private Stream<DomibusConnectorParty> createParty(
        Map<String, PartyIdType> partyIdTypes,
        Map<String, Configuration.BusinessProcesses.Parties.Party> parties,
        Role role, String partyName, DomibusConnectorParty.PartyRoleType roleType) {

        return parties.get(partyName)
                      .getIdentifier()
                      .stream()
                      .map(identifier -> {
                          var connectorParty = new DomibusConnectorParty();
                          connectorParty.setPartyName(partyName);
                          connectorParty.setRole(role.getValue());
                          connectorParty.setPartyId(identifier.getPartyId());
                          connectorParty.setRoleType(roleType);
                          String partyIdTypeValue =
                              partyIdTypes.get(identifier.getPartyIdType()).getValue();
                          connectorParty.setPartyIdType(partyIdTypeValue);
                          return connectorParty;
                      });
    }

    /**
     * Creates a new DomibusConnectorParty and adds it to the current PMode set.
     *
     * @param party The DomibusConnectorParty to be created and added to the current PMode set. Must
     *              not be null.
     * @return The created DomibusConnectorParty object.
     */
    @Transactional
    public DomibusConnectorParty createParty(DomibusConnectorParty party) {
        LOGGER.trace("#createParty: called with party [{}]", party);
        var connectorPModeSet = this.getCurrentPModeSetOrNewSet();
        connectorPModeSet.getParties().add(party);
        connectorPModeSet.setDescription(String.format("added party %s in UI", party));
        // find party in new p-modes by equals
        return updatePModeSet(connectorPModeSet)
            .getParties()
            .stream()
            .filter(p -> p.equals(party))
            .findAny()
            .get();
    }

    /**
     * Deletes the given DomibusConnectorAction from the current PMode set.
     *
     * @param action The DomibusConnectorAction to be deleted. Must not be null.
     * @throws NullPointerException if the action is null.
     */
    @Transactional
    public void deleteAction(DomibusConnectorAction action) {
        LOGGER.trace("deleteAction: delete Action [{}]", action);
        var connectorPModeSet = this.getCurrentPModeSetOrNewSet();
        connectorPModeSet.getActions().remove(action);
        connectorPModeSet.setDescription(String.format("delete action %s clicked in UI", action));
        updatePModeSet(connectorPModeSet);
    }

    /**
     * Creates a new DomibusConnectorAction and adds it to the current PMode set.
     *
     * @param action The DomibusConnectorAction to be created and added to the current PMode set.
     *               Must not be null.
     * @return The created DomibusConnectorAction object.
     * @throws NullPointerException if the action is null.
     */
    @Transactional
    public DomibusConnectorAction createAction(DomibusConnectorAction action) {
        LOGGER.trace("#createAction: called with action [{}]", action);
        var connectorPModeSet = this.getCurrentPModeSetOrNewSet();
        connectorPModeSet.getActions().add(action);
        connectorPModeSet.setDescription(String.format("added action %s in UI", action));
        // find party in new p-modes by equals
        return updatePModeSet(connectorPModeSet)
            .getActions()
            .stream()
            .filter(p -> p.equals(action))
            .findAny()
            .get();
    }

    /**
     * Updates an existing DomibusConnectorAction in the current PMode set.
     *
     * @param oldAction     The original DomibusConnectorAction to be updated. Must not be null.
     * @param updatedAction The updated DomibusConnectorAction to replace the old action. Must not
     *                      be null.
     * @return The updated DomibusConnectorAction object.
     * @throws NullPointerException if either oldAction or updatedAction is null.
     */
    @Transactional(readOnly = false)
    public DomibusConnectorAction updateAction(
        DomibusConnectorAction oldAction, DomibusConnectorAction updatedAction) {
        LOGGER.trace(
            "updateAction: updateAction with oldAction [{}] and new action [{}]", oldAction,
            updatedAction
        );
        var connectorPModeSet = this.getCurrentPModeSetOrNewSet();
        connectorPModeSet.getActions().remove(oldAction);
        connectorPModeSet.getActions().add(updatedAction);
        connectorPModeSet.setDescription(String.format("updated action %s in UI", updatedAction));
        // find party in new p-modes by equals
        return updatePModeSet(connectorPModeSet)
            .getActions()
            .stream()
            .filter(p -> p.equals(updatedAction))
            .findAny()
            .get();
    }

    /**
     * Creates a new DomibusConnectorService and adds it to the current PMode set.
     *
     * @param service The DomibusConnectorService to be created and added to the current PMode set.
     * @return The created DomibusConnectorService object.
     */
    @Transactional()
    public DomibusConnectorService createService(DomibusConnectorService service) {
        LOGGER.trace("createService: with service [{}]", service);
        var connectorPModeSet = this.getCurrentPModeSetOrNewSet();
        connectorPModeSet.getServices().add(service);
        connectorPModeSet.setDescription(String.format("added service %s in UI", service));
        // find party in new p-modes by equals
        return updatePModeSet(connectorPModeSet)
            .getServices()
            .stream()
            .filter(p -> p.equals(service))
            .findAny()
            .get();
    }

    /**
     * Updates a DomibusConnectorService in the current PMode set.
     *
     * @param oldService     The original DomibusConnectorService to be updated. Must not be null.
     * @param updatedService The updated DomibusConnectorService to replace the old service. Must
     *                       not be null.
     * @return The updated DomibusConnectorService object.
     * @throws NullPointerException if either oldService or updatedService is null.
     */
    @Transactional
    public DomibusConnectorService updateService(
        DomibusConnectorService oldService, DomibusConnectorService updatedService) {
        LOGGER.trace("updateService: with new service [{}]", updatedService);
        var connectorPModeSet = this.getCurrentPModeSetOrNewSet();
        connectorPModeSet.getServices().remove(oldService);
        connectorPModeSet.getServices().add(updatedService);
        connectorPModeSet.setDescription(String.format("updated service %s in UI", updatedService));
        // find party in new p-modes by equals
        return updatePModeSet(connectorPModeSet)
            .getServices()
            .stream()
            .filter(p -> p.equals(updatedService))
            .findAny()
            .get();
    }

    /**
     * Deletes a service from the current PMode set.
     *
     * @param service The service to be deleted. Must not be null.
     * @throws NullPointerException if the service is null.
     */
    @Transactional()
    public void deleteService(DomibusConnectorService service) {
        LOGGER.trace("deleteService: with service [{}]", service);
        DomibusConnectorPModeSet connectorPModeSet = this.getCurrentPModeSetOrNewSet();
        connectorPModeSet.getServices().remove(service);
        connectorPModeSet.setDescription(String.format("delete service %s clicked in UI", service));
        updatePModeSet(connectorPModeSet);
    }

    private DomibusConnectorPModeSet updatePModeSet(DomibusConnectorPModeSet connectorPModeSet) {
        var laneId = connectorPModeSet.getMessageLaneId();
        if (laneId == null) {
            laneId = DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
            LOGGER.info("Setting default lane [{}] pModeSet", laneId);
            connectorPModeSet.setMessageLaneId(laneId);
        }
        this.connectorPModeService.updatePModeConfigurationSet(connectorPModeSet);
        return this.getCurrentPModeSet(laneId).orElseThrow(
            () -> new IllegalStateException("After update there must be a p-ModeSet with this id"));
    }

    /**
     * Updates the description of an active PMode set for a Domibus Connector.
     *
     * @param connectorPModeSet The DomibusConnectorPModeSet object representing the active PMode
     *                          set. Must not be null.
     * @throws NullPointerException if connectorPModeSet is null.
     */
    @Transactional()
    public void updateActivePModeSetDescription(DomibusConnectorPModeSet connectorPModeSet) {
        var laneId = connectorPModeSet.getMessageLaneId();
        if (laneId == null) {
            laneId = DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
            LOGGER.info("Setting default lane [{}] pModeSet", laneId);
            connectorPModeSet.setMessageLaneId(laneId);
        }
        this.connectorPModeService.updateActivePModeSetDescription(connectorPModeSet);
    }

    /**
     * Updates the password for the connector keystore in the given DomibusConnectorPModeSet.
     *
     * @param connectorPModeSet    The DomibusConnectorPModeSet containing the connector keystore to
     *                             update.
     * @param newConnectorstorePwd The new password for the connector keystore.
     */
    @Transactional()
    public void updateConnectorstorePassword(
        DomibusConnectorPModeSet connectorPModeSet, String newConnectorstorePwd) {

        this.keystorePersistenceService.updateKeystorePassword(
            connectorPModeSet.getConnectorstore(), newConnectorstorePwd);
    }

    public Optional<DomibusConnectorPModeSet> getCurrentPModeSet(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId) {
        return this.connectorPModeService.getCurrentPModeSet(laneId);
    }

    /**
     * Returns a list of inactive PMode sets for the Domibus Connector.
     *
     * @return A list of DomibusConnectorPModeSet objects representing the inactive PMode sets.
     */
    public List<DomibusConnectorPModeSet> getInactivePModeSets() {
        final DomibusConnectorBusinessDomain.BusinessDomainId laneId =
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId();

        return this.connectorPModeService.getInactivePModeSets(laneId);
    }

    /**
     * Retrieves the current PMode set or creates a new set if none exists.
     *
     * @return An Optional holding the current PMode set, or an empty Optional if a new set was
     *      created.
     */
    private DomibusConnectorPModeSet getCurrentPModeSetOrNewSet() {
        final var laneId = DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
        var currentPModeSetOptional = this.connectorPModeService.getCurrentPModeSet(laneId);
        return currentPModeSetOptional.orElseGet(() -> {
            var connectorPModeSet = new DomibusConnectorPModeSet();
            connectorPModeSet.setMessageLaneId(laneId);
            return connectorPModeSet;
        });
    }

    public DomibusConnectorKeystore getConnectorstore(String connectorstoreUUID) {
        return keystorePersistenceService.getKeystoreByUUID(connectorstoreUUID);
    }
}
