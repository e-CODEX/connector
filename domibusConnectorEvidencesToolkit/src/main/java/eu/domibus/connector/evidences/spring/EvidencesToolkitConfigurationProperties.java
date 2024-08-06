/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.evidences.spring;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import eu.domibus.connector.dss.configuration.SignatureConfigurationProperties;
import javax.validation.Valid;
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
