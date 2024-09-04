/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.persistence.model.PDomibusConnectorLinkPartner;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

@SuppressWarnings("squid:S1135")
@Disabled("Tests is failing on win10 maven build")
class DomibusConnectorLinkPartnerDaoTest {
    @Autowired
    DomibusConnectorLinkPartnerDao dao;

    @Test
    void testCreateNewLink() {
        PDomibusConnectorLinkPartner linkInfo = new PDomibusConnectorLinkPartner();
        linkInfo.setDescription("test description");
        linkInfo.setLinkName("name");
        //
        HashMap<String, String> props = new HashMap<>();
        props.put("test", "test");
        linkInfo.setProperties(props);

        dao.save(linkInfo);

        // TODO: check db
    }

    @Test
    void findByExample() {
        PDomibusConnectorLinkPartner linkPartner = new PDomibusConnectorLinkPartner();
        linkPartner.setLinkType(LinkType.GATEWAY);
        Example<PDomibusConnectorLinkPartner> example = Example.of(linkPartner);

        List<PDomibusConnectorLinkPartner> all = dao.findAll(example);

        assertThat(all).hasSize(1);
    }

    @Test
    void findOneBackendByLinkNameAndEnabledIsTrue() {
        Optional<PDomibusConnectorLinkPartner> test =
            dao.findOneBackendByLinkNameAndEnabledIsTrue("test");
        assertThat(test).isNotEmpty();
    }

    @Test
    void findOneByLinkName() {
        Optional<PDomibusConnectorLinkPartner> test2 = dao.findOneByLinkName("test2");
        assertThat(test2).isNotEmpty();

        assertThat(test2.get().getProperties())
            .as("must have property entry with [test=test]")
            .hasEntrySatisfying("test", k -> k.equals("test"));
    }

    @Test
    void findHighestId() {
        Long highestId = dao.findHighestId();
        assertThat(highestId).isEqualTo(1001);
    }
}
