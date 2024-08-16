/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.service;

import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

/**
 * This service handles the technical transport state of a message between the connector and a link
 * partner (gw, client).
 */
public interface TransportStateService {
    /**
     * Sets the transport status for transports to GW.
     *
     * @param transportState the transport status to set
     * @param transportId    contains the transportId
     */
    void updateTransportToGatewayStatus(TransportId transportId,
                                        DomibusConnectorTransportState transportState);

    /**
     * Sets the transport status for transport to backendClient.
     *
     * @param transportState the transport status to set, contains also the transport id / connector
     *                       message id
     * @param transportId    contains the transportId
     */
    void updateTransportToBackendClientStatus(TransportId transportId,
                                              DomibusConnectorTransportState transportState);

    void updateTransportStatus(DomibusConnectorTransportState transportState);

    /**
     * Creates a transport ID for the given connector message and link partner name.
     *
     * @param message         the connector message for which to create the transport ID
     * @param linkPartnerName the name of the link partner for which to create the transport ID
     * @return the created transport ID
     */
    TransportId createTransportFor(DomibusConnectorMessage message,
                                   DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName);

    List<DomibusConnectorTransportStep> getPendingTransportsForLinkPartner(
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName);

    Optional<DomibusConnectorTransportStep> getTransportStepById(TransportId transportId);

    /**
     * The TransportId class represents a transport identifier.
     */
    @Data
    @NoArgsConstructor
    class TransportId {
        private java.lang.String transportId;

        /**
         * Creates a new instance of the TransportId class.
         *
         * @param transportId the transport identifier
         * @throws IllegalArgumentException if the transportId is null or empty
         */
        public TransportId(java.lang.String transportId) {
            if (!StringUtils.hasLength(transportId)) {
                throw new IllegalArgumentException(
                    "TransportId is not allowed to be null or empty!");
            }
            this.transportId = transportId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TransportId that = (TransportId) o;
            return Objects.equals(transportId, that.transportId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(transportId);
        }

        @Override
        public java.lang.String toString() {
            return "TransportId{" + "transportId='" + transportId + '\'' + '}';
        }
    }

    /**
     * The DomibusConnectorTransportState class represents the state of transport in the Domibus
     * Connector.
     */
    @Data
    @NoArgsConstructor
    class DomibusConnectorTransportState {
        private TransportId connectorTransportId;
        // may be the same as the connectorMessageId but must not...
        private DomibusConnectorMessageId connectorMessageId;
        private java.lang.String transportImplId;
        // the id of the transport attempt itself, can be null, eg. a jms id
        private java.lang.String remoteMessageId;
        // in case of GW ebms id, in case of backend national id/backend id, only filled if
        private TransportState status;
        private List<DomibusConnectorMessageError> messageErrorList = new ArrayList<>();
        private java.lang.String text;
        private DomibusConnectorLinkPartner linkPartner;

        @Override
        public java.lang.String toString() {
            return new ToStringCreator(this)
                .append("msgId", this.connectorTransportId)
                .append("remote id", this.remoteMessageId)
                .append("status", this.status)
                .toString();
        }
    }
}
