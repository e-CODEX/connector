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
import eu.domibus.connector.dss.configuration.validation.ValidEtsiValidationPolicyXml;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

/**
 * This class represents the configuration properties for Signature Validation. It inherits
 * properties from the CertificateVerifierConfigurationProperties class. It contains properties for
 * the validation constraints XML file path.
 */
@Setter
@MapNested
public class SignatureValidationConfigurationProperties
    extends CertificateVerifierConfigurationProperties {
    @NotBlank
    @ValidEtsiValidationPolicyXml
    @ConfigurationLabel("Validation Constraints")
    @ConfigurationDescription("The DSS Certificate Validation Constraints config")
    private String validationConstraintsXml = "classpath:/102853/constraint.xml";

    @NotNull
    public String getValidationConstraintsXml() {
        return validationConstraintsXml;
    }
}
