/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model;

import eu.ecodex.connector.domain.enums.DomibusConnectorEvidenceType;
import java.io.Serializable;
import java.util.Arrays;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

/**
 * This is an object that internally represents the evidences for a message. It contains the
 * evidence itself as a byte[] containing a structured document, and an enum type
 * {@link DomibusConnectorEvidenceType} which describes the evidence type. To be able to connect the
 * confirmation to a message it should be instantiated and added to the
 * {@link DomibusConnectorMessage} object.
 *
 * @author riederb
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class DomibusConnectorMessageConfirmation implements Serializable {
    /**
     * Is null by default, will be set if the confirmation has been stored to database. Is used to
     * identify the evidence for updating the transport state of the evidence
     */
    private @Nullable Long evidenceDbId = null;
    private DomibusConnectorEvidenceType evidenceType;
    private @Nullable byte[] evidence;

    /**
     * Constructs a new instance of DomibusConnectorMessageConfirmation with the given evidence type
     * and evidence.
     *
     * @param evidenceType The type of evidence for the message confirmation. Must not be null.
     * @param evidence     The evidence for the message confirmation. May be null.
     */
    public DomibusConnectorMessageConfirmation(DomibusConnectorEvidenceType evidenceType,
                                               @Nullable byte[] evidence) {
        this.evidenceType = evidenceType;
        this.evidence = evidence;
    }

    /**
     * Constructs a new instance of DomibusConnectorMessageConfirmation with the given evidence
     * type. The evidence parameter is not set.
     *
     * @param evidenceType The type of evidence for the message confirmation. Must not be null.
     */
    public DomibusConnectorMessageConfirmation(DomibusConnectorEvidenceType evidenceType) {
        this.evidenceType = evidenceType;
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("evidenceType", this.evidenceType);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DomibusConnectorMessageConfirmation that)) {
            return false;
        }

        return Arrays.equals(evidence, that.evidence);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(evidence);
    }
}
