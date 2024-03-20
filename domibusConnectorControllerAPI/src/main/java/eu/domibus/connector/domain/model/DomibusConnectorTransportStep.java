package eu.domibus.connector.domain.model;

import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;

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
    private PriorityQueue<DomibusConnectorTransportStepStatusUpdate> statusUpdates = new PriorityQueue<>(new TransportStepComparator());
    private LocalDateTime finalStateReached;

    public Optional<DomibusConnectorMessage> getTransportedMessage() {
        return Optional.ofNullable(transportedMessage);
    }

    public void setTransportedMessage(DomibusConnectorMessage transportedMessage) {
        if (transportedMessage == null) {
            throw new IllegalArgumentException("The transported message is not allowed to be null");
        }
        if (transportedMessage.getConnectorMessageId() == null) {
            throw new IllegalArgumentException("The connectorMessageId of the transported message must not be null!");
        }
        this.transportedMessage = transportedMessage;
        this.connectorMessageIdOfTransportedMsg = transportedMessage.getConnectorMessageId();

    }

    public TransportStateService.TransportId getTransportId() {
        return transportId;
    }

    public void setTransportId(TransportStateService.TransportId transportId) {
        this.transportId = transportId;
    }

    public DomibusConnectorLinkPartner.LinkPartnerName getLinkPartnerName() {
        return linkPartnerName;
    }

    public void setLinkPartnerName(DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {
        this.linkPartnerName = linkPartnerName;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public java.lang.String getTransportSystemMessageId() {
        return transportSystemMessageId;
    }

    public void setTransportSystemMessageId(java.lang.String transportSystemMessageId) {
        this.transportSystemMessageId = transportSystemMessageId;
    }

    public java.lang.String getRemoteMessageId() {
        return remoteMessageId;
    }

    public void setRemoteMessageId(java.lang.String remoteMessageId) {
        this.remoteMessageId = remoteMessageId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
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
            java.lang.String error = java.lang.String.format("Cannot add stepStatusUpdate with state [%s] because there is already a state with higher or equal priority of [%s]!", stepStatusUpdate.getTransportState(), lastPriority);
            throw new IllegalArgumentException(error);
        }

    }

    public LocalDateTime getFinalStateReached() {
        return finalStateReached;
    }

    public void setFinalStateReached(LocalDateTime setFinalStateReached) {
        this.finalStateReached = setFinalStateReached;
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

    public void setConnectorMessageId(DomibusConnectorMessageId transportedMessageConnectorMessageId) {
        this.connectorMessageIdOfTransportedMsg = transportedMessageConnectorMessageId;
        if (this.transportedMessage != null
                && this.transportedMessage.getConnectorMessageId() != null
                && !this.transportedMessage.getConnectorMessageId().equals(transportedMessageConnectorMessageId)) {
            throw new IllegalArgumentException("Cannot set a different connector message id here!");
        }
    }

    /**
     * Compares first by created time
     * and then by priority
     */
    private static class TransportStepComparator implements Comparator<DomibusConnectorTransportStepStatusUpdate> {

        @Override
        public int compare(DomibusConnectorTransportStepStatusUpdate o1, DomibusConnectorTransportStepStatusUpdate o2) {
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
                return comp;    //if timestamp is enough return comparision
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

    public static class DomibusConnectorTransportStepStatusUpdate {

        private TransportState transportState;

        private LocalDateTime created;

        private java.lang.String text;

        public TransportState getTransportState() {
            return transportState;
        }

        public void setTransportState(TransportState transportState) {
            this.transportState = transportState;
        }

        public LocalDateTime getCreated() {
            return created;
        }

        public void setCreated(LocalDateTime created) {
            this.created = created;
        }

        public java.lang.String getText() {
            return text;
        }

        public void setText(java.lang.String text) {
            this.text = text;
        }

    }

}
