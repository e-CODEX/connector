/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.lib.spring.configuration.validation.CheckKeyIsLoadableFromKeyStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * The KeyAndKeyStoreAndTrustStoreConfigurationProperties class represents the configuration
 * properties for a key, key store, and trust store. It extends the
 * KeyAndKeyStoreConfigurationProperties class, which provides the basic key and key store
 * configuration properties.
 *
 * <p>This class is annotated with the @CheckKeyIsLoadableFromKeyStore annotation. It also has a
 * nested property, trustStore, that represents the configuration of the trust store.
 */
@CheckKeyIsLoadableFromKeyStore
@Getter
@Setter
@NoArgsConstructor
public class KeyAndKeyStoreAndTrustStoreConfigurationProperties
    extends KeyAndKeyStoreConfigurationProperties {
    /**
     * Configuration of the TrustStore.
     */
    @NestedConfigurationProperty
    @Valid
    @NotNull
    private StoreConfigurationProperties trustStore;
}
