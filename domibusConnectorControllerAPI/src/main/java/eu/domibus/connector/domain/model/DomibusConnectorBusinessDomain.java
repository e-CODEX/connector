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
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The DomibusConnectorBusinessDomain class represents a business domain in Domibus. A business
 * domain is a logical grouping of messaging configurations and properties.
 *
 * <p>This class has the following properties: - id: The unique identifier of the business domain.
 * - description: A description of the business domain. - enabled: A flag indicating whether the
 * business domain is enabled or not. - messageLaneProperties: A map of message lane properties. -
 * configurationSource: The source of configuration for the business domain.
 *
 * <p>This class also contains a nested class BusinessDomainId, which represents the identifier of
 * a business domain.
 *
 * <p>Usage example:
 */
@Data
@NoArgsConstructor
public class DomibusConnectorBusinessDomain {
    public static final String DEFAULT_LANE_NAME = "defaultBusinessDomain";
    private BusinessDomainId id;
    private String description;
    private boolean enabled;
    private Map<String, String> messageLaneProperties = new HashMap<>();
    private ConfigurationSource configurationSource;

    /**
     * Returns the default message lane for the Domibus Connector.
     *
     * @return the default message lane as a DomibusConnectorBusinessDomain object
     */
    public static DomibusConnectorBusinessDomain getDefaultMessageLane() {
        var defaultMessageLane = new DomibusConnectorBusinessDomain();
        defaultMessageLane.setId(new BusinessDomainId(DEFAULT_LANE_NAME));
        defaultMessageLane.setDescription("default message lane");
        defaultMessageLane.setMessageLaneProperties(new HashMap<>());
        return defaultMessageLane;
    }

    public static BusinessDomainId getDefaultMessageLaneId() {
        return getDefaultMessageLane().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DomibusConnectorBusinessDomain that)) {
            return false;
        }

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * Represents a unique identifier for a business domain.
     *
     * <p>The BusinessDomainId class is a value object that holds the message lane identifier for a
     * business domain. It provides methods for creating an instance of the class, getting and
     * setting the message lane identifier, and comparing two instances for equality.
     *
     * @since 1.0
     */
    @Data
    @NoArgsConstructor
    public static class BusinessDomainId implements Serializable {
        public BusinessDomainId(String id) {
            this.messageLaneId = id;
        }

        @NotBlank
        private String messageLaneId;

        @Override
        public String toString() {
            return String.format("MessageLaneId: [%s]", this.messageLaneId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BusinessDomainId that)) {
                return false;
            }

            return messageLaneId != null ? messageLaneId.equals(that.messageLaneId) :
                that.messageLaneId == null;
        }

        @Override
        public int hashCode() {
            return messageLaneId != null ? messageLaneId.hashCode() : 0;
        }
    }
}
