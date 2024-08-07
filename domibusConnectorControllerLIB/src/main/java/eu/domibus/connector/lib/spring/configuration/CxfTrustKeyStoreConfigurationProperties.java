/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.lib.spring.configuration.validation.CheckKeyIsLoadableFromKeyStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * The CxfTrustKeyStoreConfigurationProperties class represents the configuration properties related
 * to the trust key store used by the CXF framework. It extends the
 * KeyAndKeyStoreAndTrustStoreConfigurationProperties class which provides the basic key and key
 * store configuration properties.
 */
@CheckKeyIsLoadableFromKeyStore
@Valid
@Setter
@Getter
public class CxfTrustKeyStoreConfigurationProperties
    extends KeyAndKeyStoreAndTrustStoreConfigurationProperties {
    @Valid
    @NotNull
    private String encryptAlias;
    private boolean loadCaCerts = true;
}
