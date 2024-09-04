/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.dss.configuration;

import eu.ecodex.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the configuration properties for signature configuration.
 *
 * <p>This class extends the KeyAndKeyStoreConfigurationProperties class and inherits its
 * properties and methods.
 * It also adds two additional properties:
 * - encryptionAlgorithm:
 * The encryption algorithm to be used for signature.
 * - digestAlgorithm: The digest algorithm to be used for signature.
 */
@Getter
@Setter
public class SignatureConfigurationProperties extends KeyAndKeyStoreConfigurationProperties {
    @NotNull
    EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.RSA;
    @NotNull
    DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA256;
}
