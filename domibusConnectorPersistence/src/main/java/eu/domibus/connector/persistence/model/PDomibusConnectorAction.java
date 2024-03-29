package eu.domibus.connector.persistence.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = PDomibusConnectorAction.TABLE_NAME)
public class PDomibusConnectorAction implements Serializable {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_ACTION";

    @Id
    @Column(name = "ID")
    @TableGenerator(
            name = "seq" + TABLE_NAME, table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
            pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME, pkColumnValue = TABLE_NAME + ".ID",
            valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
            initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
            allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
    private Long id;
    @Column(name = "ACTION", nullable = false)
    private String action;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PMODE_SET", referencedColumnName = "ID")
    private PDomibusConnectorPModeSet pModeSet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PDomibusConnectorPModeSet getpModeSet() {
        return pModeSet;
    }

    public void setpModeSet(PDomibusConnectorPModeSet pModeSet) {
        this.pModeSet = pModeSet;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("action", action);
        return toString.build();
    }
}
