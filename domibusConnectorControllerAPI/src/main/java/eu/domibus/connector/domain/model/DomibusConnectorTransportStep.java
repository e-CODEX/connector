/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The DomibusConnectorTransportStep class represents a step in the transport process of a message
 * in Domibus. It encapsulates information about the transported message, transport ID, link partner
 * name, attempt count, transport system message ID, remote message ID, creation time, status
 * updates, and final state reached time. It provides methods to get and set the transported
 * message, transport ID, link partner name, attempt count, transport system message ID, remote
 * message ID, creation time, status updates, and final state reached time. It also provides methods
 * to add a status update, get the connector message ID, check if the step is in a specific state,
 * get the last status update, set the connector message ID, and check if the step is in a pending
 * state.
 */
@Data
@NoArgsConstructor
public class DomibusConnectorTransportStep {
    private TransportStateService.TransportId transportId;
    @Nullable
    private DomibusConnectorMessage transportedMessage = null;
    private DomibusConnectorMessageId connectorMessageIdOfTransportedMsg;
    private DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName;
    private int attempt = -1;
    private java.lang.String transportSystemMessageId;
    private java.lang.String remoteMessageId;
    private LocalDateTime created;
    private PriorityQueue<DomibusConnectorTransportStepStatusUpdate> statusUpdates =
        new PriorityQueue<>(new TransportStepComparator());
    private LocalDateTime finalStateReached;

    public Optional<DomibusConnectorMessage> getTransportedMessage() {
        return Optional.ofNullable(transportedMessage);
    }

    /**
     * Sets the transported message for this DomibusConnectorTransportStep.
     *
     * @param transportedMessage The DomibusConnectorMessage to be set as the transported message.
     * @throws IllegalArgumentException if the transportedMessage is null or the connectorMessageId
     *                                  is null.
     */
    public void setTransportedMessage(DomibusConnectorMessage transportedMessage) {
        if (transportedMessage == null) {
            throw new IllegalArgumentException("The transported message is not allowed to be null");
        }
        if (transportedMessage.getConnectorMessageId() == null) {
            throw new IllegalArgumentException(
                "The connectorMessageId of the transported message must not be null!");
        }
        this.transportedMessage = transportedMessage;
        this.connectorMessageIdOfTransportedMsg = transportedMessage.getConnectorMessageId();
    }

    public void addStatusUpdate(DomibusConnectorTransportStepStatusUpdate u) {
        this.statusUpdates.add(u);
    }

    public DomibusConnectorMessageId getConnectorMessageId() {
        return this.connectorMessageIdOfTransportedMsg;
    }

    public List<DomibusConnectorTransportStepStatusUpdate> getStatusUpdates() {
        return new ArrayList<>(statusUpdates);
    }

    public void setStatusUpdates(List<DomibusConnectorTransportStepStatusUpdate> statusUpdates) {
        this.statusUpdates.addAll(statusUpdates);
    }

    /**
     * Adds a transport status update to the current transport step.
     *
     * @param stepStatusUpdate The status update to be added.
     * @throws IllegalArgumentException If the stepStatusUpdate has a priority lower or equal to the
     *                                  existing status updates.
     */
    public void addTransportStatus(DomibusConnectorTransportStepStatusUpdate stepStatusUpdate) {
        if (stepStatusUpdate.getTransportState().getPriority() >= 10) {
            this.finalStateReached = LocalDateTime.now();
        }

        int lastPriority = Integer.MIN_VALUE;
        DomibusConnectorTransportStepStatusUpdate peek = this.statusUpdates.peek();
        if (peek != null) {
            lastPriority = peek.getTransportState().getPriority();
        }

        if (stepStatusUpdate.getTransportState().getPriority() > lastPriority) {
            this.statusUpdates.add(stepStatusUpdate);
        } else {
            java.lang.String error = java.lang.String.format(
                "Cannot add stepStatusUpdate with state [%s] because there is already a state "
                    + "with higher or equal priority of [%s]!",
                stepStatusUpdate.getTransportState(), lastPriority
            );
            throw new IllegalArgumentException(error);
        }
    }

    public boolean isInPendingState() {
        TransportState state = TransportState.PENDING;
        return isInState(state);
    }

    public boolean isInPendingDownloadedState() {
        TransportState state = TransportState.PENDING_DOWNLOADED;
        return isInState(state);
    }

    public boolean isInAcceptedState() {
        TransportState state = TransportState.ACCEPTED;
        return isInState(state);
    }

    private boolean isInState(TransportState state) {
        DomibusConnectorTransportStepStatusUpdate lastState = this.statusUpdates.peek();
        return lastState != null && lastState.getTransportState() == state;
    }

    public DomibusConnectorTransportStepStatusUpdate getLastStatusUpdate() {
        return this.statusUpdates.peek();
    }

    /**
     * Sets the connector message ID for the transported message in the
     * DomibusConnectorTransportStep.
     *
     * @param transportedMessageConnectorMessageId The DomibusConnectorMessageId to be set as the
     *                                             connector message ID of the transported message.
     * @throws IllegalArgumentException If the transportedMessage is null or the connectorMessageId
     *                                  is different from the connector message ID of the
     *                                  transported message.
     */
    public void setConnectorMessageId(
        DomibusConnectorMessageId transportedMessageConnectorMessageId) {
        this.connectorMessageIdOfTransportedMsg = transportedMessageConnectorMessageId;
        if (this.transportedMessage != null
            && this.transportedMessage.getConnectorMessageId() != null
            && !this.transportedMessage.getConnectorMessageId()
            .equals(transportedMessageConnectorMessageId)) {
            throw new IllegalArgumentException("Cannot set a different connector message id here!");
        }
    }

    /**
     * A comparator used to sort {@link DomibusConnectorTransportStepStatusUpdate} objects based on
     * their transport step information. The sorting is done in the following order: 1. The created
     * timestamp in descending order. If the created timestamp is the same, then 2. The transport
     * state priority in ascending order.
     */
    private static class TransportStepComparator
        implements Comparator<DomibusConnectorTransportStepStatusUpdate> {
        @Override
        public int compare(DomibusConnectorTransportStepStatusUpdate o1,
                           DomibusConnectorTransportStepStatusUpdate o2) {
            LocalDateTime time1 = LocalDateTime.MIN;
            if (o1.getCreated() != null) {
                time1 = o1.getCreated();
            }
            LocalDateTime time2 = LocalDateTime.MIN;
            if (o2.getCreated() != null) {
                time2 = o2.getCreated();
            }
            int comp = time2.compareTo(time1);
            if (comp != 0) {
                return comp;    // if timestamp is enough return comparison
            }
            TransportState state1 = TransportState.PENDING;
            TransportState state2 = TransportState.PENDING;
            if (o1.getTransportState() != null) {
                state1 = o1.getTransportState();
            }
            if (o2.getTransportState() != null) {
                state2 = o2.getTransportState();
            }
            return state1.getPriority() - state2.getPriority();
        }
    }

    /**
     * Represents the status update of a transport step in the Domibus Connector. It contains
     * information about the transport state, the creation timestamp, and additional text.
     */
    @Data
    public static class DomibusConnectorTransportStepStatusUpdate {
        private TransportState transportState;
        private LocalDateTime created;
        private java.lang.String text;
    }
}
