/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.model;

import static eu.ecodex.connector.persistence.model.PDomibusConnectorTransportStep.TABLE_NAME;

import eu.ecodex.connector.controller.service.TransportStateService;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
