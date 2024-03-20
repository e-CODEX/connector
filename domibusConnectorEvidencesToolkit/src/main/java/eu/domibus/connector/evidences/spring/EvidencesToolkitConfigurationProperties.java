package eu.domibus.connector.evidences.spring;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import eu.domibus.connector.dss.configuration.SignatureConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@BusinessDomainScoped
@Component
@ConfigurationProperties(prefix = EvidencesToolkitConfigurationProperties.CONFIG_PREFIX)
@Validated
public class EvidencesToolkitConfigurationProperties {

    public static final String CONFIG_PREFIX = "connector.evidences";

    @Valid
    @NestedConfigurationProperty
    SignatureConfigurationProperties signature = new SignatureConfigurationProperties();

    @Valid
    @NestedConfigurationProperty
    EvidencesIssuerInfo issuerInfo = new EvidencesIssuerInfo();

    public SignatureConfigurationProperties getSignature() {
        return signature;
    }

    public void setSignature(SignatureConfigurationProperties signature) {
        this.signature = signature;
    }

    public EvidencesIssuerInfo getIssuerInfo() {
        return issuerInfo;
    }

    public void setIssuerInfo(EvidencesIssuerInfo issuerInfo) {
        this.issuerInfo = issuerInfo;
    }
}
