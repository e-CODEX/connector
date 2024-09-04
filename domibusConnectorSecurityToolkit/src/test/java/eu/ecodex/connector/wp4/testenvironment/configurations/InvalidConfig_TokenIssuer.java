/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.configurations;

import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.TokenIssuer;

/**
 * The InvalidConfig_TokenIssuer class represents a collection of methods that return instances of
 * the TokenIssuer class with invalid configurations.
 *
 * <p>These methods are used for testing purposes and cover different invalid configurations of the
 * TokenIssuer object.
 *
 * <p>This class should not be instantiated, as all methods are static.
 *
 * <p>This class does not provide any public members.
 *
 * @see TokenIssuer
 * @see AdvancedSystemType
 * @since 1.0
 */
// SUB_CONF_16
@SuppressWarnings("checkstyle:TypeName")
public class InvalidConfig_TokenIssuer {
    private static TokenIssuer issuer;

    /**
     * Retrieves a TokenIssuer object with No AdvancedElectronicSystem.
     *
     * <p>This method creates a new TokenIssuer object and sets the following properties:
     * - serviceProvider: "Test Inc."
     * - country: "DE"
     * - advancedElectronicSystem: null
     *
     * @return a TokenIssuer object with No AdvancedElectronicSystem
     */
    // SUB_CONF_16 Variant 1
    // Authentication Based & Signature Based - No AdvancedElectronicSystem
    public static TokenIssuer get_NoAdvancedElectronicSystem() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(null);
        issuer.setCountry("DE");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    /**
     * Retrieves a TokenIssuer object with Authentication Based and No Country.
     *
     * <p>This method creates a new TokenIssuer object and sets the following properties:
     * - serviceProvider: "Test Inc."
     * - country: null
     * - advancedElectronicSystem: Authentication-based
     *
     * @return a TokenIssuer object with Authentication Based and No Country
     */
    // SUB_CONF_16 Variant 2
    // Authentication Based - No Country
    public static TokenIssuer get_AuthBased_NoCountry() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.AUTHENTICATION_BASED);
        issuer.setCountry(null);
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    /**
     * Retrieves a TokenIssuer object with Authentication Based and No Service Provider.
     *
     * <p>This method creates a new TokenIssuer object and sets the following properties:
     * - serviceProvider: null
     * - country: "DE"
     * - advancedElectronicSystem: Authentication-based
     *
     * @return a TokenIssuer object with Authentication Based and No Service Provider
     */
    // SUB_CONF_16 Variant 3
    // Authentication Based - No Service Provider
    public static TokenIssuer get_AuthBased_NoServiceProvider() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.AUTHENTICATION_BASED);
        issuer.setCountry("DE");
        issuer.setServiceProvider(null);

        return issuer;
    }

    /**
     * Retrieves a TokenIssuer object with Authentication Based and a non-ISO-3166 country.
     *
     * <p>This method creates a new TokenIssuer object and sets the following properties:
     * - serviceProvider: "Test Inc."
     * - country: "Deutschland"
     * - advancedElectronicSystem: Authentication-based
     *
     * @return a TokenIssuer object with Authentication Based and a non-ISO-3166 country
     */
    // SUB_CONF_16 Variant 4
    // Authentication Based - Not-ISO-3166 Country
    public static TokenIssuer get_AuthBased_NoISOCountry() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.AUTHENTICATION_BASED);
        issuer.setCountry("Deutschland");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    /**
     * Retrieves a TokenIssuer object with Signature Based and No Country.
     *
     * <p>This method creates a new TokenIssuer object and sets the following properties:
     * - serviceProvider: "Test Inc."
     * - country: null
     * - advancedElectronicSystem: Signature-based
     *
     * @return a TokenIssuer object with Signature Based and No Country
     */
    // SUB_CONF_16 Variant 5
    // Signature Based - No Country
    public static TokenIssuer get_SigBased_NoCountry() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        issuer.setCountry(null);
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    /**
     * Retrieves a TokenIssuer object with Signature Based - No Service Provider.
     *
     * <p>This method creates a new TokenIssuer object and sets the following properties:
     * - serviceProvider: null
     * - country: "DE"
     * - advancedElectronicSystem: Signature-based
     *
     * @return a TokenIssuer object with Signature Based - No Service Provider
     */
    // SUB_CONF_16 Variant 6
    // Signature Based - No Service Provider
    public static TokenIssuer get_SigBased_NoServiceProvider() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        issuer.setCountry("DE");
        issuer.setServiceProvider(null);

        return issuer;
    }

    /**
     * Retrieves a TokenIssuer object with Signature Based and a non-ISO-3166 country.
     *
     * <p>This method creates a new TokenIssuer object and sets the following properties:
     * - serviceProvider: "Test Inc."
     * - country: "Deutschland"
     * - advancedElectronicSystem: Signature-based
     *
     * @return a TokenIssuer object with Signature Based and a non-ISO-3166 country
     */
    // SUB_CONF_16 Variant 7
    // Signature Based - Not-ISO-3166 Country
    public static TokenIssuer get_SigBased_NoISOCountry() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        issuer.setCountry("Deutschland");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }
}
