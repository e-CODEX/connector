/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.lib.spring.configuration.validation.CheckKeyIsLoadableFromKeyStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * A Property class to map properties for a keystore with path and password and a key with alias
 * and password.
 *
 * <p>.store.path=keystore path .store.password=keystore password
 *
 * <p>.key.alias=alias for a private key in the configured key store .key.password=password for
 * this key
 */
@CheckKeyIsLoadableFromKeyStore
@Validated
@NoArgsConstructor
@Data
public class KeyAndKeyStoreConfigurationProperties {
    /**
     * Configuration of the (Key/Certificate)Store.
     */
    @NestedConfigurationProperty
    @Valid
    @NotNull
    private StoreConfigurationProperties keyStore;
    /**
     * Configures the default alias to use.
     */
    @NestedConfigurationProperty
    @Valid
    @NotNull
    private KeyConfigurationProperties privateKey;
}
