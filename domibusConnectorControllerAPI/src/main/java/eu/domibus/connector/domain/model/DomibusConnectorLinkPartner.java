/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkMode;
import eu.domibus.connector.domain.enums.LinkType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;

/**
 * The DomibusConnectorLinkPartner class represents a link partner in the Domibus Connector. It
 * contains various properties and configurations related to the link partner.
 */
@Data
@NoArgsConstructor
public class DomibusConnectorLinkPartner {
    @Valid
    private LinkPartnerName linkPartnerName;
    private String description;
    private boolean enabled;
    // allowed LinkMode.PASSIVE or LinkMode.PULL
    private LinkMode rcvLinkMode = LinkMode.PASSIVE;
    // allowed LinkMode.PASSIVE or LinkMode.PUSH
    private LinkMode sendLinkMode = LinkMode.PASSIVE;
    private LinkType linkType;
    private Duration pullInterval = Duration.ofMinutes(5L);
    private Map<String, String> properties = new HashMap<>();
    private DomibusConnectorLinkConfiguration linkConfiguration;
    private ConfigurationSource configurationSource;

    /**
     * LinkPartnerName represents the name of a link partner in the Domibus application.
     */
    @Data
    @NoArgsConstructor
    public static class LinkPartnerName {
        @NotBlank
        private String linkName;

        public LinkPartnerName(String linkName) {
            this.linkName = linkName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LinkPartnerName that = (LinkPartnerName) o;
            return Objects.equals(linkName, that.linkName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(linkName);
        }

        @Override
        public String toString() {
            return this.linkName;
        }
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("linkName", this.linkPartnerName)
            .toString();
    }
}
