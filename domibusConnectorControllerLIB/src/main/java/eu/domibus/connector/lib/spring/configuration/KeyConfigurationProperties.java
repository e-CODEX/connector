package eu.domibus.connector.lib.spring.configuration;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import org.hibernate.validator.constraints.Length;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Configuration properties for referencing a
 *  key in a key store
 *   a alias and a optional password
 */
public class KeyConfigurationProperties {

    public KeyConfigurationProperties() {}

    public KeyConfigurationProperties(String alias, String password) {
        this.alias = alias;
        this.password = password;
    }

    /**
     * The alias of the Certificate/Key
     */
    @ConfigurationLabel("Alias of certificate or key")
    @NotNull(message = "an alias must be provided!")
    @Length(min = 1, message = "Alias must have at least one character!")
    String alias;

    /**
     * The password of the Certificate/Key
     */
    @NotNull
    @ConfigurationLabel("Password of key")
    String password = "";

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public @Nullable String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
