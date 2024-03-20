package eu.domibus.connector.ui.service;

import eu.domibus.configuration.Configuration;
import eu.domibus.configuration.Configuration.BusinessProcesses.Parties.PartyIdTypes.PartyIdType;
import eu.domibus.configuration.Configuration.BusinessProcesses.Roles.Role;
import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.model.DomibusConnectorKeystore.KeystoreType;
import eu.domibus.connector.domain.model.DomibusConnectorParty.PartyRoleType;
import eu.domibus.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import eu.domibus.connector.evidences.spring.HomePartyConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.persistence.service.DomibusConnectorKeystorePersistenceService;
import eu.domibus.connector.persistence.service.DomibusConnectorPModeService;
import eu.domibus.connector.persistence.service.DomibusConnectorPropertiesPersistenceService;
import eu.domibus.connector.persistence.spring.DatabaseResourceLoader;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class WebPModeService {

    protected final static Logger LOGGER = LoggerFactory.getLogger(WebPModeService.class);

    private final DomibusConnectorPropertiesPersistenceService propertiesPersistenceService;
    private final DomibusConnectorPModeService pModeService;
    private final DomibusConnectorKeystorePersistenceService keystorePersistenceService;
    private final ConfigurationPropertyManagerService configurationPropertyManagerService;
    private final ApplicationContext ctx;

    public WebPModeService(DomibusConnectorPropertiesPersistenceService propertiesPersistenceService,
                           DomibusConnectorPModeService pModeService,
                           DomibusConnectorKeystorePersistenceService keystorePersistenceService,
                           ConfigurationPropertyManagerService configurationPropertyManagerService,
                           ApplicationContext ctx) {
        this.propertiesPersistenceService = propertiesPersistenceService;
        this.pModeService = pModeService;
        this.keystorePersistenceService = keystorePersistenceService;
        this.configurationPropertyManagerService = configurationPropertyManagerService;
        this.ctx = ctx;
    }

    public static Object byteArrayToXmlObject(final byte[] xmlAsBytes, final Class<?> instantiationClazz,
                                              final Class<?>... initializationClasses) throws Exception {

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(xmlAsBytes);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().parse(bis);

            JAXBContext ctx = JAXBContext.newInstance(initializationClasses);

            Unmarshaller unmarshaller = ctx.createUnmarshaller();

            JAXBElement<?> jaxbElement = unmarshaller.unmarshal(document, instantiationClazz);
            return jaxbElement.getValue();
        } catch (JAXBException | SAXException | IOException | ParserConfigurationException e) {
            throw new Exception("Exception parsing byte[] to " + instantiationClazz.getName(), e);
        }

    }

    @Transactional(readOnly = false)
    public boolean importPModes(byte[] contents, String description, DomibusConnectorKeystore store) {
        if (contents == null || contents.length < 1) {
            throw new IllegalArgumentException("pModes are not allowed to be null or empty!");
        }
        LOGGER.debug("Starting import of PModes");
        Configuration pmodes = null;
        try {
            pmodes = (Configuration) byteArrayToXmlObject(contents, Configuration.class, Configuration.class);
        } catch (Exception e) {
            LOGGER.error("Cannot load provided pmode file!", e);
            throw new RuntimeException(e);
        }

        DomibusConnectorPModeSet pModeSet;
        try {
            pModeSet = mapPModeConfigurationToPModeSet(pmodes, contents, description, store);
            this.updatePModeSet(pModeSet);
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
     * Update the security toolki configuration settings
     * within the current business domain
     * @param store - the StoreSettings to update
     */
    private void updateSecurityConfiguration(DomibusConnectorKeystore store) {
        DCEcodexContainerProperties dcEcodexContainerProperties = configurationPropertyManagerService.loadConfiguration(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), DCEcodexContainerProperties.class);
//        if(dcEcodexContainerProperties.getSignatureValidation().getTrustStore()!=null) {
//        dcEcodexContainerProperties.getSignatureValidation().getTrustStore().setPassword(store.getPasswordPlain());
//        dcEcodexContainerProperties.getSignatureValidation().getTrustStore().setType(store.getType().toString());
//        dcEcodexContainerProperties.getSignatureValidation().getTrustStore().setPath(DatabaseResourceLoader.DB_URL_PREFIX + store.getUuid());
//        }else {
        	StoreConfigurationProperties storeConfigurationProperties = new StoreConfigurationProperties();
        	storeConfigurationProperties.setPassword(store.getPasswordPlain());
        	storeConfigurationProperties.setType(store.getType().toString());
        	storeConfigurationProperties.setPath(DatabaseResourceLoader.DB_URL_PREFIX + store.getUuid());
        	dcEcodexContainerProperties.getSignatureValidation().setTrustStore(storeConfigurationProperties);
//        }
        configurationPropertyManagerService.updateConfiguration(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), dcEcodexContainerProperties);
    }

    private void updateHomePartyConfigurationProperties(Configuration pmodes) {
        String homePartyName = pmodes.getParty();

        Configuration.BusinessProcesses.Parties.Party homeParty = pmodes
                .getBusinessProcesses()
                .getParties()
                .getParty()
                .stream()
                .filter(p -> p.getName().equals(homePartyName))
                .findFirst()
                .get();

        EvidencesToolkitConfigurationProperties homePartyConfigurationProperties = configurationPropertyManagerService.loadConfiguration(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), EvidencesToolkitConfigurationProperties.class);
        homePartyConfigurationProperties.getIssuerInfo().getAs4Party().setName(homeParty.getIdentifier().get(0).getPartyId());
        homePartyConfigurationProperties.getIssuerInfo().getAs4Party().setEndpointAddress(homeParty.getEndpoint());

        configurationPropertyManagerService.updateConfiguration(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), homePartyConfigurationProperties);
    }
    
    public DomibusConnectorParty getHomeParty() {
    	EvidencesToolkitConfigurationProperties homePartyConfigurationProperties = configurationPropertyManagerService.loadConfiguration(DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), EvidencesToolkitConfigurationProperties.class);
    	
    	return getCurrentPModeSet(DomibusConnectorBusinessDomain.getDefaultMessageLaneId())
                .map(DomibusConnectorPModeSet::getParties)
                .flatMap(partiesList -> partiesList.stream()
                        .filter(p -> (p.getPartyId().equals(homePartyConfigurationProperties.getIssuerInfo().getAs4Party().getName()) && p.getRoleType().equals(PartyRoleType.INITIATOR)))
                        .findAny()
                ).orElse(null);
    }


    @Transactional(readOnly = false)
    public DomibusConnectorKeystore importConnectorstore(byte[] connectorstoreBytes, String password, KeystoreType connectorstoreType) {
        DomibusConnectorKeystore connectorstore = new DomibusConnectorKeystore();

        String description = "Connectorstore uploaded with PMode-Set imported at " + new Date();
        connectorstore.setDescription(description);

        connectorstore.setKeystoreBytes(connectorstoreBytes);
        connectorstore.setPasswordPlain(password);
        connectorstore.setType(connectorstoreType);

        connectorstore = keystorePersistenceService.persistNewKeystore(connectorstore);

        return connectorstore;
    }


    private DomibusConnectorPModeSet mapPModeConfigurationToPModeSet(Configuration pmodes, byte[] contents, String description, DomibusConnectorKeystore connectorstore) {
        DomibusConnectorPModeSet pModeSet = new DomibusConnectorPModeSet();
        pModeSet.setDescription(description);
        pModeSet.setpModes(contents);
        pModeSet.setConnectorstore(connectorstore);

        pModeSet.setServices(importServices(pmodes));
        pModeSet.setActions(importActions(pmodes));
        pModeSet.setParties(importParties(pmodes));
        pModeSet.setCreateDate(new Date());
        return pModeSet;
    }


    private List<DomibusConnectorService> importServices(Configuration pmodes) {
        return pmodes.getBusinessProcesses()
                .getServices()
                .getService()
                .stream()
                .map(s -> {
                    DomibusConnectorService service = new DomibusConnectorService();
                    service.setService(s.getValue());
                    service.setServiceType(s.getType());
                    return service;
                })
                .collect(Collectors.toList());
    }

    private List<DomibusConnectorAction> importActions(Configuration pmodes) {

        return pmodes.getBusinessProcesses()
                .getActions()
                .getAction()
                .stream()
                .map(a -> {
                    DomibusConnectorAction action = new DomibusConnectorAction();
                    action.setAction(a.getValue());
                    return action;
                })
                .collect(Collectors.toList());
    }

    private List<DomibusConnectorParty> importParties(Configuration pmodes) {
        String homeParty = pmodes.getParty();


        Map<String, Role> roles = pmodes.getBusinessProcesses()
                .getRoles()
                .getRole()
                .stream()
                .collect(Collectors.toMap(r -> r.getName(), Function.identity()));

        Map<String, Configuration.BusinessProcesses.Parties.Party> parties = pmodes
                .getBusinessProcesses()
                .getParties()
                .getParty()
                .stream()
                .collect(Collectors.toMap(p -> p.getName(), Function.identity()));

        Map<String, PartyIdType> partyIdTypes = pmodes
                .getBusinessProcesses()
                .getParties()
                .getPartyIdTypes()
                .getPartyIdType()
                .stream()
                .collect(Collectors.toMap(p -> p.getName(), Function.identity()));

        List<DomibusConnectorParty> importedParties = pmodes.getBusinessProcesses()
                .getProcess()
                .stream()
                .map(process ->
                        Stream.of(process
                                        .getInitiatorParties()
                                        .getInitiatorParty()
                                        .stream()
                                        .map(initiatorParty -> this.createParty(partyIdTypes, parties, roles.get(process.getInitiatorRole()), initiatorParty.getName(), DomibusConnectorParty.PartyRoleType.INITIATOR)),
                                process.
                                        getResponderParties().
                                        getResponderParty().
                                        stream().
                                        map(responderParty -> this.createParty(partyIdTypes, parties, roles.get(process.getResponderRole()), responderParty.getName(), DomibusConnectorParty.PartyRoleType.RESPONDER))
                        ).flatMap(Function.identity())
                ).flatMap(Function.identity())
                .flatMap(Function.identity())
                .distinct() //remove duplicate parties
                .collect(Collectors.toList());
        return importedParties;

    }


    private Stream<DomibusConnectorParty> createParty(Map<String, PartyIdType> partyIdTypes, Map<String, Configuration.BusinessProcesses.Parties.Party> parties,
                                                      Role role, String partyName, DomibusConnectorParty.PartyRoleType roleType) {

        return parties.get(partyName)
                .getIdentifier()
                .stream()
                .map(identifier -> {
                    DomibusConnectorParty p = new DomibusConnectorParty();
                    p.setPartyName(partyName);
                    p.setRole(role.getValue());
                    p.setPartyId(identifier.getPartyId());
                    p.setRoleType(roleType);
                    String partyIdTypeValue = partyIdTypes.get(identifier.getPartyIdType()).getValue();
                    p.setPartyIdType(partyIdTypeValue);
                    return p;
                });
    }


    public List<DomibusConnectorParty> getPartyList() {
        return getCurrentPModeSetOrNewSet().getParties();
    }

    public List<DomibusConnectorAction> getActionList() {
        return getCurrentPModeSetOrNewSet().getActions();
    }

    public List<String> getActionListString() {
        return this.getActionList()
                .stream()
                .map(DomibusConnectorAction::getAction)
                .collect(Collectors.toList());
    }

    public List<DomibusConnectorService> getServiceList() {
        return getCurrentPModeSetOrNewSet().getServices();
    }

    public List<String> getServiceListString() {
        return this.getServiceList()
                .stream()
                .map(DomibusConnectorService::getService)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    public void deleteParty(DomibusConnectorParty p) {
        LOGGER.trace("#deleteParty: called, use partyDao to delete");
        DomibusConnectorPModeSet pModes = this.getCurrentPModeSetOrNewSet();
        pModes.getParties().remove(p);
        pModes.setDescription(String.format("delete party %s clicked in UI", p));
        updatePModeSet(pModes);
    }

    @Transactional(readOnly = false)
    public DomibusConnectorParty updateParty(DomibusConnectorParty oldParty, DomibusConnectorParty updatedParty) {
        LOGGER.trace("#updateParty: called, update party [{}] to party [{}]", oldParty, updatedParty);
        DomibusConnectorPModeSet pModes = this.getCurrentPModeSetOrNewSet();
        pModes.getParties().remove(oldParty);
        pModes.getParties().add(updatedParty);
        pModes.setDescription(String.format("updated party %s in UI", updatedParty));
        //find party in new p-modes by equals
        return updatePModeSet(pModes)
                .getParties()
                .stream()
                .filter(p -> p.equals(updatedParty))
                .findAny()
                .get();
    }

    @Transactional
    public DomibusConnectorParty createParty(DomibusConnectorParty party) {
        LOGGER.trace("#createParty: called with party [{}]", party);
        DomibusConnectorPModeSet pModes = this.getCurrentPModeSetOrNewSet();
        pModes.getParties().add(party);
        pModes.setDescription(String.format("added party %s in UI", party));
        //find party in new p-modes by equals
        return updatePModeSet(pModes)
                .getParties()
                .stream()
                .filter(p -> p.equals(party))
                .findAny()
                .get();
    }

    @Transactional
    public void deleteAction(DomibusConnectorAction action) {
        LOGGER.trace("deleteAction: delete Action [{}]", action);
        DomibusConnectorPModeSet pModes = this.getCurrentPModeSetOrNewSet();
        pModes.getActions().remove(action);
        pModes.setDescription(String.format("delete action %s clicked in UI", action));
        updatePModeSet(pModes);
    }

    @Transactional
    public DomibusConnectorAction createAction(DomibusConnectorAction action) {
        LOGGER.trace("#createAction: called with action [{}]", action);
        DomibusConnectorPModeSet pModes = this.getCurrentPModeSetOrNewSet();
        pModes.getActions().add(action);
        pModes.setDescription(String.format("added action %s in UI", action));
        //find party in new p-modes by equals
        return updatePModeSet(pModes)
                .getActions()
                .stream()
                .filter(p -> p.equals(action))
                .findAny()
                .get();
    }

    @Transactional(readOnly = false)
    public DomibusConnectorAction updateAction(DomibusConnectorAction oldAction, DomibusConnectorAction updatedAction) {
        LOGGER.trace("updateAction: updateAction with oldAction [{}] and new action [{}]", oldAction, updatedAction);
        DomibusConnectorPModeSet pModes = this.getCurrentPModeSetOrNewSet();
        pModes.getActions().remove(oldAction);
        pModes.getActions().add(updatedAction);
        pModes.setDescription(String.format("updated action %s in UI", updatedAction));
        //find party in new p-modes by equals
        return updatePModeSet(pModes)
                .getActions()
                .stream()
                .filter(p -> p.equals(updatedAction))
                .findAny()
                .get();
    }

    @Transactional(readOnly = false)
    public DomibusConnectorService createService(DomibusConnectorService service) {
        LOGGER.trace("createService: with service [{}]", service);
        DomibusConnectorPModeSet pModes = this.getCurrentPModeSetOrNewSet();
        pModes.getServices().add(service);
        pModes.setDescription(String.format("added service %s in UI", service));
        //find party in new p-modes by equals
        return updatePModeSet(pModes)
                .getServices()
                .stream()
                .filter(p -> p.equals(service))
                .findAny()
                .get();
    }

    @Transactional
    public DomibusConnectorService updateService(DomibusConnectorService oldService, DomibusConnectorService updatedService) {
        LOGGER.trace("updateService: with new service [{}]", updatedService);
        DomibusConnectorPModeSet pModes = this.getCurrentPModeSetOrNewSet();
        pModes.getServices().remove(oldService);
        pModes.getServices().add(updatedService);
        pModes.setDescription(String.format("updated service %s in UI", updatedService));
        //find party in new p-modes by equals
        return updatePModeSet(pModes)
                .getServices()
                .stream()
                .filter(p -> p.equals(updatedService))
                .findAny()
                .get();
    }

    @Transactional(readOnly = false)
    public void deleteService(DomibusConnectorService service) {
        LOGGER.trace("deleteService: with service [{}]", service);
        DomibusConnectorPModeSet pModes = this.getCurrentPModeSetOrNewSet();
        pModes.getServices().remove(service);
        pModes.setDescription(String.format("delete service %s clicked in UI", service));
        updatePModeSet(pModes);
    }

    private DomibusConnectorPModeSet updatePModeSet(DomibusConnectorPModeSet pModes) {
        DomibusConnectorBusinessDomain.BusinessDomainId laneId = pModes.getMessageLaneId();
        if (laneId == null) {
            laneId = DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
            LOGGER.info("Setting default lane [{}] pModeSet", laneId);
            pModes.setMessageLaneId(laneId);
        }
        this.pModeService.updatePModeConfigurationSet(pModes);
        return this.getCurrentPModeSet(laneId).orElseThrow(() -> new IllegalStateException("After update there must be a p-ModeSet with this id"));
    }

    @Transactional(readOnly = false)
    public void updateActivePModeSetDescription(DomibusConnectorPModeSet pModes) {
    	DomibusConnectorBusinessDomain.BusinessDomainId laneId = pModes.getMessageLaneId();
        if (laneId == null) {
            laneId = DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
            LOGGER.info("Setting default lane [{}] pModeSet", laneId);
            pModes.setMessageLaneId(laneId);
        }
        this.pModeService.updateActivePModeSetDescription(pModes);
    }

    @Transactional(readOnly = false)
    public void updateConnectorstorePassword(DomibusConnectorPModeSet pModes, String newConnectorstorePwd) {

    	this.keystorePersistenceService.updateKeystorePassword(pModes.getConnectorstore(), newConnectorstorePwd);
    }

    public Optional<DomibusConnectorPModeSet> getCurrentPModeSet(DomibusConnectorBusinessDomain.BusinessDomainId laneId) {
        return this.pModeService.getCurrentPModeSet(laneId);
    }

    public List<DomibusConnectorPModeSet> getInactivePModeSets(){
    	final DomibusConnectorBusinessDomain.BusinessDomainId laneId = DomibusConnectorBusinessDomain.getDefaultMessageLaneId();

    	return this.pModeService.getInactivePModeSets(laneId);
    }

    private DomibusConnectorPModeSet getCurrentPModeSetOrNewSet() {
        final DomibusConnectorBusinessDomain.BusinessDomainId laneId = DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
        Optional<DomibusConnectorPModeSet> currentPModeSetOptional = this.pModeService.getCurrentPModeSet(laneId);
        return currentPModeSetOptional.orElseGet(() -> {
            DomibusConnectorPModeSet set = new DomibusConnectorPModeSet();
            set.setMessageLaneId(laneId);
            return set;
        });
    }

    public DomibusConnectorKeystore getConnectorstore(String connectorstoreUUID) {
    	return keystorePersistenceService.getKeystoreByUUID(connectorstoreUUID);
    }

}
