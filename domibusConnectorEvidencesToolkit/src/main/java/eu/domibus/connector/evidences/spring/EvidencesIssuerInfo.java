package eu.domibus.connector.evidences.spring;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class EvidencesIssuerInfo {

    @Valid
    @NotNull
    @NestedConfigurationProperty
    private PostalAdressConfigurationProperties postalAddress = new PostalAdressConfigurationProperties();

    @NotNull
    @Valid
    @NestedConfigurationProperty
    private HomePartyConfigurationProperties as4Party = new HomePartyConfigurationProperties();

    public PostalAdressConfigurationProperties getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAdressConfigurationProperties postalAddress) {
        this.postalAddress = postalAddress;
    }

    public HomePartyConfigurationProperties getAs4Party() {
        return as4Party;
    }

    public void setAs4Party(HomePartyConfigurationProperties as4Party) {
        this.as4Party = as4Party;
    }
}
