/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.lib.spring.configuration;

import java.util.Properties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * This abstract class represents the properties for configuring the CXF certificate and key
 * settings.
 */
@Data
@SuppressWarnings("squid:S1135")
public abstract class CxfCertKeyProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(CxfCertKeyProperties.class);
    /**
     * This property configures the path to the security policy which should be used for the backend
     * webservice.
     *
     * <p>The default security policy requires signed and encrypted messages (body+header) the
     * signing and wss is done with certificates
     */
    @NestedConfigurationProperty
    private Resource wsPolicy = new ClassPathResource("/wsdl/backend.policy.xml");
    /**
     * Configuration of the privateKey store which is used to sign the transferred soap-messages and
     * decrypt from the backendClient the received messages.
     */
    @NestedConfigurationProperty
    private KeyAndKeyStoreConfigurationProperties privateKey;
    /**
     * Trust store which is used to verify from the backendClient signed messages and encrypt the
     * messages transmitted to the backendClients.
     */
    @NestedConfigurationProperty
    private CertAndStoreConfigurationProperties trust;

    /**
     * Retrieves the WSS properties by mapping the own configured properties to the crypto
     * properties using the Merlin provider.
     *
     * @return the WSS properties
     */
    public Properties getWssProperties() {
        var p = mapCertAndStoreConfigPropertiesToMerlinProperties();
        LOGGER.debug("getSignatureProperties() are: [{}]", p);
        return p;
    }

    /**
     * Maps the own configured properties to the crypto Properties also see
     * <a
     * href="https://ws.apache.org/wss4j/config.html">https://ws.apache.org/wss4j/config.html</a>.
     *
     * @return the wss Properties
     */
    public Properties mapCertAndStoreConfigPropertiesToMerlinProperties() {
        var p = new Properties();
        p.setProperty("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        p.setProperty("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
        p.setProperty(
            "org.apache.wss4j.crypto.merlin.keystore.password",
            this.getPrivateKey().getStore().getPassword()
        );
        LOGGER.debug(
            "setting [org.apache.wss4j.crypto.merlin.keystore.file={}]",
            this.getPrivateKey().getStore().getPath()
        );
        try {
            // TODO: fix this
            //  p.setProperty("org.apache.wss4j.crypto.merlin.keystore.file", this.getPrivateKey()
            //  .getStore().getPathUrlAsString());
        } catch (Exception e) {
            throw new RuntimeException(
                "Error with property: [" + getPrefix() + ".privateKey.store.path]\n"
                    + "value is [" + this.getPrivateKey().getStore().getPath() + "]");
        }
        p.setProperty(
            "org.apache.wss4j.crypto.merlin.keystore.alias",
            this.getPrivateKey().getKey().getAlias()
        );
        p.setProperty(
            "org.apache.wss4j.crypto.merlin.keystore.private.password",
            this.getPrivateKey().getKey().getPassword()
        );
        p.setProperty(
            "org.apache.wss4j.crypto.merlin.truststore.password",
            this.getTrust().getStore().getPassword()
        );
        try {
            LOGGER.debug(
                "setting [org.apache.wss4j.crypto.merlin.truststore.file={}]",
                this.getTrust().getStore().getPath()
            );
            /* TODO: fix this
                p.setProperty("org.apache.wss4j.crypto.merlin.truststore.file", this.getTrust()
                .getStore().getPathUrlAsString());
             */
        } catch (Exception e) {
            var info = new StrBuilder();
            info.append("Trust Store Property: [");
            info.append(getPrefix());
            info.append(".trust.store.path] \n cannot be processed. ");
            info.append("Using the configured privateKey store [{}] as trust store");
            LOGGER.info(
                info.toString(), p.getProperty("org.apache.wss4j.crypto.merlin.keystore.file")
            );

            p.setProperty(
                "org.apache.wss4j.crypto.merlin.truststore.file",
                p.getProperty("org.apache.wss4j.crypto.merlin.keystore.file")
            );
            p.setProperty(
                "org.apache.wss4j.crypto.merlin.truststore.password",
                p.getProperty("org.apache.wss4j.crypto.merlin.keystore.password")
            );
        }
        p.setProperty(
            "org.apache.wss4j.crypto.merlin.load.cacerts",
            Boolean.toString(this.getTrust().isLoadCaCerts())
        );

        return p;
    }

    private String getPrefix() {
        return "?not set?";
    }

    /**
     * Configuration properties for specifying Key and KeyStore configuration.
     */
    @Data
    @NoArgsConstructor
    public static class KeyAndKeyStoreConfigurationProperties {
        public KeyAndKeyStoreConfigurationProperties(
            StoreConfigurationProperties keyStore, KeyConfigurationProperties key) {
            this.store = keyStore;
            this.key = key;
        }

        /**
         * Configuration of the (Key/Certificate)Store.
         */
        @NestedConfigurationProperty
        private StoreConfigurationProperties store;
        /**
         * Configures the default alias to use.
         */
        @NestedConfigurationProperty
        private KeyConfigurationProperties key;
    }

    /**
     * Configuration properties for specifying the certificate and store configuration.
     */
    @Data
    @NoArgsConstructor
    public static class CertAndStoreConfigurationProperties {
        public CertAndStoreConfigurationProperties(StoreConfigurationProperties keyStore) {
            this.store = keyStore;
        }

        /**
         * Configuration of the (Key/Certificate)Store.
         */
        @NestedConfigurationProperty
        private StoreConfigurationProperties store;
        /**
         * Load system Ca Certs? (default false).
         *
         * <p>Whether or not to load the CA certs in ${java.home}/lib/security/cacerts (default is
         * false)
         */
        private boolean loadCaCerts = false;
    }
}
