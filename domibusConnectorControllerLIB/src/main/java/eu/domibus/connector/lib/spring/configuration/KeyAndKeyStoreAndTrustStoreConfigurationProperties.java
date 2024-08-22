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
