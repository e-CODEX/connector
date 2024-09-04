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

import eu.ecodex.connector.domain.model.DomibusConnectorParty;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorPartyBuilder;
import eu.ecodex.connector.persistence.dao.DomibusConnectorPartyDao;
import eu.ecodex.connector.persistence.model.PDomibusConnectorParty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Disabled("Currently out of order - mocks must be repaired!")
class DomibusConnectorPartyPersistenceServiceImplTest {
    @Mock
    DomibusConnectorPartyDao domibusConnectorPartyDao;
    DomibusConnectorPartyPersistenceServiceImpl partyPersistenceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DomibusConnectorPartyPersistenceServiceImpl partyPersistenceServiceImpl =
            new DomibusConnectorPartyPersistenceServiceImpl();
        partyPersistenceServiceImpl.setPartyDao(domibusConnectorPartyDao);
        partyPersistenceService = partyPersistenceServiceImpl;
    }

    @Test
    void testGetParty() {
        DomibusConnectorParty party =
            partyPersistenceService.getParty("AT", "GW");

        assertThat(party).isNotNull();
        assertThat(party.getPartyId()).isEqualTo("AT");
        assertThat(party.getRole()).isEqualTo("GW");
        assertThat(party.getPartyIdType()).isEqualTo(
            "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
    }

    @Test
    void testGetPartyByPartyId() {
        DomibusConnectorParty party =
            partyPersistenceService.getPartyByPartyId("AT");

        assertThat(party).isNotNull();
        assertThat(party.getPartyId()).isEqualTo("AT");
        assertThat(party.getRole()).isEqualTo("GW");
        assertThat(party.getPartyIdType()).isEqualTo(
            "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
    }

    @Test
    void mapPartyToDomain() {
        PDomibusConnectorParty dbParty = new PDomibusConnectorParty();
        dbParty.setPartyId("partyId");
        dbParty.setPartyIdType("partyIdType");
        dbParty.setRole("role");

        DomibusConnectorParty domibusConnectorParty =
            partyPersistenceService.mapPartyToDomain(dbParty);

        assertThat(domibusConnectorParty.getPartyId()).isEqualTo("partyId");
        assertThat(domibusConnectorParty.getPartyIdType()).isEqualTo("partyIdType");
        assertThat(domibusConnectorParty.getRole()).isEqualTo("role");
    }

    @Test
    void mapPartyToDomain_mapNull_shouldRetNull() {
        assertThat(partyPersistenceService.mapPartyToDomain(null)).isNull();
    }

    @Test
    void mapPartyToPersistence() {
        DomibusConnectorParty domainParty =
            DomibusConnectorPartyBuilder.createBuilder()
                                        .setPartyId("partyId")
                                        .setRole("role")
                                        .setPartyIdType(
                                            "partyIdType")
                                        .build();

        PDomibusConnectorParty dbParty = partyPersistenceService.mapPartyToPersistence(domainParty);

        assertThat(dbParty.getPartyId()).isEqualTo("partyId");
        assertThat(dbParty.getRole()).isEqualTo("role");
        assertThat(dbParty.getPartyIdType()).isEqualTo("partyIdType");
    }

    @Test
    void mapPartyToPersistence_mapNull_shouldRetNull() {
        assertThat(partyPersistenceService.mapPartyToPersistence(null)).isNull();
    }
}
