package eu.domibus.connector.controller.service;


import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.*;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * This service handles the technical transport state of a message
 * between the connector and a link partner (gw, client)
 */
public interface TransportStateService {
    /**
     * Sets the transport status for transports to GW
     *
     * @param transportState the transport status to set
     * @param transportId    contains the transportId
     */
    void updateTransportToGatewayStatus(TransportId transportId, DomibusConnectorTransportState transportState);

    /**
     * Sets the transport status for transport to backendClient
     *
     * @param transportState the transport status to set, contains also the transport id / connector message id
     * @param transportId    contains the transportId
     */
    void updateTransportToBackendClientStatus(TransportId transportId, DomibusConnectorTransportState transportState);


    void updateTransportStatus(DomibusConnectorTransportState transportState);

    /**
     * Creates new transport for the message
     *
     * @param message
     * @return
     */
    TransportId createTransportFor(
            DomibusConnectorMessage message,
            DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName);

    List<DomibusConnectorTransportStep> getPendingTransportsForLinkPartner(
            DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName);

    Optional<DomibusConnectorTransportStep> getTransportStepById(TransportId transportId);

    class TransportId {
        private java.lang.String transportId;

        public TransportId(java.lang.String transportId) {
            if (StringUtils.isEmpty(transportId)) {
                throw new IllegalArgumentException("TransportId is not allowed to be null or empty!");
            }
            this.transportId = transportId;
        }

        public java.lang.String getTransportId() {
            return transportId;
        }

        public void setTransportId(java.lang.String transportId) {
            this.transportId = transportId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(transportId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TransportId that = (TransportId) o;
            return Objects.equals(transportId, that.transportId);
        }

        @Override
        public java.lang.String toString() {
            return "TransportId{" +
                    "transportId='" + transportId + '\'' +
                    '}';
        }
    }

    public static class DomibusConnectorTransportState {
        private TransportId connectorTransportId; // may be the same as the connectorMessageId but must not...
        private DomibusConnectorMessageId connectorMessageId;
        private java.lang.String transportImplId; // the id of the transport attempt itself, can be null, eg. a jms id
        private java.lang.String remoteMessageId;
                // in case of GW ebms id, in case of backend national id/backend id, only filled if
        private TransportState status;
        private List<DomibusConnectorMessageError> messageErrorList = new ArrayList<>();
        private java.lang.String text;
        private DomibusConnectorLinkPartner linkPartner;

        public DomibusConnectorLinkPartner getLinkPartner() {
            return linkPartner;
        }

        public void setLinkPartner(DomibusConnectorLinkPartner linkPartner) {
            this.linkPartner = linkPartner;
        }

        public TransportState getStatus() {
            return status;
        }

        public void setStatus(TransportState status) {
            this.status = status;
        }

        public TransportId getConnectorTransportId() {
            return connectorTransportId;
        }

        public void setConnectorTransportId(TransportId connectorTransportId) {
            this.connectorTransportId = connectorTransportId;
        }

        public DomibusConnectorMessageId getConnectorMessageId() {
            return connectorMessageId;
        }

        public void setConnectorMessageId(DomibusConnectorMessageId connectorMessageId) {
            this.connectorMessageId = connectorMessageId;
        }

        public java.lang.String getRemoteMessageId() {
            return remoteMessageId;
        }

        public void setRemoteMessageId(java.lang.String remoteMessageId) {
            this.remoteMessageId = remoteMessageId;
        }

        public List<DomibusConnectorMessageError> getMessageErrorList() {
            return messageErrorList;
        }

        public void setMessageErrorList(List<DomibusConnectorMessageError> messageErrorList) {
            this.messageErrorList = messageErrorList;
        }

        public java.lang.String getTransportImplId() {
            return transportImplId;
        }

        public void setTransportImplId(java.lang.String transportImplId) {
            this.transportImplId = transportImplId;
        }

        public void addMessageError(DomibusConnectorMessageError error) {
            this.messageErrorList.add(error);
        }

        @Override
        public java.lang.String toString() {
            return new ToStringCreator(this)
                    .append("msgId", this.connectorTransportId)
                    .append("remote id", this.remoteMessageId)
                    .append("status", this.status)
                    .toString();
        }

        public java.lang.String getText() {
            return text;
        }

        public void setText(java.lang.String text) {
            this.text = text;
        }
    }
}
