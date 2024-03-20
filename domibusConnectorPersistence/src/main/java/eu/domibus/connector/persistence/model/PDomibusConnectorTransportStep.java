package eu.domibus.connector.persistence.model;


import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static eu.domibus.connector.persistence.model.PDomibusConnectorTransportStep.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME)
@ToString
public class PDomibusConnectorTransportStep {

    public static final String TABLE_NAME = "DC_TRANSPORT_STEP";

    @Id
    @Column(name="ID")
    @TableGenerator(name = "seqTransportStep",
            table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
            pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
            pkColumnValue = TABLE_NAME + ".ID",
            valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
            initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
            allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE_BULK)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqTransportStep")
    private Long id;

//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Column(name = "CONNECTOR_MESSAGE_ID", nullable = false)
    private String connectorMessageId;

    @Column(name = "LINK_PARTNER_NAME", nullable = false)
    private DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName;

    @Column(name = "ATTEMPT", nullable = false)
    private int attempt = 1;

    @Column(name = "TRANSPORT_ID")
    private TransportStateService.TransportId transportId;

    @Column(name = "TRANSPORTED_MESSAGE")
    @Lob
    private String transportedMessage;

    /**
     * The message id of the system used to transport the message
     * eg. jms-message-id, webRequestNumber,...
     */
    @Column(name = "TRANSPORT_SYSTEM_MESSAGE_ID")
    private String transportSystemMessageId;

    @Column(name = "REMOTE_MESSAGE_ID")
    private String remoteMessageId;

    @Column(name = "CREATED", nullable = false)
    private LocalDateTime created;

    /**
     * will be set to the date
     * when the final state has been reached
     *
     */
    @Column(name = "FINAL_STATE_REACHED")
    private LocalDateTime finalStateReached;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transportStep", fetch = FetchType.EAGER)
    private List<PDomibusConnectorTransportStepStatusUpdate> statusUpdates = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        statusUpdates.forEach(u -> u.setTransportStep(this));
        created = LocalDateTime.now();
    }

    public TransportStateService.TransportId getTransportId() {
        return transportId;
    }

    public void setTransportId(TransportStateService.TransportId transportId) {
        this.transportId = transportId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConnectorMessageId() {
        return connectorMessageId;
    }

    public void setConnectorMessageId(String message) {
        this.connectorMessageId = message;
    }

    public DomibusConnectorLinkPartner.LinkPartnerName getLinkPartnerName() {
        return linkPartnerName;
    }

    public void setLinkPartnerName(DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {
        this.linkPartnerName = linkPartnerName;
    }

    public LocalDateTime getFinalStateReached() {
        return finalStateReached;
    }

    public void setFinalStateReached(LocalDateTime finalStateReached) {
        this.finalStateReached = finalStateReached;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public String getTransportSystemMessageId() {
        return transportSystemMessageId;
    }

    public void setTransportSystemMessageId(String transportSystemMessageId) {
        this.transportSystemMessageId = transportSystemMessageId;
    }

    public String getRemoteMessageId() {
        return remoteMessageId;
    }

    public void setRemoteMessageId(String remoteMessageId) {
        this.remoteMessageId = remoteMessageId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<PDomibusConnectorTransportStepStatusUpdate> getStatusUpdates() {
        return statusUpdates;
    }

    public void setStatusUpdates(List<PDomibusConnectorTransportStepStatusUpdate> statusUpdates) {
        this.statusUpdates = statusUpdates;
    }

    public String getTransportedMessage() {
        return transportedMessage;
    }

    public void setTransportedMessage(String transportedMessage) {
        this.transportedMessage = transportedMessage;
    }
}
