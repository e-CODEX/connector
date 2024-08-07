/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.configuration;

import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import jakarta.annotation.Nullable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * This property class is meant to be loaded over the
 * {@link ConfigurationPropertyManagerService} so also
 * MessageLane specific properties are taken into account.
 */
@ConfigurationProperties(prefix = "connector.confirmation-messages")
@Data
@NoArgsConstructor
public class EvidenceActionServiceConfigurationProperties {
    private boolean enforceServiceActionNames = false;
    @NestedConfigurationProperty
    private EvidenceServiceAction relayREEMDAcceptance =
        new EvidenceServiceAction(new AS4Action("RelayREMMDAcceptanceRejection"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction relayREEMDRejection =
        new EvidenceServiceAction(new AS4Action("RelayREMMDAcceptanceRejection"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction relayREMMDFailure =
        new EvidenceServiceAction(new AS4Action("RelayREMMDFailure"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction delivery =
        new EvidenceServiceAction(new AS4Action("DeliveryNonDeliveryToRecipient"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction nonDelivery =
        new EvidenceServiceAction(new AS4Action("DeliveryNonDeliveryToRecipient"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction nonRetrieval =
        new EvidenceServiceAction(new AS4Action("RetrievalNonRetrievalToRecipient"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction retrieval =
        new EvidenceServiceAction(new AS4Action("RetrievalNonRetrievalToRecipient"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction submissionAcceptance =
        new EvidenceServiceAction(new AS4Action("SubmissionAcceptanceRejection"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction submissionRejection =
        new EvidenceServiceAction(new AS4Action("SubmissionAcceptanceRejection"), null);

    /**
     * The EvidenceServiceAction class represents an action to be taken for a specific service
     * in the evidence module. It contains the AS4 action and the AS4 service.
     */
    @Data
    @NoArgsConstructor
    public static class EvidenceServiceAction {
        @Valid
        @NotNull
        private EvidenceActionServiceConfigurationProperties.AS4Action action;
        @Valid
        @NestedConfigurationProperty
        private EvidenceActionServiceConfigurationProperties.AS4Service service;

        public EvidenceServiceAction(AS4Action action, AS4Service service) {
            this.action = action;
            this.service = service;
        }

        /**
         * Retrieves the connector action from the EvidenceServiceAction object.
         *
         * @return the connector action, or null if the action is null
         */
        @Nullable
        public DomibusConnectorAction getConnectorAction() {
            if (this.action == null) {
                return null;
            }
            return this.action.getConnectorAction();
        }

        /**
         * Retrieves the DomibusConnectorService from the EvidenceServiceAction object.
         *
         * @return the DomibusConnectorService, or null if the service is null
         */
        @Nullable
        public DomibusConnectorService getConnectorService() {
            if (this.service == null) {
                return null;
            }
            return this.getService().getConnectorService();
        }
    }

    /**
     * The AS4Action class represents an AS4 action.
     */
    @Validated
    @Valid
    @Data
    @NoArgsConstructor
    public static class AS4Action {
        @NotBlank
        private String action;

        public AS4Action(String action) {
            this.action = action;
        }

        public DomibusConnectorAction getConnectorAction() {
            return new DomibusConnectorAction(this.action);
        }
    }

    /**
     * AS4Service class represents a service in the AS4 module.
     */
    @Validated
    @Valid
    @Data
    @NoArgsConstructor
    public static class AS4Service {
        @NotBlank
        private String name;
        private String serviceType;

        public AS4Service(String name, String serviceType) {
            this.name = name;
            this.serviceType = serviceType;
        }

        public AS4Service(String name) {
            this.name = name;
        }

        public DomibusConnectorService getConnectorService() {
            return new DomibusConnectorService(this.name, this.serviceType);
        }
    }
}
