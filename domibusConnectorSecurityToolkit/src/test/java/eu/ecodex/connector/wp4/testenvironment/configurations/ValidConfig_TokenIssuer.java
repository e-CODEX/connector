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
 * This class provides methods to create a TokenIssuer object with specific configurations.
 */
// SUB_CONF_15
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_TokenIssuer {
    private static TokenIssuer issuer;

    /**
     * Retrieves a TokenIssuer object with specific configurations for full authentication-based
     * advanced electronic system.
     *
     * <p>The TokenIssuer object is created and initialized with the following values:
     * - serviceProvider: "Test Inc."
     * - country: "DE"
     * - advancedElectronicSystem: AdvancedSystemType.AUTHENTICATION_BASED
     *
     * @return the TokenIssuer object with full authentication-based configuration
     */
    // SUB_CONF_15 Variant 1
    public static TokenIssuer get_FullAuthenticationBased() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.AUTHENTICATION_BASED);
        issuer.setCountry("DE");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    /**
     * Retrieves a TokenIssuer object with specific configurations for full signature-based
     * advanced electronic system.
     *
     * <p>The TokenIssuer object is created and initialized with the following values:
     * - serviceProvider: "Test Inc."
     * - country: "DE"
     * - advancedElectronicSystem: AdvancedSystemType.SIGNATURE_BASED
     *
     * @return the TokenIssuer object with full signature-based configuration
     */
    // SUB_CONF_15 Variant 2
    public static TokenIssuer get_FullSignatureBased() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        issuer.setCountry("DE");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }
}
