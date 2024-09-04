/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.lib.spring.configuration;

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
