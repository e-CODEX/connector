/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.dss.configuration;

import eu.domibus.connector.common.annotations.MapNested;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * This class represents the configuration properties for a Certificate Verifier. It contains
 * properties for the name of the verifier, whether the trust store is enabled, the trust store
 * configuration properties, whether the ignore store is enabled, the ignore store configuration
 * properties, the trusted list source, whether OCSP is enabled, whether CRL is enabled, and whether
 * AIA is enabled.
 */
@SuppressWarnings("squid:S1135")
@MapNested
@Getter
@Setter
public class CertificateVerifierConfigurationProperties {
    @NotBlank
    private String certificateVerifierName = "default";
    private boolean trustStoreEnabled = true;
    // TODO: add not null validation if trustStoreEnabled = true
    @Valid
    @NestedConfigurationProperty
    @ConfigurationLabel("Trust Store")
    @ConfigurationDescription("This store holds all valid certificates for validation")
    @MapNested
    private StoreConfigurationProperties trustStore;
    private boolean ignoreStoreEnabled = false;
    /**
     * Any certificate within this store would not be considered for certificate validation.
     */
    // TODO: add not null validation if trustStoreEnabled = true
    @Valid
    @MapNested
    @NestedConfigurationProperty
    @ConfigurationLabel("Ignore Store")
    @ConfigurationDescription(
        "This store holds all ignored certificates for validation.\nAny certificate within "
            + "this store\nwould not be considered for certificate validation"
    )
    private StoreConfigurationProperties ignoreStore;
    /**
     * The trust source which should be used the sources are configured under.
     * {@link BasicDssConfigurationProperties}
     */
    @ConfigurationLabel("Trusted List Source")
    @ConfigurationDescription(
        "The names of trusted list source. The sources are configured under: "
            + BasicDssConfigurationProperties.PREFIX + ".trust-source.*"
    )
    private String trustedListSource;
    /**
     * should ocsp be queried.
     */
    private boolean ocspEnabled = true;
    /**
     * should a crl be queried.
     */
    private boolean crlEnabled = true;
    /**
     * should AIA be used.
     */
    private boolean aiaEnabled = true;

    public @Nullable String getCertificateVerifierName() {
        return certificateVerifierName;
    }

    public @Nullable StoreConfigurationProperties getTrustStore() {
        return trustStore;
    }
}
