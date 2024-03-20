package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.builder.DomibusConnectorPartyBuilder;
import eu.domibus.connector.persistence.dao.DomibusConnectorPartyDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorParty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

//import static eu.domibus.connector.persistence.model.test.util.PersistenceEntityCreator.createPartyPKforPartyAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@Disabled("Currently out of order - mocks must be repaired!")
public class DomibusConnectorPartyPersistenceServiceImplTest {

    @Mock
    DomibusConnectorPartyDao domibusConnectorPartyDao;

    DomibusConnectorPartyPersistenceServiceImpl partyPersistenceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DomibusConnectorPartyPersistenceServiceImpl partyPersistenceServiceImpl = new DomibusConnectorPartyPersistenceServiceImpl();
        partyPersistenceServiceImpl.setPartyDao(domibusConnectorPartyDao);
        partyPersistenceService = partyPersistenceServiceImpl;
    }

    @Test
    public void testGetParty() {

//        Mockito.when(this.domibusConnectorPartyDao.findById(eq(createPartyPKforPartyAT())))
//                .thenReturn(Optional.of(createPartyAT()));

        eu.domibus.connector.domain.model.DomibusConnectorParty party = partyPersistenceService.getParty("AT", "GW");

        assertThat(party).isNotNull();
        assertThat(party.getPartyId()).isEqualTo("AT");
        assertThat(party.getRole()).isEqualTo("GW");
        assertThat(party.getPartyIdType()).isEqualTo("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
    }

    @Test
    public void testGetPartyByPartyId() {

//        Mockito.when(this.domibusConnectorPartyDao.findOneByPartyId(eq("AT")))
//                .thenReturn(createPartyAT());

        eu.domibus.connector.domain.model.DomibusConnectorParty party = partyPersistenceService.getPartyByPartyId("AT");

        assertThat(party).isNotNull();
        assertThat(party.getPartyId()).isEqualTo("AT");
        assertThat(party.getRole()).isEqualTo("GW");
        assertThat(party.getPartyIdType()).isEqualTo("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");

    }


    @Test
    public void mapPartyToDomain() throws Exception {
        PDomibusConnectorParty dbParty = new PDomibusConnectorParty();
        dbParty.setPartyId("partyId");
        dbParty.setPartyIdType("partyIdType");
        dbParty.setRole("role");

        DomibusConnectorParty domibusConnectorParty = partyPersistenceService.mapPartyToDomain(dbParty);

        assertThat(domibusConnectorParty.getPartyId()).isEqualTo("partyId");
        assertThat(domibusConnectorParty.getPartyIdType()).isEqualTo("partyIdType");
        assertThat(domibusConnectorParty.getRole()).isEqualTo("role");
    }

    @Test
    public void mapPartyToDomain_mapNull_shouldRetNull() throws Exception {
        assertThat(partyPersistenceService.mapPartyToDomain(null)).isNull();
    }


    @Test
    public void mapPartyToPersistence() throws Exception {
        DomibusConnectorParty domainParty = DomibusConnectorPartyBuilder.createBuilder()
                .setPartyId("partyId")
                .setRole("role")
                .setPartyIdType("partyIdType")
                .build();

        PDomibusConnectorParty dbParty = partyPersistenceService.mapPartyToPersistence(domainParty);

        assertThat(dbParty.getPartyId()).isEqualTo("partyId");
        assertThat(dbParty.getRole()).isEqualTo("role");
        assertThat(dbParty.getPartyIdType()).isEqualTo("partyIdType");
    }

    @Test
    public void mapPartyToPersistence_mapNull_shouldRetNull() {
        assertThat(partyPersistenceService.mapPartyToPersistence(null)).isNull();
    }

}