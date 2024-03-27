package eu.domibus.connector.common.service;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.persistence.service.DCBusinessDomainPersistenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = {DCBusinessDomainManagerImpl.class, ConnectorConfigurationProperties.class})
class DCBusinessDomainManagerImplTest {
    @Autowired
    DCBusinessDomainManagerImpl dcBusinessDomainManager;
    @MockBean
    DCBusinessDomainPersistenceService dcBusinessDomainPersistenceService;

    @Test
    void testUpdateProperties() {
        Map<String, String> currentProperties = new HashMap<>();
        currentProperties.put("prop1", "value1");
        currentProperties.put("prop2", "value2");
        currentProperties.put("prop3", "value3");

        Map<String, String> changedProperties = new HashMap<>();
        changedProperties.put("prop1", null); // should be removed
        changedProperties.put("prop2", "value_changed"); // should be changed

        Map<String, String> stringStringMap =
                dcBusinessDomainManager.updateChangedProperties(currentProperties, changedProperties);

        Map<String, String> expectedResultMap = new HashMap<>();
        expectedResultMap.put("prop2", "value_changed");
        expectedResultMap.put("prop3", "value3");

        assertThat(stringStringMap).containsExactlyEntriesOf(expectedResultMap);
    }
}
