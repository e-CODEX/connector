/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.link.common;

import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * The MerlinPropertiesFactory class is responsible for mapping own configured properties to the
 * crypto Properties used by the WSS4J library. It provides a method to map the key store and trust
 * store configuration properties to the crypto Properties.
 */
@Service
public class MerlinPropertiesFactory {
    private static final Logger LOGGER = LogManager.getLogger(MerlinPropertiesFactory.class);

    /**
     * Maps the own configured properties to the crypto Properties also see
     * https://ws.apache.org/wss4j/config.html.
     *
     * @return the wss Properties
     */
    public Properties mapCertAndStoreConfigPropertiesToMerlinProperties(
        KeyAndKeyStoreAndTrustStoreConfigurationProperties config, String prefix) {
        if (config == null) {
            throw new IllegalArgumentException(prefix + ".config.* properties are missing!");
        }
        StoreConfigurationProperties keyStore = config.getKeyStore();
        if (keyStore == null) {
            throw new IllegalArgumentException(
                prefix + ".config.key-store.* properties are missing or wrong!");
        }

        var properties = new Properties();
        properties.put("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        properties.put("org.apache.wss4j.crypto.merlin.keystore.type", keyStore.getType());
        properties.put("org.apache.wss4j.crypto.merlin.keystore.password", keyStore.getPassword());
        LOGGER.debug(
            "setting [org.apache.wss4j.crypto.merlin.keystore.file={}]", keyStore.getPath());
        try {
            properties.put("org.apache.wss4j.crypto.merlin.keystore.file", keyStore.getPath());
        } catch (Exception e) {
            throw new RuntimeException(
                "Error with property: [" + prefix + ".config.key-store.path]\n"
                    + "value is [" + keyStore.getPath() + "]");
        }
        properties.put(
            "org.apache.wss4j.crypto.merlin.keystore.alias", config.getPrivateKey().getAlias());
        properties.put(
            "org.apache.wss4j.crypto.merlin.keystore.private.password",
            config.getPrivateKey().getPassword()
        );

        StoreConfigurationProperties trustStore = config.getTrustStore();
        properties.put("org.apache.wss4j.crypto.merlin.truststore.type", trustStore.getType());
        properties.put(
            "org.apache.wss4j.crypto.merlin.truststore.password", trustStore.getPassword());
        try {
            LOGGER.debug(
                "setting [org.apache.wss4j.crypto.merlin.truststore.file={}]",
                trustStore.getPath()
            );
            properties.put("org.apache.wss4j.crypto.merlin.truststore.file", trustStore.getPath());
        } catch (Exception e) {
            String info = "Trust Store Property: [{}.config.trust-store.path]\n "
                + "cannot be processed. Using the configured key store [{}] as trust store";
            LOGGER.info(
                info, prefix, properties.get("org.apache.wss4j.crypto.merlin.keystore.file")
            );
        }

        if (config instanceof CxfTrustKeyStoreConfigurationProperties configurationProperties) {
            properties.put(
                "org.apache.wss4j.crypto.merlin.load.cacerts",
                Boolean.toString(configurationProperties.isLoadCaCerts())
            );
            properties.put(
                "security.encryption.username", configurationProperties.getEncryptAlias()
            );
        }

        return properties;
    }
}
