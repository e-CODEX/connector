/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.evidences.spring;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
