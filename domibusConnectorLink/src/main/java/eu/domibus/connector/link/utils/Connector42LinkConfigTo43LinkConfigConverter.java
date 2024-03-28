package eu.domibus.connector.link.utils;

import eu.domibus.connector.common.DomibusConnectorDefaults;
import eu.domibus.connector.domain.enums.LinkMode;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import eu.domibus.connectorplugins.link.gwwspushplugin.WsGatewayPlugin;
import eu.domibus.connectorplugins.link.gwwspushplugin.WsGatewayPluginConfigurationProperties;
import eu.domibus.connectorplugins.link.wsbackendplugin.WsBackendPlugin;
import eu.domibus.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This class will help, load old properties (e.g. Connector 4.2)
 * and convert it into new properties (eg. Connector 4.3)
 * This one will do it for Link Configuration
 */
public class Connector42LinkConfigTo43LinkConfigConverter {
    public static final String GWL_GW_ADDRESS_OLD_PROP_NAME = "connector.gatewaylink.ws.submissionEndpointAddress";
    public static final String GWL_ENCRYPT_ALIAS_OLD_PROP_NAME = "connector.gatewaylink.ws.encrypt-alias";
    public static final String GWL_TRUST_STORE_PW_OLD_PROP_NAME = "connector.gatewaylink.ws.trust-store.password";
    public static final String GWL_TRUST_STORE_PATH_OLD_PROP_NAME = "connector.gatewaylink.ws.trust-store.path";
    public static final String GWL_KEY_STORE_PW_OLD_PROP_NAM = "connector.gatewaylink.ws.key-store.password";
    public static final String GWL_KEY_STORE_PATH_OLD_PROP_NAME = "connector.gatewaylink.ws.key-store.path";
    public static final String GWL_PRIVATE_KEY_ALIAS_OLD_PROP_NAME = "connector.gatewaylink.ws.private-key.alias";
    public static final String GWL_PRIVATE_KEY_PW_OLD_PROP_NAME = "connector.gatewaylink.ws.private-key.password";

    public static final String BACKEND_TRUST_STORE_PW_OLD_PROP_NAME = "connector.backend.ws.trust.trust-store.password";
    public static final String BACKEND_TRUST_STORE_PATH_OLD_PROP_NAME = "connector.backend.ws.trust.trust-store.path";
    public static final String BACKEND_KEY_STORE_PW_OLD_PROP_NAM = "connector.backend.ws.key.key-store.password";
    public static final String BACKEND_KEY_STORE_PATH_OLD_PROP_NAME = "connector.backend.ws.key.key-store.path";
    public static final String BACKEND_PRIVATE_KEY_ALIAS_OLD_PROP_NAME = "connector.backend.ws.key.private-key.alias";
    public static final String BACKEND_PRIVATE_KEY_PW_OLD_PROP_NAME = "connector.backend.ws.key.private-key.password";

    private final Properties oldProperties;
    private final BeanToPropertyMapConverter beanToPropertyMapConverter;
    private final JdbcTemplate jdbcTemplate;

    public Connector42LinkConfigTo43LinkConfigConverter(
            BeanToPropertyMapConverter beanToPropertyMapConverter,
            JdbcTemplate jdbcTemplate,
            Properties oldProperties) {
        this.beanToPropertyMapConverter = beanToPropertyMapConverter;
        this.jdbcTemplate = jdbcTemplate;
        this.oldProperties = oldProperties;
    }

    public List<DomibusConnectorLinkPartner> getGwPartner() {
        DomibusConnectorLinkConfiguration lnkConfig = getGwLinkConfiguration();
        DomibusConnectorLinkPartner linkPartner = new DomibusConnectorLinkPartner();
        linkPartner.setLinkConfiguration(lnkConfig);
        linkPartner.setLinkPartnerName(new DomibusConnectorLinkPartner.LinkPartnerName(DomibusConnectorDefaults.DEFAULT_GATEWAY_NAME));
        linkPartner.setSendLinkMode(LinkMode.PUSH);
        linkPartner.setRcvLinkMode(LinkMode.PASSIVE);
        linkPartner.setLinkType(LinkType.GATEWAY);
        linkPartner.setEnabled(true);
        linkPartner.setDescription("Imported GW Link Partner Config");
        return Stream.of(linkPartner).collect(Collectors.toList());
    }

