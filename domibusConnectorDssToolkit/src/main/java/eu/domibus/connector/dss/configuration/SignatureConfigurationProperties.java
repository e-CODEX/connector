/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.dss.configuration;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import javax.validation.constraints.NotNull;
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
