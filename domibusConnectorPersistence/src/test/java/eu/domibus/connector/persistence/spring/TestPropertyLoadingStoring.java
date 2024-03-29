package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.common.service.ConfigurationPropertyLoaderServiceImpl;
import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.common.service.DCBusinessDomainManager;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles({"test", "db_h2", "storage-db", STORAGE_DB_PROFILE_NAME, "prop-test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestPropertyLoadingStoring {
    @Autowired
    ConfigurationPropertyLoaderServiceImpl configurationPropertyLoaderService;
    @Autowired
    DCBusinessDomainManager dcBusinessDomainManager;
    @Autowired
    TestProperties testProperties;

    @Test
    @Order(1)
    void testPropLoad() {
        try {
            DomibusConnectorBusinessDomain.BusinessDomainId d1 =
                    DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
            CurrentBusinessDomain.setCurrentBusinessDomain(d1);

            TestProperties changed1 = configurationPropertyLoaderService.loadConfiguration(d1, TestProperties.class);
            changed1.setT1("abc1");
            changed1.setCamelCaseProperty("camelCase");

            assertThat(testProperties.getT1()).isEqualTo("test1");

            configurationPropertyLoaderService.updateConfiguration(d1, changed1);

            assertThat(testProperties.getT1()).isEqualTo("abc1");
            assertThat(testProperties.getCamelCaseProperty()).isEqualTo("camelCase");
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }

    @Test
    @Order(3)
    void testPropLoadDifferentBusinessDomain() {
        DomibusConnectorBusinessDomain b = new DomibusConnectorBusinessDomain();
        DomibusConnectorBusinessDomain.BusinessDomainId bid = new DomibusConnectorBusinessDomain.BusinessDomainId("b2");
        b.setId(bid);
        dcBusinessDomainManager.createBusinessDomain(b);

        try {
            DomibusConnectorBusinessDomain.BusinessDomainId defaultDomain =
                    DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
            DomibusConnectorBusinessDomain.BusinessDomainId testDomain2 = bid;

            // update properties in default domain
            TestProperties changed1 =
                    configurationPropertyLoaderService.loadConfiguration(defaultDomain, TestProperties.class);
            changed1.setT1("defaultDomain");
            changed1.setCamelCaseProperty("defaultDomain");
            configurationPropertyLoaderService.updateConfiguration(defaultDomain, changed1);

            // update properties in testdomain
            TestProperties changed2 =
                    configurationPropertyLoaderService.loadConfiguration(testDomain2, TestProperties.class);
            changed2.setT1("testDomain2");
            changed2.setCamelCaseProperty("testDomain2");
            configurationPropertyLoaderService.updateConfiguration(testDomain2, changed2);

            // verify properties in test domain
            CurrentBusinessDomain.setCurrentBusinessDomain(testDomain2);
            assertThat(testProperties.getT1()).isEqualTo("testDomain2");
            assertThat(testProperties.getCamelCaseProperty()).isEqualTo("testDomain2");

            // verify properties in default domain
            CurrentBusinessDomain.setCurrentBusinessDomain(defaultDomain);
            assertThat(testProperties.getT1()).isEqualTo("defaultDomain");
            assertThat(testProperties.getCamelCaseProperty()).isEqualTo("defaultDomain");
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }

    @SpringBootApplication(scanBasePackages = "eu.domibus.connector")
    public static class TestContext {

    }
}
