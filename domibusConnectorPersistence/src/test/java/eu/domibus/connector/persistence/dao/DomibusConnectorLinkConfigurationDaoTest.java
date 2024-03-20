package eu.domibus.connector.persistence.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import eu.domibus.connector.persistence.model.PDomibusConnectorLinkConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Optional;

import static com.github.database.rider.core.api.dataset.SeedStrategy.CLEAN_INSERT;

//@CommonPersistenceTest
//@DataSet(value = "/database/testdata/dbunit/DomibusConnectorLinkConfiguration.xml", strategy = CLEAN_INSERT)
@Disabled
class DomibusConnectorLinkConfigurationDaoTest {

    @Autowired
    DomibusConnectorLinkConfigurationDao dao;

    @Test
    void testCreateNewLink() {
        PDomibusConnectorLinkConfiguration linkConfig = new PDomibusConnectorLinkConfiguration();

        linkConfig.setConfigName("Config3");

        HashMap<String, String> props = new HashMap<>();
        props.put("test","test");

        dao.save(linkConfig);

        //TODO: check db
    }

    @Test
    public void findById() {
        Optional<PDomibusConnectorLinkConfiguration> linkConfig = dao.findById(2l);
    }


}