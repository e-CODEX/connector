package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.common.annotations.MapNested;
import eu.domibus.connector.lib.spring.configuration.validation.CheckResourceIsReadable;
import eu.domibus.connector.lib.spring.configuration.validation.CheckStoreIsLoadable;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.style.ToStringCreator;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;


@Validated
@CheckStoreIsLoadable
@MapNested
public class StoreConfigurationProperties {

    private static final Logger LOGGER = LogManager.getLogger(StoreConfigurationProperties.class);

    /**
     * Path to the Key/Truststore
     */
    @ConfigurationLabel("Path to key or truststore")
    @CheckResourceIsReadable
    private String path;

    /**
     * Password to open the Store
     */
    @NotNull
    @ConfigurationLabel("Password of the key or truststore")
    private String password;

    @ConfigurationLabel("JavaKeystoreType - default JKS")
    private String type = "JKS";

    public StoreConfigurationProperties() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
