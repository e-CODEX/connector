/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.persistence.model.converter.TransportStateJpaConverter;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * The PDomibusConnectorTransportStepStatusUpdate class represents a status update for a transport
 * step in Domibus connector.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorTransportStepStatusUpdate.TABLE_NAME)
@IdClass(PDomibusConnectorTransportStepStatusUpdateIdClass.class)
public class PDomibusConnectorTransportStepStatusUpdate {
    public static final java.lang.String TABLE_NAME = "DC_TRANSPORT_STEP_STATUS";
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @Id
    @JoinColumn(name = "TRANSPORT_STEP_ID", referencedColumnName = "ID")
    @MapsId
    private PDomibusConnectorTransportStep transportStep;
    @Id
    @Column(name = "STATE", nullable = false)
    //@Convert(converter = TransportStateJpaConverter.class)
    // does not work because it is part of ID!
    // instead convert within setter/getter!
    private String transportStateString;
    @Column(name = "CREATED", nullable = false)
    private LocalDateTime created;
    @Lob
    @Column(name = "TEXT")
    private java.lang.String text;

    @PrePersist
    public void beforePersist() {
        created = LocalDateTime.now();
    }

    public TransportState getTransportState() {
        return TransportStateJpaConverter.converter
            .convertToEntityAttribute(this.transportStateString);
    }

    public void setTransportState(TransportState transportState) {
        this.transportStateString = TransportStateJpaConverter.converter
            .convertToDatabaseColumn(transportState);
    }
}
