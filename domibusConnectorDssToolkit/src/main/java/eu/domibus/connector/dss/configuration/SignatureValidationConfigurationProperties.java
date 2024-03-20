package eu.domibus.connector.dss.configuration;

import eu.domibus.connector.common.annotations.MapNested;
import eu.domibus.connector.dss.configuration.validation.ValidEtsiValidationPolicyXml;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@MapNested
public class SignatureValidationConfigurationProperties extends CertificateVerifierConfigurationProperties {

    @NotBlank
    @ValidEtsiValidationPolicyXml
    @ConfigurationLabel("Validation Constraints")
    @ConfigurationDescription("The DSS Certificate Validation Constraints config")
    private String validationConstraintsXml = "classpath:/102853/constraint.xml";

    @NotNull
    public String getValidationConstraintsXml() {
        return validationConstraintsXml;
    }

    public void setValidationConstraintsXml(String validationConstraintsXml) {
        this.validationConstraintsXml = validationConstraintsXml;
    }

}
