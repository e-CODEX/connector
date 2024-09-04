/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.security.configuration;

import eu.ecodex.connector.common.annotations.BusinessDomainScoped;
import eu.ecodex.connector.dss.configuration.SignatureConfigurationProperties;
import eu.ecodex.connector.dss.configuration.SignatureValidationConfigurationProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * This class represents the configuration properties for the DCEcodexContainer.
 */
@Data
@Component
@BusinessDomainScoped
@ConfigurationProperties(prefix = DCEcodexContainerProperties.PREFIX)
public class DCEcodexContainerProperties {
    public static final String PREFIX = "connector.ecodex-container";
    @Valid
    @NotNull
    @NestedConfigurationProperty
    private SignatureConfigurationProperties signature = new SignatureConfigurationProperties();
    @Valid
    @NotNull
    @NestedConfigurationProperty
    private SignatureValidationConfigurationProperties signatureValidation =
        new SignatureValidationConfigurationProperties();
}
