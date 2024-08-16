/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.lib.spring.configuration.validation.CheckKeyIsLoadableFromKeyStore;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
