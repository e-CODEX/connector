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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

/**
 * This configuration properties are holding the address information which is appended to the
 * generated ETSI-REM-evidences.
 */
@Getter
@Setter
@Validated
@Valid
public class PostalAdressConfigurationProperties {
    /**
     * The street.
     */
    @NotBlank
    private String street;
    /**
     * Locality, eg, Brussels, Vienna, ...
     */
    @NotBlank
    private String locality;
    /**
     * The zipCode.
     */
    @NotBlank
    private String zipCode;
    /**
     * The country, preferred the ISO 2-letter Country Code eg. AT, DE, ...
     */
    @NotBlank
    private String country;
}
