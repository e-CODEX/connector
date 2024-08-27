/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.evidences.spring;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
