/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.builder;

import eu.ecodex.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

/**
 * The DomibusConnectorMessageConfirmationBuilder class is responsible for building instances of
 * DomibusConnectorMessageConfirmation objects. It provides methods to set the evidence type and
 * evidence byte array, copy properties from an existing DomibusConnectorMessageConfirmation object,
 * and build the final DomibusConnectorMessageConfirmation object. It also performs necessary
 * validations before building the object.
 */
@NoArgsConstructor
public final class DomibusConnectorMessageConfirmationBuilder {
    public static DomibusConnectorMessageConfirmationBuilder createBuilder() {
        return new DomibusConnectorMessageConfirmationBuilder();
    }

    private DomibusConnectorEvidenceType evidenceType;
    private byte[] evidence;

    public DomibusConnectorMessageConfirmationBuilder setEvidenceType(
        DomibusConnectorEvidenceType evidenceType) {
        this.evidenceType = evidenceType;
        return this;
    }

    public DomibusConnectorMessageConfirmationBuilder setEvidence(byte[] evidence) {
        this.evidence = evidence;
        return this;
    }

    /**
     * Builds an instance of DomibusConnectorMessageConfirmation by setting the evidence type and
     * evidence.
     *
     * @return The built DomibusConnectorMessageConfirmation object.
     * @throws IllegalArgumentException if evidence type is not set
     */
    public DomibusConnectorMessageConfirmation build() {
        if (evidence == null) {
            evidence = new byte[0];
        }
        if (evidenceType == null) {
            throw new IllegalArgumentException("Evidence type must be set!");
        }
        return new DomibusConnectorMessageConfirmation(evidenceType, evidence);
    }

    /**
     * Copies the properties from a given DomibusConnectorMessageConfirmation object to the current
     * instance.
     *
     * @param c the DomibusConnectorMessageConfirmation object to copy properties from. Must not be
     *          null.
     * @return the updated DomibusConnectorMessageConfirmationBuilder instance
     * @throws IllegalArgumentException if the provided DomibusConnectorMessageConfirmation object
     *                                  is null
     */
    public DomibusConnectorMessageConfirmationBuilder copyPropertiesFrom(
        DomibusConnectorMessageConfirmation c) {
        if (c == null) {
            throw new IllegalArgumentException("Cannot copy properties from null object!");
        }
        this.evidence = ArrayUtils.clone(c.getEvidence());
        this.evidenceType = c.getEvidenceType();
        return this;
    }
}
