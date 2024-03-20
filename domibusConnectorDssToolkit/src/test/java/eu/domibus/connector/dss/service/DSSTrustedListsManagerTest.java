package eu.domibus.connector.dss.service;

import eu.domibus.connector.common.SpringProfiles;
import eu.domibus.connector.common.configuration.ConnectorConverterAutoConfiguration;
import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.dss.configuration.BasicDssConfiguration;
import eu.ecodex.utils.spring.converter.ConverterAutoConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {BasicDssConfiguration.class,
        DSSTrustedListsManager.class,
        ConverterAutoConfiguration.class,
        ConnectorConverterAutoConfiguration.class,
        DCKeyStoreService.class
},
    properties = "connector.dss.tlCacheLocation=file:./target/tlcache/"

)
@ActiveProfiles({"seclib-test", SpringProfiles.TEST, "dss-tl-test" })
class DSSTrustedListsManagerTest {


    @Autowired
    DSSTrustedListsManager dssTrustedListsManager;

    @Test
    public void testStartup() {
        Assertions.assertThat(dssTrustedListsManager.getAllSourceNames()).hasSize(2);
        Assertions.assertThat(dssTrustedListsManager.getCertificateSource("list1")).isPresent();
    }


}