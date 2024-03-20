package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.model.DetachedSignatureMimeType;

import javax.persistence.*;

import static eu.domibus.connector.persistence.model.PDomibusConnectorPersistenceModel.*;

@Entity
@Table(name = PDomibusConnectorDetachedSignature.TABLE_NAME)
public class PDomibusConnectorDetachedSignature {

    public static final String TABLE_NAME = "DC_MSGCNT_DETSIG";

    @Id
    @Column(name="ID")
    @TableGenerator(name = "seq" + TABLE_NAME,
            table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
            pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
            pkColumnValue = TABLE_NAME + ".ID",
            valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
            initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
            allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE_BULK)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
    private Long id;

//    @OneToOne(optional = false, orphanRemoval = true, fetch = FetchType.EAGER)
//    @JoinColumn(name = "CONTENT", referencedColumnName = "ID")
//    PDomibusConnectorMsgCont content;

    @Lob
    @Column(name = "SIGNATURE")
    private byte detachedSignature[];

    @Column(name = "SIGNATURE_NAME")
    private String detachedSignatureName;

    @Column(name = "SIGNATURE_TYPE")
    private DetachedSignatureMimeType mimeType;

//    public PDomibusConnectorMsgCont getContent() {
//        return content;
//    }
//
//    public void setContent(PDomibusConnectorMsgCont content) {
//        this.content = content;
//    }

    public byte[] getDetachedSignature() {
        return detachedSignature;
    }

    public void setDetachedSignature(byte[] detachedSignature) {
        this.detachedSignature = detachedSignature;
    }

    public String getDetachedSignatureName() {
        return detachedSignatureName;
    }

    public void setDetachedSignatureName(String detachedSignatureName) {
        this.detachedSignatureName = detachedSignatureName;
    }

    public DetachedSignatureMimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(DetachedSignatureMimeType mimeType) {
        this.mimeType = mimeType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
