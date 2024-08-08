/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import static eu.domibus.connector.persistence.model.PDomibusConnectorTransportStep.TABLE_NAME;

import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The PDomibusConnectorTransportStep class represents a transport step in the Domibus Connector. It
 * contains information about the transport process for a specific message.
 */
@Getter
@Setter
@Entity
@Table(name = TABLE_NAME)
@ToString
public class PDomibusConnectorTransportStep {
    public static final String TABLE_NAME = "DC_TRANSPORT_STEP";
    @Id
    @Column(name = "ID")
    @TableGenerator(
        name = "seqTransportStep",
        table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
        pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
        pkColumnValue = TABLE_NAME + ".ID",
        valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
        initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
        allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE_BULK
    )
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
     * The message id of the system used to transport the message e.g. jms-message-id,
     * webRequestNumber,...
     */
    @Column(name = "TRANSPORT_SYSTEM_MESSAGE_ID")
    private String transportSystemMessageId;
    @Column(name = "REMOTE_MESSAGE_ID")
    private String remoteMessageId;
    @Column(name = "CREATED", nullable = false)
    private LocalDateTime created;
    /**
     * Will be set to the date when the final state has been reached.
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
}
