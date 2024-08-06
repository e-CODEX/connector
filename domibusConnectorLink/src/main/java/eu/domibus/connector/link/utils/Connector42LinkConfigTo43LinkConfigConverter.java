/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

/**
 * This class will help, load old properties (e.g. Connector 4.2) and convert it into new properties
 * (e.g. Connector 4.3) This one will do it for Link Configuration
 */
public class Connector42LinkConfigTo43LinkConfigConverter {
    public static final String GWL_GW_ADDRESS_OLD_PROP_NAME =
        "connector.gatewaylink.ws.submissionEndpointAddress";
    public static final String GWL_ENCRYPT_ALIAS_OLD_PROP_NAME =
        "connector.gatewaylink.ws.encrypt-alias";
    public static final String GWL_TRUST_STORE_PW_OLD_PROP_NAME =
        "connector.gatewaylink.ws.trust-store.password";
    public static final String GWL_TRUST_STORE_PATH_OLD_PROP_NAME =
        "connector.gatewaylink.ws.trust-store.path";
    public static final String GWL_KEY_STORE_PW_OLD_PROP_NAM =
        "connector.gatewaylink.ws.key-store.password";
    public static final String GWL_KEY_STORE_PATH_OLD_PROP_NAME =
        "connector.gatewaylink.ws.key-store.path";
    public static final String GWL_PRIVATE_KEY_ALIAS_OLD_PROP_NAME =
        "connector.gatewaylink.ws.private-key.alias";
    public static final String GWL_PRIVATE_KEY_PW_OLD_PROP_NAME =
        "connector.gatewaylink.ws.private-key.password";
    public static final String BACKEND_TRUST_STORE_PW_OLD_PROP_NAME =
        "connector.backend.ws.trust.trust-store.password";
    public static final String BACKEND_TRUST_STORE_PATH_OLD_PROP_NAME =
        "connector.backend.ws.trust.trust-store.path";
    public static final String BACKEND_KEY_STORE_PW_OLD_PROP_NAM =
        "connector.backend.ws.key.key-store.password";
    public static final String BACKEND_KEY_STORE_PATH_OLD_PROP_NAME =
        "connector.backend.ws.key.key-store.path";
    public static final String BACKEND_PRIVATE_KEY_ALIAS_OLD_PROP_NAME =
        "connector.backend.ws.key.private-key.alias";
    public static final String BACKEND_PRIVATE_KEY_PW_OLD_PROP_NAME =
        "connector.backend.ws.key.private-key.password";
    private final Properties oldProperties;
    private final BeanToPropertyMapConverter beanToPropertyMapConverter;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Converts the configuration properties of Connector 4.2 link to Connector 4.3 link.
     */
    public Connector42LinkConfigTo43LinkConfigConverter(
        BeanToPropertyMapConverter beanToPropertyMapConverter,
        JdbcTemplate jdbcTemplate,
        Properties oldProperties) {
        this.beanToPropertyMapConverter = beanToPropertyMapConverter;
        this.jdbcTemplate = jdbcTemplate;
        this.oldProperties = oldProperties;
    }

    /**
     * Retrieves the Gateway partner for the Domibus Connector.
     *
     * @return The list of DomibusConnectorLinkPartner representing the Gateway partner.
     */
    public List<DomibusConnectorLinkPartner> getGwPartner() {
        DomibusConnectorLinkConfiguration lnkConfig = getGwLinkConfiguration();
        var linkPartner = new DomibusConnectorLinkPartner();
        linkPartner.setLinkConfiguration(lnkConfig);
        linkPartner.setLinkPartnerName(new DomibusConnectorLinkPartner.LinkPartnerName(
            DomibusConnectorDefaults.DEFAULT_GATEWAY_NAME));
        linkPartner.setSendLinkMode(LinkMode.PUSH);
        linkPartner.setRcvLinkMode(LinkMode.PASSIVE);
        linkPartner.setLinkType(LinkType.GATEWAY);
        linkPartner.setEnabled(true);
        linkPartner.setDescription("Imported GW Link Partner Config");
        return Stream.of(linkPartner).toList();
    }

