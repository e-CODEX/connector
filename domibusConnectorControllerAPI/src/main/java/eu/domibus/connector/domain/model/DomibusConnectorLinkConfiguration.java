package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.ConfigurationSource;
import org.springframework.core.style.ToStringCreator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class DomibusConnectorLinkConfiguration {


    @NotBlank
    private LinkConfigName configName;

    private String linkImpl;

    private Map<String,String> properties = new HashMap<>();

    private ConfigurationSource configurationSource;

    public LinkConfigName getConfigName() {
        return configName;
    }

    public ConfigurationSource getConfigurationSource() {
        return configurationSource;
    }

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    public void setConfigName(LinkConfigName configName) {
        this.configName = configName;
    }

    public String getLinkImpl() {
        return linkImpl;
    }

    public void setLinkImpl(String linkImpl) {
        this.linkImpl = linkImpl;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public static class LinkConfigName {
        private String configName;

        public LinkConfigName(String configName) {
            this.configName = configName;
        }

        public String getConfigName() {
            return configName;
        }

        public void setConfigName(String configName) {
            this.configName = configName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LinkConfigName that = (LinkConfigName) o;
            return Objects.equals(configName, that.configName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(configName);
        }

        @Override
        public String toString() {
            return this.configName;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomibusConnectorLinkConfiguration that = (DomibusConnectorLinkConfiguration) o;
        return Objects.equals(configName, that.configName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configName);
    }

    public String toString() {
        return new ToStringCreator(this)
                .append("ConfigName", this.configName)
                .append("Plugin", this.linkImpl)
                .toString();
    }
}
