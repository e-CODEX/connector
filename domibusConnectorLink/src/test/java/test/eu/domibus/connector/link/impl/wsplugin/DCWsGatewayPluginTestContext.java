package test.eu.domibus.connector.link.impl.wsplugin;

import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.persistence.dao.DomibusConnectorLinkConfigurationDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorLinkPartnerDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorLinkConfiguration;
import eu.domibus.connector.persistence.model.PDomibusConnectorLinkPartner;
import eu.domibus.connector.persistence.service.DCLinkPersistenceService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;


@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@Import({LinkTestContext.class, CxfAutoConfiguration.class, DCWsGatewayPlugin.class})
@Profile(DCWsGatewayPluginTestContext.WS_TEST_PROFILE)
public class DCWsGatewayPluginTestContext {
    public static final String WS_TEST_PROFILE = "ws-test";
    public static final String WS_GATEWAY_TEST_PROFILE = "ws-gw-test";

    @Autowired
    DCLinkPersistenceService linkPersistenceService;

    public static PDomibusConnectorLinkConfiguration getWsGatewayLinkConfig() {
        PDomibusConnectorLinkConfiguration linkConfig = new PDomibusConnectorLinkConfiguration();
        //        linkConfig.setLinkImpl(DCWsGatewayPlugin.IMPL_NAME);
        linkConfig.setConfigName("wsgateway");

        HashMap<String, String> props = new HashMap<>();

        props.put("link.wsgatewayplugin.soap.key-store.path", "classpath:/keystores/connector-gwlink-keystore.jks");
        props.put("link.wsgatewayplugin.soap.key-store.password", "12345");
        props.put("link.wsgatewayplugin.soap.private-key.alias", "connector");
        props.put("link.wsgatewayplugin.soap.private-key.password", "12345");

        props.put("link.wsgatewayplugin.soap.trust-store.path", "classpath:/keystores/connector-gwlink-truststore.jks");
        props.put("link.wsgatewayplugin.soap.trust-store.password", "12345");

        linkConfig.setProperties(props);

        //                "connector.link.autostart=false",
        //                "link.wsgatewayplugin.soap.key-store.path=classpath:/keystores/connector-gwlink-keystore.jks",
        //                "link.wsgatewayplugin.soap.key-store.password=12345",
        //                "link.wsgatewayplugin.soap.private-key.alias=connector",
        //                "link.wsgatewayplugin.soap.private-key.password=12345",
        //                "link.wsgatewayplugin.soap.trust-store.password=12345",
        //                "link.wsgatewayplugin.soap.trust-store
        //                .path=classpath:/keystores/connector-gwlink-truststore.jks",

        return linkConfig;
    }

    public static PDomibusConnectorLinkPartner getWsGwLinkInfo() {
        PDomibusConnectorLinkPartner linkPartner1 = new PDomibusConnectorLinkPartner();
        String linkName1 = "cn=gw";

        linkPartner1.setLinkName(linkName1);
        linkPartner1.setLinkType(LinkType.GATEWAY);

        linkPartner1.setLinkConfiguration(getWsGatewayLinkConfig());
        linkPartner1.setEnabled(true);

        HashMap<String, String> props = new HashMap<>();
        props.put("name.push-address", "push");
        //        props.put("link.wsgate")

        linkPartner1.setProperties(props);

        return linkPartner1;
    }

    @Bean
    @Primary
    @Profile(WS_TEST_PROFILE)
    public DomibusConnectorLinkPartnerDao domibusConnectorWsLinkPartnerDao() {
        DomibusConnectorLinkPartnerDao dao = Mockito.mock(DomibusConnectorLinkPartnerDao.class);
        Mockito.when(dao.findAllByEnabledIsTrue())
               .thenReturn(Stream.of(getWsGwLinkInfo()).collect(Collectors.toList()));
        Mockito.when(dao.findOneByLinkName("cn=gw"))
               .thenReturn(Optional.of(getWsGwLinkInfo()));

        return dao;
    }

    @Bean
    @Primary
    @Profile(WS_TEST_PROFILE)
    public DomibusConnectorLinkConfigurationDao domibusConnectorWsLinkConfigDao() {
        DomibusConnectorLinkConfigurationDao dao = Mockito.mock(DomibusConnectorLinkConfigurationDao.class);
        Mockito.when(dao.getOneByConfigName(eq("wsgateway")))
               .thenReturn(Optional.of(getWsGatewayLinkConfig()));

        return dao;
    }

    @Bean
    @Profile(WS_TEST_PROFILE)
    public DomibusConnectorLinkConfiguration domibusConnectorLinkConfiguration() {
        return linkPersistenceService
                .getLinkConfiguration(new DomibusConnectorLinkConfiguration.LinkConfigName("wsgateway")).get();
    }
}
