package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorLinkConfiguration;
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
