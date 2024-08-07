package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

/**
 * In the future the connector will supoort multiple UseCases, Backends, Gateways
 * This Entity describes a configuration set for message processing and will be used
 * to process the message
 */
@Entity
@Table(name = PDomibusConnectorMessageLane.TABLE_NAME)
public class PDomibusConnectorMessageLane {

    public static final String TABLE_NAME = "DC_MESSAGE_LANE";

    @Id
    @Column(name="ID")
    @TableGenerator(name = "seq" + TABLE_NAME,
            table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
            pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
            pkColumnValue = TABLE_NAME + ".ID",
            valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
            initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
            allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
    private Long id;

    @Column(name= "NAME", unique = true, nullable = false, length = 255)
    private DomibusConnectorBusinessDomain.BusinessDomainId name;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DC_MESSAGE_LANE_PROPERTY", joinColumns=@JoinColumn(name="DC_MESSAGE_LANE_ID", referencedColumnName = "ID"))
    @MapKeyColumn (name="PROPERTY_NAME", nullable = false)
    @Column(name="PROPERTY_VALUE", length = 2048)
    private Map<String, String> properties = new HashMap<String, String>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DomibusConnectorBusinessDomain.BusinessDomainId getName() {
        return name;
    }

    public void setName(DomibusConnectorBusinessDomain.BusinessDomainId name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
