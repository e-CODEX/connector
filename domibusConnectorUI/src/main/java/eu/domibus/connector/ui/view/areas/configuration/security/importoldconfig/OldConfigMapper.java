/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.security.importoldconfig;

import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import eu.domibus.connector.dss.configuration.SignatureConfigurationProperties;
import eu.domibus.connector.dss.configuration.SignatureValidationConfigurationProperties;
import eu.domibus.connector.evidences.spring.EvidencesIssuerInfo;
import eu.domibus.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import eu.domibus.connector.evidences.spring.PostalAdressConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import java.util.Map;

/**
 * This class is responsible for mapping old configuration properties to new configuration objects.
 */
public class OldConfigMapper {
    private static final String PATH = ".path";
    private static final String PASSWORD = ".password";
    private static final String ALIAS = ".alias";
    private static final String KEYSTORE_WITH_DASH = ".key-store";
    private static final String KEYSTORE_WITH_CAMELCASE_LETTER = ".keyStore";
    private static final String KEYSTORE = ".keystore";
    private static final String TRUSTSTORE = ".truststore";
    private static final String TRUSTSTORE_WITH_DASH = ".trust-store";
    private static final String TRUSTSTORE_WITH_CAMELCASE_LETTER = ".trustStore";
    private static final String PRIVATE_KEY = ".privatekey";
    private static final String PRIVATE_KEY_WITH_DASH = ".private-key";
    private static final String PRIVATE_KEY_WITH_CAMELCASE_LETTER = ".privateKey";
    private static final String TOKEN_ISSUER_COUNTRY = "token.issuer.country";
    private static final String TOKEN_ISSUER_SERVICE_PROVIDER = "token.issuer.service-provider";
    private static final String TOKEN_ISSUER_IDENTITY_PROVIDER = "token.issuer.identity-provider";
    private static final String TOKEN_ISSUER_AES_TYPE =
        "token.issuer.advanced-electronic-system-type";
    private static final String SECURITY = "connector.security";
    private static final String EVIDENCE = "connector.evidences";
    private static final String POSTAL_ADDR_STREET = "postal.address.street";
    private static final String POSTAL_ADDR_LOCALITY = "postal.address.locality";
    private static final String POSTAL_ADDR_ZIPCODE = "postal.address.zip-code";
    private static final String POSTAL_ADDR_COUNTRY = "postal.address.country";
    private final Map<String, String> oldProperties;

    public OldConfigMapper(Map<String, String> oldProperties) {
        this.oldProperties = oldProperties;
    }

    /**
     * Migrates the business document configuration properties from the old configuration format to
     * the new format.
     *
     * @return The migrated DCBusinessDocumentValidationConfigurationProperties object with the
     *      updated configuration.
     */
    public DCBusinessDocumentValidationConfigurationProperties
    migrateBusinessDocumentConfigurationProperties() {
        var configurationProperties = new DCBusinessDocumentValidationConfigurationProperties();
        configurationProperties.setServiceProvider(
            oldProperties.get(TOKEN_ISSUER_SERVICE_PROVIDER));
        configurationProperties.setCountry(oldProperties.get(TOKEN_ISSUER_COUNTRY));
        configurationProperties.setDefaultAdvancedSystemType(
            AdvancedElectronicSystemType.valueOf(oldProperties.get(TOKEN_ISSUER_AES_TYPE)));

        if (oldProperties.containsKey(TOKEN_ISSUER_IDENTITY_PROVIDER)) {
            configurationProperties.setAuthenticationValidation(
                new DCBusinessDocumentValidationConfigurationProperties
                    .AuthenticationValidationConfigurationProperties()
            );
            configurationProperties.getAuthenticationValidation()
                                   .setIdentityProvider(
                                       oldProperties.get(TOKEN_ISSUER_IDENTITY_PROVIDER));
        }

        configurationProperties.setSignatureValidation(
            new SignatureValidationConfigurationProperties()
        );
        configurationProperties.getSignatureValidation().setTrustStoreEnabled(true);

        var trustStore = new StoreConfigurationProperties();
        configurationProperties.getSignatureValidation().setTrustStore(trustStore);

        if (oldProperties.containsKey(SECURITY + TRUSTSTORE + PATH)) {
            trustStore.setPath(oldProperties.get(SECURITY + TRUSTSTORE + PATH));
        } else if (oldProperties.containsKey(SECURITY + TRUSTSTORE_WITH_CAMELCASE_LETTER + PATH)) {
            trustStore.setPath(
                oldProperties.get(SECURITY + TRUSTSTORE_WITH_CAMELCASE_LETTER + PATH));
        } else if (oldProperties.containsKey(SECURITY + TRUSTSTORE_WITH_DASH + PATH)) {
            trustStore.setPath(oldProperties.get(SECURITY + TRUSTSTORE_WITH_DASH + PATH));
        }

        if (oldProperties.containsKey(SECURITY + TRUSTSTORE + PASSWORD)) {
            trustStore.setPassword(oldProperties.get(SECURITY + TRUSTSTORE + PASSWORD));
        } else if (oldProperties.containsKey(
            SECURITY + TRUSTSTORE_WITH_CAMELCASE_LETTER + PASSWORD)) {
            trustStore.setPassword(
                oldProperties.get(SECURITY + TRUSTSTORE_WITH_CAMELCASE_LETTER + PASSWORD));
        } else if (oldProperties.containsKey(SECURITY + TRUSTSTORE_WITH_DASH + PASSWORD)) {
            trustStore.setPassword(oldProperties.get(SECURITY + TRUSTSTORE_WITH_DASH + PASSWORD));
        }

        trustStore.setType("JKS");

        return configurationProperties;
    }

