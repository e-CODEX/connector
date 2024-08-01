/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.common.annotations.MapNested;
import eu.domibus.connector.lib.spring.configuration.validation.CheckResourceIsReadable;
import eu.domibus.connector.lib.spring.configuration.validation.CheckStoreIsLoadable;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.style.ToStringCreator;
import org.springframework.validation.annotation.Validated;

/**
 * The StoreConfigurationProperties class represents the configuration properties for a keystore or
 * truststore. It contains properties for the path to the store file, the password to open the
 * store, and the type of store. The class is annotated with the @Validated annotation to enable
 * validation of its properties. The @CheckStoreIsLoadable annotation is used to validate if a
 * StoreConfigurationProperties object can be loaded as a key store. The @MapNested annotation is
 * used to specify that every field in this class should be mapped as an individual property.
 *
 * <p>The @CheckResourceIsReadable annotation is used to validate whether the resource (store file)
 * is readable.
 */
@Validated
@CheckStoreIsLoadable
@MapNested
@Getter
@Setter
@NoArgsConstructor
public class StoreConfigurationProperties {
    private static final Logger LOGGER = LogManager.getLogger(StoreConfigurationProperties.class);
    /**
     * Path to the Key/Truststore.
     */
    @ConfigurationLabel("Path to key or truststore")
    @CheckResourceIsReadable
    private String path;
    /**
     * Password to open the Store.
     */
    @NotNull
    @ConfigurationLabel("Password of the key or truststore")
    private String password;
    @ConfigurationLabel("JavaKeystoreType - default JKS")
    private String type = "JKS";

    @Override
    public String toString() {
        ToStringCreator append = new ToStringCreator(this)
            .append("path", this.path)
            .append("type", this.type);

        if (LOGGER.getLevel().isMoreSpecificThan(Level.TRACE)) {
            append.append("password", "****");
        } else {
            append.append("password", this.password);
        }

        return append.toString();
    }
}
