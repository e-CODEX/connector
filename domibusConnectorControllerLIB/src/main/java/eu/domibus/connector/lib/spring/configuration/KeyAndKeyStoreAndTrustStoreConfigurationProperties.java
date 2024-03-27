package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.lib.spring.configuration.validation.CheckKeyIsLoadableFromKeyStore;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@CheckKeyIsLoadableFromKeyStore
public class KeyAndKeyStoreAndTrustStoreConfigurationProperties extends KeyAndKeyStoreConfigurationProperties {
    /**
     * Configuration of the TrustStore
     */
    @NestedConfigurationProperty
    @Valid
    @NotNull
    private StoreConfigurationProperties trustStore;

    public KeyAndKeyStoreAndTrustStoreConfigurationProperties() {
    }

    public StoreConfigurationProperties getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(StoreConfigurationProperties trustStore) {
        this.trustStore = trustStore;
    }
}
