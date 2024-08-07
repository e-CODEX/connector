package eu.domibus.connector.evidences.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * This configuration properties are holding
 * the address information which is appended to
 * the generated ETSI-REM-evidences
 */
@Validated
@Valid
public class PostalAdressConfigurationProperties {

    /**
     * The street
     */
    @NotBlank
    private String street;

    /**
     * Locality, eg, Brussels, Vienna, ...
     */
    @NotBlank
    private String locality;

    /**
     * The zipCode
     */
    @NotBlank
    private String zipCode;

    /**
     * The country, preferred the
     * ISO 2-letter Country Code
     * eg. AT, DE, ...
     *
     *
     */
    @NotBlank
    private String country;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
