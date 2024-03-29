package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.persistence.dao.CommonPersistenceTest;
import eu.domibus.connector.persistence.dao.DomibusConnectorKeystoreDao;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@CommonPersistenceTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DomibusConnectorPModePersistenceServiceTest {
    @Autowired
    DomibusConnectorPModePersistenceService pModePersistenceService;

    @Autowired
    DomibusConnectorKeystoreDao keyStoreDao;

    @Test
    @Order(1)
    void getConfiguredSingleParty() {
        DomibusConnectorParty searchParty = new DomibusConnectorParty();
        searchParty.setPartyId("domibus-blue");
        searchParty.setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);

        Optional<DomibusConnectorParty> configuredSingle = pModePersistenceService.getConfiguredSingle(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                searchParty
        );

        assertThat(configuredSingle).isPresent();
    }

    @Test
    @Order(2)
    void testGetConfiguredSingleAction() {
        DomibusConnectorAction searchAction = new DomibusConnectorAction();
        searchAction.setAction("Form_A");

        Optional<DomibusConnectorAction> configuredSingle = pModePersistenceService.getConfiguredSingle(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                searchAction
        );
        assertThat(configuredSingle).isPresent();
    }

    @Test
    @Order(3)
    void testGetConfiguredService() {
        DomibusConnectorService searchService = new DomibusConnectorService();
        searchService.setService("service1");
        searchService.setServiceType(null);

        Optional<DomibusConnectorService> configuredSingle = pModePersistenceService.getConfiguredSingle(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                searchService
        );
        assertThat(configuredSingle).isPresent();
    }

    @Test
    @Order(198)
    void updatePModeConfigurationSet_shouldThrow_DueUnsetConnectorStoreUUID() {
        DomibusConnectorPModeSet pModeSet = new DomibusConnectorPModeSet();
        pModeSet.setMessageLaneId(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        pModeSet.setDescription("Example");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pModePersistenceService.updatePModeConfigurationSet(pModeSet);
        });
    }

    @Test
    @Order(199)
    void updatePModeConfigurationSet_shouldThrow_DueNotFoundConnectorStore() {
        DomibusConnectorPModeSet pModeSet = new DomibusConnectorPModeSet();
        pModeSet.setMessageLaneId(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        pModeSet.setDescription("Example");

        DomibusConnectorKeystore keystore = new DomibusConnectorKeystore();
        keystore.setUuid("store1123");

        pModeSet.setConnectorstore(keystore);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pModePersistenceService.updatePModeConfigurationSet(pModeSet);
        });
    }

    @Test
    @Order(200)
        // should run last, because it changes db
    void updatePModeConfigurationSet() {
        DomibusConnectorPModeSet pModeSet = new DomibusConnectorPModeSet();
        pModeSet.setMessageLaneId(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        pModeSet.setDescription("Example");

        DomibusConnectorKeystore keystore = new DomibusConnectorKeystore();
        keystore.setUuid("store1");
        pModeSet.setConnectorstore(keystore);

        pModeSet.getServices().add(DomainEntityCreator.createServiceEPO());
        pModeSet.getActions().add(DomainEntityCreator.createActionForm_A());

        DomibusConnectorParty partyDomibusRedResponder = DomainEntityCreator.createPartyDomibusRed();
        partyDomibusRedResponder.setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        pModeSet.getParties().add(partyDomibusRedResponder);

        DomibusConnectorParty partyDomibusRedInitiator = DomainEntityCreator.createPartyDomibusRed();
        partyDomibusRedInitiator.setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        pModeSet.getParties().add(partyDomibusRedInitiator);

        DomibusConnectorParty partyDomibusBlueResponder = DomainEntityCreator.createPartyDomibusBlue();
        partyDomibusBlueResponder.setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        pModeSet.getParties().add(partyDomibusBlueResponder);

        DomibusConnectorParty partyDomibusBluInitiator = DomainEntityCreator.createPartyDomibusBlue();
        partyDomibusBluInitiator.setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        pModeSet.getParties().add(partyDomibusBluInitiator);

        pModePersistenceService.updatePModeConfigurationSet(pModeSet);
    }

    @Test
    @Order(20)
    void getCurrentPModeSet() {
        Optional<DomibusConnectorPModeSet> currentPModeSet =
                pModePersistenceService.getCurrentPModeSet(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        assertThat(currentPModeSet).isPresent();
    }
}
