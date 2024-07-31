/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.ConfigurationSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;

/**
 * The DomibusConnectorLinkConfiguration class represents the configuration for a link connector in
 * Domibus.
 */
@Data
@NoArgsConstructor
public class DomibusConnectorLinkConfiguration {
    @NotBlank
    private LinkConfigName configName;
    private String linkImpl;
    private Map<String, String> properties = new HashMap<>();
    private ConfigurationSource configurationSource;

    /**
     * The LinkConfigName class represents the name of a link configuration.
     */
    @Data
    @NoArgsConstructor
    public static class LinkConfigName {
        private String configName;

        public LinkConfigName(String configName) {
            this.configName = configName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DomibusConnectorLinkConfiguration that = (DomibusConnectorLinkConfiguration) o;
        return Objects.equals(configName, that.configName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configName);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("ConfigName", this.configName)
            .append("Plugin", this.linkImpl)
            .toString();
    }
}