    private DomibusConnectorLinkConfiguration getGwLinkConfiguration() {
        DomibusConnectorLinkConfiguration domibusConnectorLinkConfiguration = new DomibusConnectorLinkConfiguration();
        domibusConnectorLinkConfiguration.setLinkImpl(WsGatewayPlugin.IMPL_NAME);
        domibusConnectorLinkConfiguration.setConfigName(new DomibusConnectorLinkConfiguration.LinkConfigName(
                "Imported_4.2_GWConfig"));
        domibusConnectorLinkConfiguration.setProperties(beanToPropertyMapConverter.readBeanPropertiesToMap(
                convertGwLinkProperties(),
                ""
        ));
        return domibusConnectorLinkConfiguration;
    }

    private WsGatewayPluginConfigurationProperties convertGwLinkProperties() {
        WsGatewayPluginConfigurationProperties wsGatewayPluginConfigurationProperties =
                new WsGatewayPluginConfigurationProperties();
        wsGatewayPluginConfigurationProperties.setGwAddress(getOldRequiredProperty(GWL_GW_ADDRESS_OLD_PROP_NAME));
        wsGatewayPluginConfigurationProperties.setCxfLoggingEnabled(false);

        CxfTrustKeyStoreConfigurationProperties cxfProps = new CxfTrustKeyStoreConfigurationProperties();
        wsGatewayPluginConfigurationProperties.setSoap(cxfProps);
        cxfProps.setEncryptAlias(getOldRequiredProperty(GWL_ENCRYPT_ALIAS_OLD_PROP_NAME));

        StoreConfigurationProperties trustStore = new StoreConfigurationProperties();
        trustStore.setPassword(getOldRequiredProperty(GWL_TRUST_STORE_PW_OLD_PROP_NAME));
        trustStore.setPath(getOldRequiredProperty(GWL_TRUST_STORE_PATH_OLD_PROP_NAME));
        trustStore.setType("JKS");
        cxfProps.setTrustStore(trustStore);

        StoreConfigurationProperties keyStore = new StoreConfigurationProperties();
        keyStore.setType("JKS");
        keyStore.setPassword(getOldRequiredProperty(GWL_KEY_STORE_PW_OLD_PROP_NAM));
        keyStore.setPath(getOldRequiredProperty(GWL_KEY_STORE_PATH_OLD_PROP_NAME));
        cxfProps.setKeyStore(keyStore);

        KeyConfigurationProperties privateKeyConfig = new KeyConfigurationProperties();
        privateKeyConfig.setPassword(getOldRequiredProperty(GWL_PRIVATE_KEY_PW_OLD_PROP_NAME));
        privateKeyConfig.setAlias(getOldRequiredProperty(GWL_PRIVATE_KEY_ALIAS_OLD_PROP_NAME));
        cxfProps.setPrivateKey(privateKeyConfig);
        return wsGatewayPluginConfigurationProperties;
    }

    private WsBackendPluginConfigurationProperties convertBackendLinkProperties() {
        WsBackendPluginConfigurationProperties wsBackendPluginConfigurationProperties =
                new WsBackendPluginConfigurationProperties();
        wsBackendPluginConfigurationProperties.setCxfLoggingEnabled(false);

        CxfTrustKeyStoreConfigurationProperties cxfProps = new CxfTrustKeyStoreConfigurationProperties();
        wsBackendPluginConfigurationProperties.setSoap(cxfProps);

        StoreConfigurationProperties trustStore = new StoreConfigurationProperties();
        cxfProps.setTrustStore(trustStore);
        trustStore.setPassword(getOldRequiredProperty(BACKEND_TRUST_STORE_PW_OLD_PROP_NAME));
        trustStore.setPath(getOldRequiredProperty(BACKEND_TRUST_STORE_PATH_OLD_PROP_NAME));
        trustStore.setType("JKS");

        StoreConfigurationProperties keyStore = new StoreConfigurationProperties();
        cxfProps.setKeyStore(keyStore);
        keyStore.setType("JKS");
        keyStore.setPassword(getOldRequiredProperty(BACKEND_KEY_STORE_PW_OLD_PROP_NAM));
        keyStore.setPath(getOldRequiredProperty(BACKEND_KEY_STORE_PATH_OLD_PROP_NAME));

        KeyConfigurationProperties privateKeyConfig = new KeyConfigurationProperties();
        cxfProps.setPrivateKey(privateKeyConfig);
        privateKeyConfig.setPassword(getOldRequiredProperty(BACKEND_PRIVATE_KEY_PW_OLD_PROP_NAME));
        privateKeyConfig.setAlias(getOldRequiredProperty(BACKEND_PRIVATE_KEY_ALIAS_OLD_PROP_NAME));

        return wsBackendPluginConfigurationProperties;
    }

