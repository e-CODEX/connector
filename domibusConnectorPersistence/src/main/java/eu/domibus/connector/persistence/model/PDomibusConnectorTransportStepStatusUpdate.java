package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.persistence.model.converter.TransportStateJpaConverter;
import org.hibernate.annotations.CreationTimestamp;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.time.LocalDateTime;


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
    //does not work because it is part of ID!
    //instead convert within setter/getter!
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

    public PDomibusConnectorTransportStep getTransportStep() {
        return transportStep;
    }

    public void setTransportStep(PDomibusConnectorTransportStep transportStep) {
        this.transportStep = transportStep;
    }

    public TransportState getTransportState() {
        return TransportStateJpaConverter.converter
                .convertToEntityAttribute(this.transportStateString);
    }

    public void setTransportState(TransportState transportState) {
        this.transportStateString = TransportStateJpaConverter.converter
                .convertToDatabaseColumn(transportState);
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