    /**
     * This method migrates the ECodex container properties from the old configuration to the new
     * configuration.
     *
     * @return The migrated DCEcodexContainerProperties object with the signature configuration set.
     */
    public DCEcodexContainerProperties migrateEcodexContainerProperties() {
        var containerProperties = new DCEcodexContainerProperties();

        var signature = new SignatureConfigurationProperties();
        containerProperties.setSignature(signature);
        var keyStore = new StoreConfigurationProperties();

        if (oldProperties.containsKey(SECURITY + KEYSTORE + PATH)) {
            keyStore.setPath(oldProperties.get(SECURITY + KEYSTORE + PATH));
        } else if (oldProperties.containsKey(SECURITY + KEYSTORE_WITH_CAMELCASE_LETTER + PATH)) {
            keyStore.setPath(oldProperties.get(SECURITY + KEYSTORE_WITH_CAMELCASE_LETTER + PATH));
        } else if (oldProperties.containsKey(SECURITY + KEYSTORE_WITH_DASH + PATH)) {
            keyStore.setPath(oldProperties.get(SECURITY + KEYSTORE_WITH_DASH + PATH));
        }

        if (oldProperties.containsKey(SECURITY + KEYSTORE + PASSWORD)) {
            keyStore.setPassword(oldProperties.get(SECURITY + KEYSTORE + PASSWORD));
        } else if (oldProperties.containsKey(
            SECURITY + KEYSTORE_WITH_CAMELCASE_LETTER + PASSWORD)) {
            keyStore.setPassword(
                oldProperties.get(SECURITY + KEYSTORE_WITH_CAMELCASE_LETTER + PASSWORD));
        } else if (oldProperties.containsKey(SECURITY + KEYSTORE_WITH_DASH + PASSWORD)) {
            keyStore.setPassword(oldProperties.get(SECURITY + KEYSTORE_WITH_DASH + PASSWORD));
        }

        keyStore.setType("JKS");
        signature.setKeyStore(keyStore);
        var privateKey = new KeyConfigurationProperties();

        if (oldProperties.containsKey(SECURITY + PRIVATE_KEY + ALIAS)) {
            privateKey.setAlias(oldProperties.get(SECURITY + PRIVATE_KEY + ALIAS));
        } else if (oldProperties.containsKey(
            SECURITY + PRIVATE_KEY_WITH_CAMELCASE_LETTER + ALIAS)) {
            privateKey.setAlias(
                oldProperties.get(SECURITY + PRIVATE_KEY_WITH_CAMELCASE_LETTER + ALIAS));
        } else if (oldProperties.containsKey(SECURITY + PRIVATE_KEY_WITH_DASH + ALIAS)) {
            privateKey.setAlias(oldProperties.get(SECURITY + PRIVATE_KEY_WITH_DASH + ALIAS));
        }

        if (oldProperties.containsKey(SECURITY + PRIVATE_KEY + PASSWORD)) {
            privateKey.setPassword(oldProperties.get(SECURITY + PRIVATE_KEY + PASSWORD));
        } else if (oldProperties.containsKey(
            SECURITY + PRIVATE_KEY_WITH_CAMELCASE_LETTER + PASSWORD)) {
            privateKey.setPassword(
                oldProperties.get(SECURITY + PRIVATE_KEY_WITH_CAMELCASE_LETTER + PASSWORD));
        } else if (oldProperties.containsKey(SECURITY + PRIVATE_KEY_WITH_DASH + PASSWORD)) {
            privateKey.setPassword(oldProperties.get(SECURITY + PRIVATE_KEY_WITH_DASH + PASSWORD));
        }

        signature.setPrivateKey(privateKey);

        return containerProperties;
    }

