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

import eu.ecodex.connector.lib.spring.configuration.validation.CheckKeyIsLoadableFromKeyStore;
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
