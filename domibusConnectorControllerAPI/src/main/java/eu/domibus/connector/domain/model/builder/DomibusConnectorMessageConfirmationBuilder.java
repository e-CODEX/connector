package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import org.apache.commons.lang3.ArrayUtils;


public final class DomibusConnectorMessageConfirmationBuilder {

    private DomibusConnectorEvidenceType evidenceType;
    private byte[] evidence;

    private DomibusConnectorMessageConfirmationBuilder() {
    }

    public static DomibusConnectorMessageConfirmationBuilder createBuilder() {
        return new DomibusConnectorMessageConfirmationBuilder();
    }

    public DomibusConnectorMessageConfirmationBuilder setEvidenceType(DomibusConnectorEvidenceType evidenceType) {
        this.evidenceType = evidenceType;
        return this;
    }

    public DomibusConnectorMessageConfirmationBuilder setEvidence(byte[] evidence) {
        this.evidence = evidence;
        return this;
    }

    public DomibusConnectorMessageConfirmation build() {
        if (evidence == null) {
            evidence = new byte[0];
            // throw new IllegalArgumentException("Evidence is not allowed to be null!");
        }
        if (evidenceType == null) {
            throw new IllegalArgumentException("Evidence type must be set!");
        }
        return new DomibusConnectorMessageConfirmation(evidenceType, evidence);
    }

    public DomibusConnectorMessageConfirmationBuilder copyPropertiesFrom(DomibusConnectorMessageConfirmation c) {
        if (c == null) {
            throw new IllegalArgumentException("Cannot copy properties from null object!");
        }
        this.evidence = ArrayUtils.clone(c.getEvidence());
        this.evidenceType = c.getEvidenceType();
        return this;
    }
}
