/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import jakarta.annotation.Nullable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * Configuration properties for referencing a key in a key store an alias and an optional password.
 */
@Data
@NoArgsConstructor
public class KeyConfigurationProperties {
    public KeyConfigurationProperties(String alias, String password) {
        this.alias = alias;
        this.password = password;
    }

    /**
     * The alias of the Certificate/Key.
     */
    @ConfigurationLabel("Alias of certificate or key")
    @NotNull(message = "an alias must be provided!")
    @Length(min = 1, message = "Alias must have at least one character!")
    String alias;
    /**
     * The password of the Certificate/Key.
     */
    @NotNull
    @ConfigurationLabel("Password of key")
    String password = "";

    public @Nullable String getPassword() {
        return password;
    }
}
