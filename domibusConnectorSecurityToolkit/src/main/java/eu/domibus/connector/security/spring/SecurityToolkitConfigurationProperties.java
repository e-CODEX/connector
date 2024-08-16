/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.security.spring;

import eu.domibus.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Contains security toolkit related configuration in a typesafe way. The class is also marked as
 *
 * @deprecated indicating that it is no longer recommended for use.
 */
@Data
@Deprecated
public class SecurityToolkitConfigurationProperties {
    public static final String CONFIG_PREFIX = "connector.security";
    @Valid
    @NotNull
    @NestedConfigurationProperty
    StoreConfigurationProperties keyStore = new StoreConfigurationProperties();
    @Valid
    @NotNull
    @NestedConfigurationProperty
    KeyConfigurationProperties privateKey = new KeyConfigurationProperties();
    @Valid
    @NotNull
    @NestedConfigurationProperty
    StoreConfigurationProperties trustStore = new StoreConfigurationProperties();
    @NotNull
    EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.RSA;
    @NotNull
    DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA512;
}
