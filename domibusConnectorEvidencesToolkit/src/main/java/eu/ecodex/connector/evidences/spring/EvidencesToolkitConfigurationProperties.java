/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.evidences.spring;

import eu.ecodex.connector.common.annotations.BusinessDomainScoped;
import eu.ecodex.connector.dss.configuration.SignatureConfigurationProperties;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * This class represents the configuration properties for the EvidencesToolkit.
 */
@BusinessDomainScoped
@Component
@ConfigurationProperties(prefix = EvidencesToolkitConfigurationProperties.CONFIG_PREFIX)
@Validated
@Getter
@Setter
public class EvidencesToolkitConfigurationProperties {
    public static final String CONFIG_PREFIX = "connector.evidences";
    @Valid
    @NestedConfigurationProperty
    SignatureConfigurationProperties signature = new SignatureConfigurationProperties();
    @Valid
    @NestedConfigurationProperty
    EvidencesIssuerInfo issuerInfo = new EvidencesIssuerInfo();
}