    /**
     * Migrates the EvidencesToolkitConfig from the old configuration format to the new format.
     *
     * @return The migrated EvidencesToolkitConfigurationProperties object with the updated
     *      configuration.
     */
    public EvidencesToolkitConfigurationProperties migrateEvidencesToolkitConfig() {
        var configurationProperties = new EvidencesToolkitConfigurationProperties();

        var signature = new SignatureConfigurationProperties();
        configurationProperties.setSignature(signature);
        var keyStore = new StoreConfigurationProperties();

        if (oldProperties.containsKey(EVIDENCE + KEYSTORE + PATH)) {
            keyStore.setPath(oldProperties.get(EVIDENCE + KEYSTORE + PATH));
        } else if (oldProperties.containsKey(EVIDENCE + KEYSTORE_WITH_CAMELCASE_LETTER + PATH)) {
            keyStore.setPath(oldProperties.get(EVIDENCE + KEYSTORE_WITH_CAMELCASE_LETTER + PATH));
        } else if (oldProperties.containsKey(EVIDENCE + KEYSTORE_WITH_DASH + PATH)) {
            keyStore.setPath(oldProperties.get(EVIDENCE + KEYSTORE_WITH_DASH + PATH));
        }

        if (oldProperties.containsKey(EVIDENCE + KEYSTORE + PASSWORD)) {
            keyStore.setPassword(oldProperties.get(EVIDENCE + KEYSTORE + PASSWORD));
        } else if (oldProperties.containsKey(
            EVIDENCE + KEYSTORE_WITH_CAMELCASE_LETTER + PASSWORD)) {
            keyStore.setPassword(
                oldProperties.get(EVIDENCE + KEYSTORE_WITH_CAMELCASE_LETTER + PASSWORD));
        } else if (oldProperties.containsKey(EVIDENCE + KEYSTORE_WITH_DASH + PASSWORD)) {
            keyStore.setPassword(oldProperties.get(EVIDENCE + KEYSTORE_WITH_DASH + PASSWORD));
        }

        keyStore.setType("JKS");
        signature.setKeyStore(keyStore);
        var privateKey = new KeyConfigurationProperties();

        if (oldProperties.containsKey(EVIDENCE + PRIVATE_KEY + ALIAS)) {
            privateKey.setAlias(oldProperties.get(EVIDENCE + PRIVATE_KEY + ALIAS));
        } else if (oldProperties.containsKey(
            EVIDENCE + PRIVATE_KEY_WITH_CAMELCASE_LETTER + ALIAS)) {
            privateKey.setAlias(
                oldProperties.get(EVIDENCE + PRIVATE_KEY_WITH_CAMELCASE_LETTER + ALIAS));
        } else if (oldProperties.containsKey(EVIDENCE + PRIVATE_KEY_WITH_DASH + ALIAS)) {
            privateKey.setAlias(oldProperties.get(EVIDENCE + PRIVATE_KEY_WITH_DASH + ALIAS));
        }

        if (oldProperties.containsKey(EVIDENCE + PRIVATE_KEY + PASSWORD)) {
            privateKey.setPassword(oldProperties.get(EVIDENCE + PRIVATE_KEY + PASSWORD));
        } else if (oldProperties.containsKey(
            EVIDENCE + PRIVATE_KEY_WITH_CAMELCASE_LETTER + PASSWORD)) {
            privateKey.setPassword(
                oldProperties.get(EVIDENCE + PRIVATE_KEY_WITH_CAMELCASE_LETTER + PASSWORD));
        } else if (oldProperties.containsKey(EVIDENCE + PRIVATE_KEY_WITH_DASH + PASSWORD)) {
            privateKey.setPassword(oldProperties.get(EVIDENCE + PRIVATE_KEY_WITH_DASH + PASSWORD));
        }

        signature.setPrivateKey(privateKey);

        var evidencesIssuerInfo = new EvidencesIssuerInfo();
        configurationProperties.setIssuerInfo(evidencesIssuerInfo);
        var postInfo = new PostalAdressConfigurationProperties();
        evidencesIssuerInfo.setPostalAddress(postInfo);
        postInfo.setCountry(oldProperties.get(POSTAL_ADDR_COUNTRY));
        postInfo.setLocality(oldProperties.get(POSTAL_ADDR_LOCALITY));
        postInfo.setZipCode(oldProperties.get(POSTAL_ADDR_ZIPCODE));
        postInfo.setStreet(oldProperties.get(POSTAL_ADDR_STREET));

        return configurationProperties;
    }
}
