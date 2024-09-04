/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorKeystore;
import eu.ecodex.connector.domain.model.DomibusConnectorPModeSet;
import eu.ecodex.connector.domain.model.DomibusConnectorParty;
import eu.ecodex.connector.domain.model.DomibusConnectorService;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import eu.ecodex.connector.persistence.dao.CommonPersistenceTest;
import eu.ecodex.connector.persistence.dao.DomibusConnectorKeystoreDao;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

@CommonPersistenceTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DomibusConnectorPModePersistenceServiceTest {
    @Autowired
    DomibusConnectorPModePersistenceService connectorPModePersistenceService;
    @Autowired
    DomibusConnectorKeystoreDao keyStoreDao;

    @Test
    @Order(1)
    void getConfiguredSingleParty() {
        DomibusConnectorParty searchParty = new DomibusConnectorParty();
        searchParty.setPartyId("domibus-blue");
        searchParty.setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);

        Optional<DomibusConnectorParty> configuredSingle =
            connectorPModePersistenceService.getConfiguredSingle(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), searchParty);

        assertThat(configuredSingle).isPresent();
    }

    @Test
    @Order(2)
    void testGetConfiguredSingleAction() {
        DomibusConnectorAction searchAction = new DomibusConnectorAction();
        searchAction.setAction("Form_A");

        Optional<DomibusConnectorAction> configuredSingle =
            connectorPModePersistenceService.getConfiguredSingle(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), searchAction);
        assertThat(configuredSingle).isPresent();
    }

    @Test
    @Order(3)
    void testGetConfiguredService() {
        DomibusConnectorService searchService = new DomibusConnectorService();
        searchService.setService("service1");
        searchService.setServiceType(null);

        Optional<DomibusConnectorService> configuredSingle =
            connectorPModePersistenceService.getConfiguredSingle(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(), searchService);
        assertThat(configuredSingle).isPresent();
    }

    @Test
    @Order(198)
    void updatePModeConfigurationSet_shouldThrow_DueUnsetConnectorStoreUUID() {
        DomibusConnectorPModeSet connectorPModeSet = new DomibusConnectorPModeSet();
        connectorPModeSet.setMessageLaneId(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        connectorPModeSet.setDescription("Example");

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> connectorPModePersistenceService.updatePModeConfigurationSet(connectorPModeSet)
        );
    }

    @Test
    @Order(199)
    void updatePModeConfigurationSet_shouldThrow_DueNotFoundConnectorStore() {
        DomibusConnectorPModeSet connectorPModeSet = new DomibusConnectorPModeSet();
        connectorPModeSet.setMessageLaneId(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        connectorPModeSet.setDescription("Example");

        DomibusConnectorKeystore keystore = new DomibusConnectorKeystore();
        keystore.setUuid("store1123");

        connectorPModeSet.setConnectorstore(keystore);

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> connectorPModePersistenceService.updatePModeConfigurationSet(
                connectorPModeSet)
        );
    }

    // should run last, because it changes db
    @Test
    @Order(200)
    void updatePModeConfigurationSet() {
        DomibusConnectorPModeSet connectorPModeSet = new DomibusConnectorPModeSet();
        connectorPModeSet.setMessageLaneId(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        connectorPModeSet.setDescription("Example");

        DomibusConnectorKeystore keystore = new DomibusConnectorKeystore();
        keystore.setUuid("store1");
        connectorPModeSet.setConnectorstore(keystore);

        connectorPModeSet.getServices().add(DomainEntityCreator.createServiceEPO());
        connectorPModeSet.getActions().add(DomainEntityCreator.createActionFormA());

        DomibusConnectorParty partyDomibusRedResponder =
            DomainEntityCreator.createPartyDomibusRed();
        partyDomibusRedResponder.setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        connectorPModeSet.getParties().add(partyDomibusRedResponder);

        DomibusConnectorParty partyDomibusRedInitiator =
            DomainEntityCreator.createPartyDomibusRed();
        partyDomibusRedInitiator.setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        connectorPModeSet.getParties().add(partyDomibusRedInitiator);

        DomibusConnectorParty partyDomibusBlueResponder =
            DomainEntityCreator.createPartyDomibusBlue();
        partyDomibusBlueResponder.setRoleType(DomibusConnectorParty.PartyRoleType.RESPONDER);
        connectorPModeSet.getParties().add(partyDomibusBlueResponder);

        DomibusConnectorParty partyDomibusBluInitiator =
            DomainEntityCreator.createPartyDomibusBlue();
        partyDomibusBluInitiator.setRoleType(DomibusConnectorParty.PartyRoleType.INITIATOR);
        connectorPModeSet.getParties().add(partyDomibusBluInitiator);

        connectorPModePersistenceService.updatePModeConfigurationSet(connectorPModeSet);
    }

    @Test
    @Order(20)
    void getCurrentPModeSet() {
        Optional<DomibusConnectorPModeSet> currentPModeSet =
            connectorPModePersistenceService.getCurrentPModeSet(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        assertThat(currentPModeSet).isPresent();
    }
}
