/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.security.configuration;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import eu.domibus.connector.dss.configuration.SignatureConfigurationProperties;
import eu.domibus.connector.dss.configuration.SignatureValidationConfigurationProperties;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
