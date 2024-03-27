package eu.domibus.connector.controller.routing;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.common.service.DCBusinessDomainManagerImpl;
import eu.domibus.connector.common.spring.BusinessDomainScopeConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.service.DCBusinessDomainPersistenceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@Import({BusinessDomainScopeConfiguration.class, DCMessageRoutingConfiguration.class})
@SpringBootTest(
        classes = {
                DCBusinessDomainManagerImpl.class,
                ConnectorConfigurationProperties.class,
                BusinessDomainScopeConfiguration.class},
        properties = {
                "connector.routing.enabled=false",
                "connector.routing.backend-rules[0].link-name=backend_alice",
                "connector.routing.backend-rules[0].match-clause=|(&(equals(ServiceName, 'Test'), equals(FromPartyId," +
                        " 'gw01')), equals(FromPartyId, 'gw02'))",
                "connector.routing.backend-rules[1].link-name=backend_abc",
                "connector.routing.backend-rules[1].match-clause=|(equals(ServiceName, 'abc'),equals(ServiceType, " +
                        "'type2'))",
                "connector.routing.backend-rules[2].link-name=backend_abc",
                "connector.routing.backend-rules[2].match-clause=|(equals(ServiceName, 'abc1'),equals(ServiceType, " +
                        "'type2'))",
                "connector.routing.backend-rules[3].link-name=backend_xyz",
                "connector.routing.backend-rules[3].match-clause=|(not(startswith(FinalRecipient, '1')),startswith" +
                        "(ServiceType, '8'))"
        }
)
class DCMessageRoutingConfigurationPropertiesTest {
    @Autowired
    DCMessageRoutingConfigurationProperties props;

    @Autowired
    ApplicationContext ctx;

    @MockBean
    DCBusinessDomainPersistenceService mock;

    @AfterEach
    public void afterEach() {
        CurrentBusinessDomain.setCurrentBusinessDomain(null);
    }

    @Test
    // @Disabled //properties are not loaded?
    void testProps() {
        CurrentBusinessDomain.setCurrentBusinessDomain(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        Map<String, RoutingRule> backendRules = props.getBackendRules();
        String property = ctx.getEnvironment().getProperty("connector.routing.rule.link-name");

        // assertThat(props.getRule()).isNotNull();
        assertThat(props.isEnabled()).isFalse();
        assertThat(backendRules).hasSize(4);
        assertThat(backendRules.get("2").getLinkName()).isEqualTo("backend_abc");
        // assertThat(props.getRuleMap()).hasSize(1);
        // TODO: verify property mapping!!!
    }
}