    public List<DomibusConnectorLinkPartner> getBackendPartners() {

        DomibusConnectorLinkConfiguration lnkConfig = getBackendLinkConfiguration();

        List<DomibusConnectorLinkPartner> domibusConnectorLinkPartners = loadBackendsFromDb().stream().peek(lp -> {
            lp.setLinkConfiguration(lnkConfig);
            lp.setDescription("imported by import 4.2 old config");
        }).collect(Collectors.toList());

        return domibusConnectorLinkPartners;
    }

    private DomibusConnectorLinkConfiguration getBackendLinkConfiguration() {
        DomibusConnectorLinkConfiguration domibusConnectorLinkConfiguration = new DomibusConnectorLinkConfiguration();
        domibusConnectorLinkConfiguration.setLinkImpl(WsBackendPlugin.IMPL_NAME);
        domibusConnectorLinkConfiguration.setConfigName(new DomibusConnectorLinkConfiguration.LinkConfigName(
                "Imported_4.2_BackendConfig"));
        domibusConnectorLinkConfiguration.setProperties(beanToPropertyMapConverter.readBeanPropertiesToMap(
                convertBackendLinkProperties(),
                ""
        ));
        return domibusConnectorLinkConfiguration;
    }

    private List<DomibusConnectorLinkPartner> loadBackendsFromDb() {
        return jdbcTemplate.query(
                "Select BACKEND_NAME, BACKEND_KEY_ALIAS, BACKEND_PUSH_ADDRESS, BACKEND_DEFAULT, BACKEND_ENABLED, " +
                        "BACKEND_DESCRIPTION " + " FROM DOMIBUS_CONNECTOR_BACKEND_INFO",
                (rs, rowNum) -> {

                    DomibusConnectorLinkPartner p = new DomibusConnectorLinkPartner();
                    p.setDescription(rs.getString("BACKEND_DESCRIPTION"));
                    p.setEnabled(rs.getBoolean("BACKEND_ENABLED"));
                    p.setLinkPartnerName(new DomibusConnectorLinkPartner.LinkPartnerName(rs.getString("BACKEND_NAME")));
                    p.setRcvLinkMode(LinkMode.PASSIVE);
                    p.setLinkType(LinkType.BACKEND);

                    Map<String, String> props = new HashMap<>();
                    p.setProperties(props);

                    props.put("encryption-alias", rs.getString("BACKEND_KEY_ALIAS"));
                    props.put("certificate-dn", rs.getString("BACKEND_NAME"));

                    String pushAddress = rs.getString("BACKEND_PUSH_ADDRESS");
                    if (StringUtils.hasText(pushAddress)) {
                        p.setSendLinkMode(LinkMode.PUSH);
                        props.put("push-address", pushAddress);
                    } else {
                        p.setSendLinkMode(LinkMode.PULL);
                    }
                    return p;
                }
        );
    }

    private String getOldRequiredProperty(String oldPropName) {
        if (oldProperties.containsKey(oldPropName)) {
            return oldProperties.get(oldPropName).toString();
        } else {
            throw new IllegalArgumentException(String.format(
                    "The provided 'old' properties does not contain the property [%s], which is required!",
                    oldPropName
            ));
        }
    }
}
