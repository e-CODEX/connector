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

import eu.ecodex.connector.domain.enums.ConfigurationSource;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.persistence.dao.CommonPersistenceTest;
import eu.ecodex.connector.persistence.service.DCBusinessDomainPersistenceService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@CommonPersistenceTest
class DCBusinessDomainPersistenceServiceImplTest {
    @Autowired
    DCBusinessDomainPersistenceService businessDomainPersistenceService;

    @Test
    @Order(1)
    void testFindById() {
        Optional<DomibusConnectorBusinessDomain> byId = businessDomainPersistenceService.findById(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        assertThat(byId).isPresent();
        assertThat(byId).get().extracting(DomibusConnectorBusinessDomain::getConfigurationSource)
                        .isEqualTo(ConfigurationSource.DB);
    }

    @Test
    @Order(1)
    void testFindById_notExistant() {
        Optional<DomibusConnectorBusinessDomain> byId = businessDomainPersistenceService.findById(
            new DomibusConnectorBusinessDomain.BusinessDomainId("not_existant"));
        assertThat(byId).isEmpty();
    }

    @Test
    @Order(2)
    void findAll() {
        List<DomibusConnectorBusinessDomain> all = businessDomainPersistenceService.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    @Order(3)
    void testUpdate() {
        Optional<DomibusConnectorBusinessDomain> byId = businessDomainPersistenceService.findById(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());

        DomibusConnectorBusinessDomain domibusConnectorBusinessDomain = byId.get();
        domibusConnectorBusinessDomain.setDescription("Hallo Welt");
        domibusConnectorBusinessDomain.getMessageLaneProperties().put("test1", "test1");
        domibusConnectorBusinessDomain.getMessageLaneProperties().put("prop1", "test2");
        domibusConnectorBusinessDomain.getMessageLaneProperties().put("prop2.prop2", "test3");

        businessDomainPersistenceService.update(domibusConnectorBusinessDomain);

        Optional<DomibusConnectorBusinessDomain> changed =
            businessDomainPersistenceService.findById(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        DomibusConnectorBusinessDomain changedBd = changed.get();

        assertThat(changedBd.getDescription()).isEqualTo("Hallo Welt");
        assertThat(changedBd.getMessageLaneProperties()).hasSize(3);
    }

    @Test
    @Order(4)
    void testUpdateNotExistant_shouldThrow() {
        DomibusConnectorBusinessDomain domibusConnectorBusinessDomain =
            new DomibusConnectorBusinessDomain();
        domibusConnectorBusinessDomain.setId(
            new DomibusConnectorBusinessDomain.BusinessDomainId("doesnotexist"));

        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> businessDomainPersistenceService.update(
                                    domibusConnectorBusinessDomain)
        );
    }
}