    /**
     * Retrieves the Gateway link configuration for the Domibus Connector.
     *
     * @return The DomibusConnectorLinkConfiguration representing the Gateway link configuration.
     */
    private DomibusConnectorLinkConfiguration getGwLinkConfiguration() {
        var domibusConnectorLinkConfiguration = new DomibusConnectorLinkConfiguration();
        domibusConnectorLinkConfiguration.setLinkImpl(WsGatewayPlugin.IMPL_NAME);
        domibusConnectorLinkConfiguration.setConfigName(
            new DomibusConnectorLinkConfiguration.LinkConfigName("Imported_4.2_GWConfig"));
        domibusConnectorLinkConfiguration.setProperties(
            beanToPropertyMapConverter.readBeanPropertiesToMap(convertGwLinkProperties(), ""));
        return domibusConnectorLinkConfiguration;
    }

    /**
     * Converts the Gateway link properties for the Domibus Connector.
     *
     * @return The converted WsGatewayPluginConfigurationProperties object.
     */
    private WsGatewayPluginConfigurationProperties convertGwLinkProperties() {
        var wsGatewayPluginConfigurationProperties = new WsGatewayPluginConfigurationProperties();
        wsGatewayPluginConfigurationProperties.setGwAddress(
            getOldRequiredProperty(GWL_GW_ADDRESS_OLD_PROP_NAME));
        wsGatewayPluginConfigurationProperties.setCxfLoggingEnabled(false);

        var cxfProps = new CxfTrustKeyStoreConfigurationProperties();
        wsGatewayPluginConfigurationProperties.setSoap(cxfProps);
        cxfProps.setEncryptAlias(getOldRequiredProperty(GWL_ENCRYPT_ALIAS_OLD_PROP_NAME));

        var trustStore = new StoreConfigurationProperties();
        trustStore.setPassword(getOldRequiredProperty(GWL_TRUST_STORE_PW_OLD_PROP_NAME));
        trustStore.setPath(getOldRequiredProperty(GWL_TRUST_STORE_PATH_OLD_PROP_NAME));
        trustStore.setType("JKS");
        cxfProps.setTrustStore(trustStore);

        var keyStore = new StoreConfigurationProperties();
        keyStore.setType("JKS");
        keyStore.setPassword(getOldRequiredProperty(GWL_KEY_STORE_PW_OLD_PROP_NAM));
        keyStore.setPath(getOldRequiredProperty(GWL_KEY_STORE_PATH_OLD_PROP_NAME));
        cxfProps.setKeyStore(keyStore);

        var privateKeyConfig = new KeyConfigurationProperties();
        privateKeyConfig.setPassword(getOldRequiredProperty(GWL_PRIVATE_KEY_PW_OLD_PROP_NAME));
        privateKeyConfig.setAlias(getOldRequiredProperty(GWL_PRIVATE_KEY_ALIAS_OLD_PROP_NAME));
        cxfProps.setPrivateKey(privateKeyConfig);
        return wsGatewayPluginConfigurationProperties;
    }

    /**
     * Converts the backend link properties to a {@link WsBackendPluginConfigurationProperties}
     * object.
     *
     * @return The converted {@link WsBackendPluginConfigurationProperties} object.
     */
    private WsBackendPluginConfigurationProperties convertBackendLinkProperties() {
        var wsBackendPluginConfigurationProperties = new WsBackendPluginConfigurationProperties();
        wsBackendPluginConfigurationProperties.setCxfLoggingEnabled(false);

        var cxfProps = new CxfTrustKeyStoreConfigurationProperties();
        wsBackendPluginConfigurationProperties.setSoap(cxfProps);

        var trustStore = new StoreConfigurationProperties();
        cxfProps.setTrustStore(trustStore);
        trustStore.setPassword(getOldRequiredProperty(BACKEND_TRUST_STORE_PW_OLD_PROP_NAME));
        trustStore.setPath(getOldRequiredProperty(BACKEND_TRUST_STORE_PATH_OLD_PROP_NAME));
        trustStore.setType("JKS");

        var keyStore = new StoreConfigurationProperties();
        cxfProps.setKeyStore(keyStore);
        keyStore.setType("JKS");
        keyStore.setPassword(getOldRequiredProperty(BACKEND_KEY_STORE_PW_OLD_PROP_NAM));
        keyStore.setPath(getOldRequiredProperty(BACKEND_KEY_STORE_PATH_OLD_PROP_NAME));

        var privateKeyConfig = new KeyConfigurationProperties();
        cxfProps.setPrivateKey(privateKeyConfig);
        privateKeyConfig.setPassword(getOldRequiredProperty(BACKEND_PRIVATE_KEY_PW_OLD_PROP_NAME));
        privateKeyConfig.setAlias(getOldRequiredProperty(BACKEND_PRIVATE_KEY_ALIAS_OLD_PROP_NAME));

        return wsBackendPluginConfigurationProperties;
    }

