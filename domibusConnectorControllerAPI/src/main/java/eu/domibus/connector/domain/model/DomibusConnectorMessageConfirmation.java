package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Arrays;


/**
 * This is an object that internally represents the evidences for a message. It
 * contains the evidence itself as a byte[] containing a structured document, and
 * an enum type {@link DomibusConnectorEvidenceType} which describes the evidence type. To be able
 * to connect the confirmation to a message it should be instantiated and added to
 * the {@link DomibusConnectorMessage} object.
 *
 * @author riederb
 * @version 1.0
 */
public class DomibusConnectorMessageConfirmation implements Serializable {
    /**
     * Is null by default, will be set if the confirmation has been
     * stored to database.
     * Is used to identify the evidence for updating the transport state of the evidence
     */
    private @Nullable Long evidenceDbId = null;
    private DomibusConnectorEvidenceType evidenceType;
    private @Nullable byte []evidence;

    /**
     * @param evidenceType the evidenceType
     * @param evidence     evidence
     */
    public DomibusConnectorMessageConfirmation(DomibusConnectorEvidenceType evidenceType, @Nullable byte[] evidence) {
        this.evidenceType = evidenceType;
        this.evidence = evidence;
    }

    /**
     * @param evidenceType evidenceType
     */
    public DomibusConnectorMessageConfirmation(DomibusConnectorEvidenceType evidenceType) {
        this.evidenceType = evidenceType;
    }

    public DomibusConnectorMessageConfirmation() {

    }

    public DomibusConnectorEvidenceType getEvidenceType() {
        return this.evidenceType;
    }

    /**
     * @param evidenceType evidenceType
     */
    public void setEvidenceType(DomibusConnectorEvidenceType evidenceType) {
        this.evidenceType = evidenceType;
    }

    public @Nullable byte[] getEvidence() {
        return this.evidence;
    }

    /**
     * @param evidence evidence
     */
    public void setEvidence(@Nullable byte[] evidence) {
        this.evidence = evidence;
    }

    @Nullable
    public Long getEvidenceDbId() {
        return evidenceDbId;
    }

    public void setEvidenceDbId(@Nullable Long evidenceDbId) {
        this.evidenceDbId = evidenceDbId;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(evidence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomibusConnectorMessageConfirmation)) return false;

        DomibusConnectorMessageConfirmation that = (DomibusConnectorMessageConfirmation) o;

        return Arrays.equals(evidence, that.evidence);
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("evidenceType", this.evidenceType);
        return builder.toString();
    }
}
