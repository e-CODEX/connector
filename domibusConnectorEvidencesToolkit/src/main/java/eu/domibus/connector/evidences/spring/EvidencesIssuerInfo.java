/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.evidences.spring;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * This class represents the information of an issuer of evidences.
 */
@Getter
@Setter
public class EvidencesIssuerInfo {
    @Valid
    @NotNull
    @NestedConfigurationProperty
    private PostalAdressConfigurationProperties postalAddress =
        new PostalAdressConfigurationProperties();
    @NotNull
    @Valid
    @NestedConfigurationProperty
    private HomePartyConfigurationProperties as4Party = new HomePartyConfigurationProperties();
}