    /**
     * Retrieves the backend partners for the Domibus Connector.
     *
     * @return The list of DomibusConnectorLinkPartner representing the backend partners.
     */
    public List<DomibusConnectorLinkPartner> getBackendPartners() {
        DomibusConnectorLinkConfiguration lnkConfig = getBackendLinkConfiguration();

        return loadBackendsFromDb()
            .stream()
            .peek(lp -> {
                lp.setLinkConfiguration(lnkConfig);
                lp.setDescription("imported by import 4.2 old config");
            })
            .toList();
    }

    /**
     * Retrieves the backend link configuration for the Domibus Connector.
     *
     * @return The backend link configuration represented by a DomibusConnectorLinkConfiguration
     *      object.
     */
    private DomibusConnectorLinkConfiguration getBackendLinkConfiguration() {
        var domibusConnectorLinkConfiguration = new DomibusConnectorLinkConfiguration();
        domibusConnectorLinkConfiguration.setLinkImpl(WsBackendPlugin.IMPL_NAME);
        domibusConnectorLinkConfiguration.setConfigName(
            new DomibusConnectorLinkConfiguration.LinkConfigName("Imported_4.2_BackendConfig"));
        domibusConnectorLinkConfiguration.setProperties(
            beanToPropertyMapConverter.readBeanPropertiesToMap(convertBackendLinkProperties(), ""));
        return domibusConnectorLinkConfiguration;
    }

    /**
     * Retrieves the list of DomibusConnectorLinkPartner representing the backend partners from the
     * database.
     *
     * @return The list of DomibusConnectorLinkPartner representing the backend partners.
     */
    private List<DomibusConnectorLinkPartner> loadBackendsFromDb() {

        return jdbcTemplate.query(
            "Select BACKEND_NAME, BACKEND_KEY_ALIAS, BACKEND_PUSH_ADDRESS, BACKEND_DEFAULT, "
                + "BACKEND_ENABLED, BACKEND_DESCRIPTION "
                + " FROM DOMIBUS_CONNECTOR_BACKEND_INFO",
            (rs, rowNum) -> {
                var linkPartner = new DomibusConnectorLinkPartner();
                linkPartner.setDescription(rs.getString("BACKEND_DESCRIPTION"));
                linkPartner.setEnabled(rs.getBoolean("BACKEND_ENABLED"));
                linkPartner.setLinkPartnerName(
                    new DomibusConnectorLinkPartner.LinkPartnerName(rs.getString("BACKEND_NAME")));
                linkPartner.setRcvLinkMode(LinkMode.PASSIVE);
                linkPartner.setLinkType(LinkType.BACKEND);

                Map<String, String> props = new HashMap<>();
                linkPartner.setProperties(props);

                props.put("encryption-alias", rs.getString("BACKEND_KEY_ALIAS"));
                props.put("certificate-dn", rs.getString("BACKEND_NAME"));

                var pushAddress = rs.getString("BACKEND_PUSH_ADDRESS");
                if (StringUtils.hasText(pushAddress)) {
                    linkPartner.setSendLinkMode(LinkMode.PUSH);
                    props.put("push-address", pushAddress);
                } else {
                    linkPartner.setSendLinkMode(LinkMode.PULL);
                }

                return linkPartner;
            }
        );
    }

    /**
     * Retrieves the value of a required property from the oldProperties map.
     *
     * @param oldPropName The name of the property to retrieve.
     * @return The value of the property as a string.
     * @throws IllegalArgumentException if the provided 'old' properties does not contain the
     *                                  required property.
     */
    private String getOldRequiredProperty(String oldPropName) {
        if (oldProperties.containsKey(oldPropName)) {
            return oldProperties.get(oldPropName).toString();
        } else {
            throw new IllegalArgumentException(String.format(
                "The provided 'old' properties does not contain the property [%s], which is "
                    + "required!",
                oldPropName
            ));
        }
    }
}
