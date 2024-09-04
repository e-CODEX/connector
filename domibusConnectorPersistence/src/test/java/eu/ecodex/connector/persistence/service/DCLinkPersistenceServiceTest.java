/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.enums.LinkMode;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.persistence.dao.CommonPersistenceTest;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

@CommonPersistenceTest
class DCLinkPersistenceServiceTest {
    @Autowired
    DCLinkPersistenceService dcLinkPersistenceService;
    @Autowired
    TransactionTemplate txTemplate;

    @Test
    @Disabled("defect")
    void testCreateLinkPartner() {
        HashMap<String, String> linkConfigProps = new HashMap<>();
        linkConfigProps.put("p1", "abc");
        linkConfigProps.put("p2", "abc2");
        DomibusConnectorLinkConfiguration linkConfiguration =
            new DomibusConnectorLinkConfiguration();
        linkConfiguration.setConfigName(
            new DomibusConnectorLinkConfiguration.LinkConfigName("config1"));
        linkConfiguration.setLinkImpl("impl1");
        linkConfiguration.setProperties(linkConfigProps);

        DomibusConnectorLinkPartner linkPartner = new DomibusConnectorLinkPartner();
        linkPartner.setRcvLinkMode(LinkMode.PULL);
        linkPartner.setSendLinkMode(LinkMode.PUSH);
        linkPartner.setLinkPartnerName(new DomibusConnectorLinkPartner.LinkPartnerName("partner1"));
        linkPartner.setLinkConfiguration(linkConfiguration);
        linkPartner.getProperties().put("p1", "a1");
        linkPartner.getProperties().put("p2", "a1123");

        // persist
        txTemplate.executeWithoutResult(t -> dcLinkPersistenceService.addLinkPartner(linkPartner));

        // load partner from db and check
        Optional<DomibusConnectorLinkPartner> partner1 = dcLinkPersistenceService.getLinkPartner(
            new DomibusConnectorLinkPartner.LinkPartnerName("partner1"));
        assertThat(partner1).isPresent();
        DomibusConnectorLinkPartner linkPartner1 = partner1.get();
        assertThat(linkPartner1.getProperties()).hasSize(2);

        assertThat(linkPartner1.getLinkConfiguration().getProperties()).hasSize(2);
    }
}
