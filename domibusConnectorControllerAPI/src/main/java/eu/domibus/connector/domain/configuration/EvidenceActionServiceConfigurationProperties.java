package eu.domibus.connector.domain.configuration;

import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * This property class is meant to be loaded over the
 * {@link ConfigurationPropertyManagerService} so also
 * MessageLane specific properties are taken into account
 */
@ConfigurationProperties(prefix = "connector.confirmation-messages")
@Data
public class EvidenceActionServiceConfigurationProperties {

    private boolean enforceServiceActionNames = false;

    @NestedConfigurationProperty
    private EvidenceServiceAction relayREEMDAcceptance = new EvidenceServiceAction(new AS4Action("RelayREMMDAcceptanceRejection"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction relayREEMDRejection = new EvidenceServiceAction(new AS4Action("RelayREMMDAcceptanceRejection"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction relayREMMDFailure = new EvidenceServiceAction(new AS4Action("RelayREMMDFailure"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction delivery = new EvidenceServiceAction(new AS4Action("DeliveryNonDeliveryToRecipient"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction nonDelivery = new EvidenceServiceAction(new AS4Action("DeliveryNonDeliveryToRecipient"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction nonRetrieval = new EvidenceServiceAction(new AS4Action("RetrievalNonRetrievalToRecipient"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction retrieval = new EvidenceServiceAction(new AS4Action("RetrievalNonRetrievalToRecipient"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction submissionAcceptance = new EvidenceServiceAction(new AS4Action("SubmissionAcceptanceRejection"), null);
    @NestedConfigurationProperty
    private EvidenceServiceAction submissionRejection = new EvidenceServiceAction(new AS4Action("SubmissionAcceptanceRejection"), null);

    public static class EvidenceServiceAction {
        @Valid
        @NotNull
        private EvidenceActionServiceConfigurationProperties.AS4Action action;

        @Valid
        @NestedConfigurationProperty
        private EvidenceActionServiceConfigurationProperties.AS4Service service;

        public EvidenceServiceAction() {}

        public EvidenceServiceAction(AS4Action action, AS4Service service) {
            this.action = action;
            this.service = service;
        }

        public AS4Action getAction() {
            return action;
        }

        public void setAction(AS4Action action) {
            this.action = action;
        }

        public AS4Service getService() {
            return service;
        }

        public void setService(AS4Service service) {
            this.service = service;
        }

        @Nullable
        public DomibusConnectorAction getConnectorAction() {
            if (this.action == null) {
                return null;
            }
            return this.action.getConnectorAction();
        }

        @Nullable
        public DomibusConnectorService getConnectorService() {
            if (this.service == null) {
                return null;
            }
            return this.getService().getConnectorService();
        }

    }

    @Validated
    @Valid
    public static class AS4Action {
        @NotBlank
        private String action;

        public AS4Action() {}

        public AS4Action(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public DomibusConnectorAction getConnectorAction() {
            return new DomibusConnectorAction(this.action);
        }
    }

    @Validated
    @Valid
    public static class AS4Service {
        @NotBlank
        private String name;

        private String serviceType;

        public AS4Service() {

        }

        public AS4Service(String name, String serviceType) {
            this.name = name;
            this.serviceType = serviceType;
        }
        
        public AS4Service(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public DomibusConnectorService getConnectorService() {
            return new DomibusConnectorService(this.name, this.serviceType);
        }
    }

}
