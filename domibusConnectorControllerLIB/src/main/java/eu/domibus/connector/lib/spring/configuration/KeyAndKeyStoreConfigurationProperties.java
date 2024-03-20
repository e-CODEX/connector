package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.lib.spring.configuration.validation.CheckKeyIsLoadableFromKeyStore;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * A Property class to map properties for an
 * keystore with path and password
 * and an key with alias and password
 *
 *  .store.path=keystore path
 *  .store.password=keystore password
 *
 *  .key.alias=alias for a private key in the configured key store
 *  .key.password=password for this key
 *
 *
 */
@CheckKeyIsLoadableFromKeyStore
@Validated
public class KeyAndKeyStoreConfigurationProperties {


    public KeyAndKeyStoreConfigurationProperties() {
    }


    /**
     * Configuration of the (Key/Certificate)Store
     */
    @NestedConfigurationProperty
    @Valid
    @NotNull
    private StoreConfigurationProperties keyStore;

    /**
     * Configures the default alias to use
     */
    @NestedConfigurationProperty
    @Valid
    @NotNull
    private KeyConfigurationProperties privateKey;


    public StoreConfigurationProperties getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(StoreConfigurationProperties keyStore) {
        this.keyStore = keyStore;
    }

    public KeyConfigurationProperties getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(KeyConfigurationProperties privateKey) {
        this.privateKey = privateKey;
    }

}
