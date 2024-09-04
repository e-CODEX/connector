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

import eu.ecodex.connector.persistence.model.PDomibusConnectorLinkConfiguration;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
@SuppressWarnings("squid:S1135")
class DomibusConnectorLinkConfigurationDaoTest {
    @Autowired
    DomibusConnectorLinkConfigurationDao dao;

    @Test
    void testCreateNewLink() {
        PDomibusConnectorLinkConfiguration linkConfig = new PDomibusConnectorLinkConfiguration();

        linkConfig.setConfigName("Config3");

        HashMap<String, String> properties = new HashMap<>();
        properties.put("test", "test");

        dao.save(linkConfig);

        // TODO: check db
    }

    @Test
    void findById() {
        Optional<PDomibusConnectorLinkConfiguration> linkConfig = dao.findById(2L);
    }
}
